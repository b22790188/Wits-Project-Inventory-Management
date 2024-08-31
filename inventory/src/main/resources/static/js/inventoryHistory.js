let currentPage = 0;
const pageSize = 10;
const urlParams = new URLSearchParams(window.location.search);
const productId = urlParams.get('productId');

document.addEventListener("DOMContentLoaded", async function() {
    if (productId) {
        try {
            const productDetails = await getProductDetails(productId);
            document.getElementById('productTitle').textContent = `${productDetails.title} 庫存歷史紀錄`;
            document.getElementById('currentStock').textContent = `目前庫存: ${productDetails.quantity}`;
            await loadInventoryHistory(productId);
        } catch (error) {
            console.error('錯誤:', error);
            alert('無法加載產品詳情，請稍後重試。');
            window.history.back();
        }
    } else {
        alert('無法找到產品 ID');
        window.history.back();
    }
});

async function getProductDetails(productId) {
    const accessToken = localStorage.getItem('access_token');
    const response = await apiFunction(`/api/1.0/products/details?id=${productId}`, 'GET', accessToken);
    return response.data;
}

async function loadInventoryHistory(productId) {
    const accessToken = localStorage.getItem('access_token');
    const response = await apiFunction(`/api/1.0/inventory/product?productId=${productId}&page=${currentPage}&size=${pageSize}`, 'GET', accessToken);
    updateCurrentStock();
    displayInventoryHistory(response.data.content);
    updatePagination(response.data);
}

function displayInventoryHistory(history) {
    const historyList = document.getElementById('inventoryHistoryList');
    historyList.innerHTML = '';

    history.forEach(record => {
        const movementType = record.movement_type === 'IN' ? '進貨' : '銷貨';
        const rowClass = record.movement_type === 'IN' ? 'in-row' : 'out-row';

        const row = document.createElement('tr');
        row.className = rowClass;
        row.innerHTML = `
            <td>${movementType}</td>
            <td>${new Date(record.movement_date).toLocaleDateString()}</td>
            <td>${record.quantity}</td>
            <td>${record.remarks}</td>
            <td>
                <button class="edit-button" onclick="openEditInventoryModal(${record.movement_id}, '${record.movement_type}', ${record.quantity}, '${record.remarks}', '${record.movement_date}')">編輯</button>
                <button class="delete-button" onclick="deleteInventory(${record.movement_id})">刪除</button>
            </td>
        `;
        historyList.appendChild(row);
    });
}

function changePage(direction) {
    if (direction === 'next') {
        currentPage++;
    } else if (direction === 'prev' && currentPage > 0) {
        currentPage--;
    }
    loadInventoryHistory(productId);
}

function updatePagination(pageData) {
    document.getElementById('prevPage').disabled = pageData.first;
    document.getElementById('nextPage').disabled = pageData.last;
}

function openEditInventoryModal(movementId, movementType, quantity, remarks, movementDate) {
    document.getElementById('editInventoryId').value = movementId;
    document.getElementById('editMovementType').value = movementType;
    document.getElementById('editQuantity').value = quantity;
    document.getElementById('editRemarks').value = remarks;
    document.getElementById('editDate').value = movementDate.split('T')[0];
    document.getElementById('editInventoryModal').style.display = 'flex';
}

function closeEditInventoryModal() {
    document.getElementById('editInventoryModal').style.display = 'none';
    document.getElementById('editInventoryId').value = '';
    document.getElementById('editMovementType').value = '';
    document.getElementById('editQuantity').value = '';
    document.getElementById('editRemarks').value = '';
    document.getElementById('editDate').value = '';
}

async function editInventory(event) {
    event.preventDefault();

    const accessToken = localStorage.getItem('access_token');
    const movementId = document.getElementById('editInventoryId').value;
    const inventoryData = {
        product_id: productId,
        movement_type: document.getElementById('editMovementType').value,
        quantity: document.getElementById('editQuantity').value,
        remarks: document.getElementById('editRemarks').value,
        movement_date: document.getElementById('editDate').value
    };

    try {
        await apiFunction(`/api/1.0/inventory?id=${movementId}`, 'PUT', accessToken, inventoryData);
        alert('庫存編輯成功！');
        closeEditInventoryModal();
        await loadInventoryHistory(productId);
    } catch (error) {
        console.error('錯誤:', error);
        alert('無法編輯庫存，請稍後重試。');
    }
}

async function deleteInventory(movementId) {
    const accessToken = localStorage.getItem('access_token');

    if (!confirm('確定要刪除此庫存記錄嗎？')) return;

    try {
        await apiFunction(`/api/1.0/inventory?id=${movementId}`, 'DELETE', accessToken);
        alert('庫存刪除成功！');
        await loadInventoryHistory(productId);
    } catch (error) {
        console.error('錯誤:', error);
        alert('無法刪除庫存，請稍後重試。');
    }
}

function openInventoryModal(productId, movementType) {
    document.getElementById('inventoryProductId').value = productId;
    document.getElementById('movementType').value = movementType;
    document.getElementById('movementQuantity').value = '';
    document.getElementById('movementDate').value = new Date().toISOString().split('T')[0];
    document.getElementById('inventoryModalTitle').textContent = movementType === 'IN' ? '進貨操作' : '銷貨操作';
    document.getElementById('inventoryModal').style.display = 'flex';
}

function closeInventoryModal() {
    document.getElementById('inventoryModal').style.display = 'none';
    document.getElementById('inventoryProductId').value = '';
    document.getElementById('movementType').value = '';
    document.getElementById('movementQuantity').value = '';
    document.getElementById('movementDate').value = '';
    document.getElementById('movementRemarks').value = '';
}

async function handleInventoryMovement(event) {
    event.preventDefault();

    const accessToken = localStorage.getItem('access_token');
    const movementData = {
        product_id: document.getElementById('inventoryProductId').value,
        movement_type: document.getElementById('movementType').value,
        quantity: document.getElementById('movementQuantity').value,
        remarks: document.getElementById('movementRemarks').value,
        movement_date: document.getElementById('movementDate').value
    };

    try {
        await apiFunction('/api/1.0/inventory', 'POST', accessToken, movementData);
        alert('庫存操作成功！');
        closeInventoryModal();
        await loadInventoryHistory(productId);
    } catch (error) {
        console.error('錯誤:', error);
        alert('無法進行庫存操作，請稍後重試。');
    }
}

function getCurrentProductId() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('productId');
}

async function apiFunction(url, method, token, data = null) {
    const headers = {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    };
    const options = {
        method: method,
        headers: headers
    };
    if (data) {
        options.body = JSON.stringify(data);
    }

    const response = await fetch(url, options);

    if (!response.ok) {
        handleAuthError(response.status);
        throw new Error(response.status);
    }

    if (method === 'DELETE') {
        return;
    }

    return response.json();
}

function handleAuthError(status) {
    if (status === 401 || status === 403) {
        alert('授權失敗，請重新登入。');
        localStorage.removeItem('access_token');
        window.location.href = '/signin.html';
    }
}

async function updateCurrentStock() {
    try {
        const productDetails = await getProductDetails(productId);
        document.getElementById('currentStock').textContent = `目前庫存: ${productDetails.quantity}`;
    } catch (error) {
        console.error('錯誤:', error);
        alert('無法更新目前庫存，請稍後重試。');
    }
}