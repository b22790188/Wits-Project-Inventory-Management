let currentPage = 0;
const pageSize = 10;
let currentProductId = null;
let isEditMode = false;

const categoryMapping = {
    'LITERATURE_FICTION': '文學小說',
    'BUSINESS_INVESTING': '商業投資',
    'ART_DESIGN': '藝術設計',
    'HUMANITIES_SOCIAL_SCIENCES': '人文社科',
    'PSYCHOLOGY_MOTIVATION': '心理勵志',
    'NATURAL_SCIENCE': '自然科學',
    'CHILDREN_YOUNG_ADULT': '兒童與青少年',
    'COMPUTERS_TECHNOLOGY': '電腦與科技',
    'COMICS_GRAPHIC_NOVELS': '漫畫與圖像小說',
    'LIFESTYLE': '生活風格',
    'OTHER': '其他'
};

document.addEventListener("DOMContentLoaded", function() {
    loadProducts();
    initializeAutocomplete("#author_search", "/api/1.0/author", "author_id", "author_name");
    initializeAutocomplete("#publisher_search", "/api/1.0/publisher", "publisher_id", "publisher_name");
});

async function apiRequest(url, method, data = null) {
    const accessToken = localStorage.getItem('access_token');
    const response = await fetch(url, {
        method: method,
        headers: {
            'Authorization': `Bearer ${accessToken}`,
            'Content-Type': 'application/json'
        },
        body: data ? JSON.stringify(data) : null
    });

    await handleAuthError(response);

    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || '發生錯誤');
    }

    if (method === 'DELETE') {
        return;
    }
    
    return response.json();
}

async function handleAuthError(response) {
    if (response.status === 403) {
        alert("登入過時，請重新登入");
        window.location.href = "/signin.html";
        throw new Error('Unauthorized');
    }
    return response;
}

function clearSearchProducts(){
    document.getElementById('search_input').value = "";
    loadProducts();
}

async function loadProducts(keyword = '') {
    const url = keyword ? `/api/1.0/products/search?keyword=${keyword}&page=${currentPage}&size=${pageSize}` : `/api/1.0/products/all?page=${currentPage}&size=${pageSize}`;
    
    try {
        const data = await apiRequest(url, 'GET');
        displayProducts(data.data.content);
        updatePagination(data.data);
    } catch (error) {
        console.error('錯誤:', error);
    }
}

function displayProducts(products) {
    const productList = document.getElementById('product_list');
    productList.innerHTML = '';

    if(products.length == 0){
        const noResultsRow = document.createElement('tr');
        const noResultsCell = document.createElement('td');
        noResultsCell.colSpan = 8;
        noResultsCell.textContent = '無符合的產品';
        noResultsCell.style.textAlign = 'center';
        noResultsRow.appendChild(noResultsCell);
        productList.appendChild(noResultsRow);
    }else{
        products.forEach(product => {
            const categoryName = categoryMapping[product.category] || '其他';
            const row = document.createElement('tr');

            row.addEventListener('click', function(event) {
                if (!event.target.closest('button')) {
                    window.location.href = `/inventoryHistory.html?productId=${product.product_id}`;
                }
            });

            row.innerHTML = `
                <td>${product.isbn}</td>
                <td>${product.title}</td>
                <td>${product.author_name}</td>
                <td>${product.publisher_name}</td>
                <td>${categoryName}</td>
                <td>$ ${product.price}</td>
                <td>${product.quantity}</td>
                <td>
                    <button class="add-button" onclick="openInventoryModal(${product.product_id}, 'IN', '${product.title}')">進貨</button>
                    <button class="sell-button" onclick="openInventoryModal(${product.product_id}, 'OUT', '${product.title}')">銷貨</button>
                    <button class="edit-button" onclick="openProductModal('edit', ${product.product_id})">編輯</button>
                    <button class="delete-button" onclick="confirmDelete(${product.product_id}, '${product.title}')">刪除</button>
                </td>
            `;
            productList.appendChild(row);
        });
    }
}

function searchProducts() {
    const keyword = document.getElementById('search_input').value.trim();
    currentPage = 0;
    loadProducts(keyword);
}

function changePage(direction) {
    if (direction === 'next') {
        currentPage++;
    } else if (direction === 'prev' && currentPage > 0) {
        currentPage--;
    }
    const keyword = document.getElementById('search_input').value.trim();
    loadProducts(keyword);
}

function updatePagination(pageData) {
    document.getElementById('prev_page').disabled = pageData.first;
    document.getElementById('next_page').disabled = pageData.last;
}

function handleKeyDown(event) {
    if (event.isComposing || event.keyCode === 229) {
        return;
    }
    if (event.key === 'Enter') {
        searchProducts();
    }
}

function openModal(modalId, title, titleText) {
    if(title != null){
        document.getElementById(title).textContent = titleText;
    }
    document.getElementById(modalId).style.display = 'flex';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

function resetForm(formId) {
    document.getElementById(formId).reset();
}

function openProductModal(mode, productId = null) {
    isEditMode = (mode === 'edit');
    currentProductId = productId;
    document.getElementById('modal_title').textContent = isEditMode ? '編輯產品' : '新增產品';
    document.getElementById('modal_submit_button').textContent = isEditMode ? '保存變更' : '新增產品';

    if (isEditMode && productId) {
        loadProductData(productId);
    } else {
        resetForm('product_form');
    }

    openModal('product_modal', 'modal_title', isEditMode ? '編輯產品' : '新增產品');
}

function closeProductModal() {
    closeModal('product_modal');
}

async function loadProductData(productId) {
    try {
        const data = await apiRequest(`/api/1.0/products/details?id=${productId}`, 'GET');
        const product = data.data;

        $("#title").val(product.title);
        $("#isbn").val(product.isbn);
        $("#published_date").val(product.published_date);
        $("#price").val(product.price);
        $("#category").val(product.category);
        $("#description").val(product.description);
        $("#quantity").val(product.quantity);
        $("#author_search").val(product.author_name);
        $("#author_id").val(product.author_id);
        $("#publisher_search").val(product.publisher_name);
        $("#publisher_id").val(product.publisher_id);
    } catch (error) {
        console.error('錯誤:', error);
    }
}

function handleSubmit(event) {
    event.preventDefault();
    if (isEditMode) {
        updateProduct();
    } else {
        addProduct();
    }
}

async function addProduct() {
    const productData = getProductFormData();
        productData.quantity = 0;

    try {
        const data = await apiRequest('/api/1.0/products', 'POST', productData);
        alert('成功新增產品！');
        closeProductModal();
        loadProducts();
    } catch (error) {
        console.error('錯誤:', error);
        alert("新增產品失敗 " + error.message);

    }
}

async function updateProduct() {
    const productData = getProductFormData();
    try {
        const data = await apiRequest(`/api/1.0/products?id=${currentProductId}`, 'PUT', productData);
        alert('成功更新產品！');
        closeProductModal();
        loadProducts();
    } catch (error) {
        console.error('錯誤:', error);
        alert("更新產品失敗 " + error.message);
    }
}

function getProductFormData() {
    return {
        title: document.getElementById('title').value,
        isbn: document.getElementById('isbn').value,
        author_id: parseInt(document.getElementById('author_id').value, 10),
        publisher_id: parseInt(document.getElementById('publisher_id').value, 10),
        published_date: document.getElementById('published_date').value,
        price: parseFloat(document.getElementById('price').value),
        category: document.getElementById('category').value,
        description: document.getElementById('description').value,
        quantity: parseInt(document.getElementById('quantity').value, 10)
    };
}

function initializeAutocomplete(inputId, baseUrl, hiddenId, labelName) {
    $(inputId).autocomplete({
        source: async function(request, response) {
            let apiUrl = request.term.trim() === "" ? `${baseUrl}/all` : `${baseUrl}/search?keyword=${request.term}`;
            try {
                const data = await apiRequest(apiUrl, 'GET');
                response($.map(data.data, function(item) {
                    return {
                        label: item[labelName],
                        value: item[labelName],
                        id: item[hiddenId]
                    };
                }));
            } catch (error) {
                response([]);
            }
        },
        minLength: 0,
        select: function(event, ui) {
            $(`#${hiddenId}`).val(ui.item.id);
        }
    }).focus(function() {
        $(this).autocomplete("search", "");
    });
}

function setDefaultDate(inputId) {
    const today = new Date().toISOString().split('T')[0];
    $(inputId).val(today);
}

function openInventoryModal(productId, movementType, productTitle) {
    resetForm('inventory_form');
    document.getElementById('inventoryProductId').value = productId;
    document.getElementById('movementType').value = movementType;
    openModal('inventoryModal', 'inventoryModalTitle', movementType === 'IN' ? productTitle + ' 進貨操作' : productTitle + ' 銷貨操作');
    setDefaultDate('#movementDate');
}

function closeInventoryModal() {
    closeModal('inventoryModal');
}

async function handleInventoryMovement(event) {
    event.preventDefault();

    const inventoryData = {
        product_id: document.getElementById('inventoryProductId').value,
        movement_type: document.getElementById('movementType').value,
        quantity: document.getElementById('movementQuantity').value,
        remarks: document.getElementById('movementRemarks').value,
        movement_date: document.getElementById('movementDate').value
    };

    try {
        const data = await apiRequest('/api/1.0/inventory', 'POST', inventoryData);
        alert('庫存操作成功！');
        closeInventoryModal();
        loadProducts();
    } catch (error) {
        console.error('錯誤:', error);
        alert('無法操作庫存，請稍後重試。');
    }
}

function confirmDelete(productId, productTitle) {
    if (confirm(`確定要刪除 ${productTitle} 嗎？`)) {
        deleteProduct(productId);
    }
}

async function deleteProduct(productId) {
    try {
        await apiRequest(`/api/1.0/products?id=${productId}`, 'DELETE');
        alert('成功刪除產品！');
        loadProducts();
    } catch (error) {
        console.error('錯誤:', error);
        alert('刪除產品時出現錯誤。');
    }
}

function showAddAuthorModal() {
    resetForm('author_form');
    openModal('addAuthorModal', null, null);
}

function closeAddAuthorModal() {
    closeModal('addAuthorModal');
}

async function addAuthor(event) {
    event.preventDefault();
    const authorData = {
        author_name: $('#authorName').val(),
        bio: $('#authorBio').val()
    };
    try {
        const data = await apiRequest('/api/1.0/author', 'POST', authorData);
        alert('作者新增成功！');
        closeAddAuthorModal();
        $('#author_search').val(authorData.author_name);
        $('#author_id').val(data.data.author_id);
        $("#author_search").autocomplete("search", authorData.author_name);
    } catch (error) {
        console.error('錯誤:', error);
        alert('無法新增作者 ' + error.message);
    }
}

function showAddPublisherModal() {
    resetForm('publisher_form');
    openModal('addPublisherModal', null, null);
}

function closeAddPublisherModal() {
    closeModal('addPublisherModal');
}

async function addPublisher(event) {
    event.preventDefault();
    const publisherData = {
        publisher_name: $('#publisherName').val()
    };
    try {
        const data = await apiRequest('/api/1.0/publisher', 'POST', publisherData);
        alert('出版商新增成功！');
        closeAddPublisherModal();
        $('#publisher_search').val(publisherData.publisher_name);
        $('#publisher_id').val(data.data.publisher_id);
        $("#publisher_search").autocomplete("search", publisherData.publisher_name);
    } catch (error) {
        console.error('錯誤:', error);
        alert('無法新增出版商 ' + error.message);
    }
}

function getCurrentProductId() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('productId');
}