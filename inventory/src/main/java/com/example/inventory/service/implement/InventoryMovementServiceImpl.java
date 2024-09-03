package com.example.inventory.service.implement;

import com.example.inventory.dto.InventoryMovementDto;
import com.example.inventory.entity.InventoryMovement;
import com.example.inventory.entity.Product;
import com.example.inventory.entity.MovementType;
import com.example.inventory.exception.notfound.InventoryMovementNotFoundException;
import com.example.inventory.exception.notfound.ProductNotFoundException;
import com.example.inventory.repository.InventoryMovementRepository;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.service.InventoryMovementService;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class InventoryMovementServiceImpl implements InventoryMovementService {

    private final InventoryMovementRepository inventoryMovementRepository;
    private final ProductRepository productRepository;

    public InventoryMovementServiceImpl(InventoryMovementRepository inventoryMovementRepository,
                                        ProductRepository productRepository) {
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Page<InventoryMovementDto> getAllInventoryMovements(Pageable pageable) {
        Pageable sortedByDateDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "movementDate"));
        return inventoryMovementRepository.findAll(sortedByDateDesc).map(this::convertToDto);
    }

    @Override
    public Page<InventoryMovementDto> getInventoryMovementsByProductId(Integer productId, Pageable pageable) {
        Pageable sortedByDateDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "movementDate"));
        return inventoryMovementRepository.findByProduct_ProductId(productId, sortedByDateDesc).map(this::convertToDto);
    }

    @Override
    public InventoryMovementDto getInventoryMovementById(Integer id) {
        InventoryMovement inventoryMovement = inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new InventoryMovementNotFoundException(id));
        return convertToDto(inventoryMovement);
    }

    @Override
    @Transactional
    public InventoryMovementDto createInventoryMovement(InventoryMovementDto inventoryMovementDto) {
        InventoryMovement inventoryMovement = convertToEntity(inventoryMovementDto);
        
        updateProductQuantity(inventoryMovement.getProduct(), inventoryMovement.getMovementType(), inventoryMovement.getQuantity());
        
        InventoryMovement savedMovement = inventoryMovementRepository.save(inventoryMovement);
        return convertToDto(savedMovement);
    }

    @Override
    @Transactional
    public InventoryMovementDto updateInventoryMovement(Integer id, InventoryMovementDto inventoryMovementDto) {
        InventoryMovement existingMovement = inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new InventoryMovementNotFoundException(id));

        revertProductQuantity(existingMovement.getProduct(), existingMovement.getMovementType(), existingMovement.getQuantity());

        existingMovement.setMovementType(inventoryMovementDto.getMovementType());
        existingMovement.setQuantity(inventoryMovementDto.getQuantity());
        existingMovement.setRemarks(inventoryMovementDto.getRemarks());
        existingMovement.setMovementDate(inventoryMovementDto.getMovementDate());

        Product product = productRepository.findById(inventoryMovementDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(inventoryMovementDto.getProductId()));
        existingMovement.setProduct(product);

        updateProductQuantity(product, existingMovement.getMovementType(), existingMovement.getQuantity());

        InventoryMovement updatedMovement = inventoryMovementRepository.save(existingMovement);
        return convertToDto(updatedMovement);
    }

    @Override
    @Transactional
    public void deleteInventoryMovement(Integer id) {
        InventoryMovement existingMovement = inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new InventoryMovementNotFoundException(id));

        revertProductQuantity(existingMovement.getProduct(), existingMovement.getMovementType(), existingMovement.getQuantity());

        inventoryMovementRepository.deleteById(id);
    }

    private InventoryMovementDto convertToDto(InventoryMovement inventoryMovement) {
        InventoryMovementDto dto = new InventoryMovementDto();
        dto.setMovementId(inventoryMovement.getMovementId());
        dto.setProductId(inventoryMovement.getProduct().getProductId());
        dto.setMovementType(inventoryMovement.getMovementType());
        dto.setQuantity(inventoryMovement.getQuantity());
        dto.setRemarks(inventoryMovement.getRemarks());
        dto.setMovementDate(inventoryMovement.getMovementDate());
        dto.setSumQuantity(inventoryMovement.getProduct().getQuantity());
        return dto;
    }

    private InventoryMovement convertToEntity(InventoryMovementDto inventoryMovementDto) {
        InventoryMovement inventoryMovement = new InventoryMovement();
        Product product = productRepository.findById(inventoryMovementDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(inventoryMovementDto.getProductId()));
        inventoryMovement.setProduct(product);
        inventoryMovement.setMovementType(inventoryMovementDto.getMovementType());
        inventoryMovement.setQuantity(inventoryMovementDto.getQuantity());
        inventoryMovement.setRemarks(inventoryMovementDto.getRemarks());
        inventoryMovement.setMovementDate(inventoryMovementDto.getMovementDate());
        return inventoryMovement;
    }

    private void updateProductQuantity(Product product, MovementType movementType, Integer quantity) {
        synchronized (product) {
            if (movementType == MovementType.IN) {
                product.setQuantity(product.getQuantity() + quantity);
            } else if (movementType == MovementType.OUT) {
                product.setQuantity(product.getQuantity() - quantity);
            }
            productRepository.save(product);
        }
    }

    private void revertProductQuantity(Product product, MovementType movementType, Integer quantity) {
        synchronized (product) {
            if (movementType == MovementType.IN) {
                product.setQuantity(product.getQuantity() - quantity);
            } else if (movementType == MovementType.OUT) {
                product.setQuantity(product.getQuantity() + quantity);
            }
            productRepository.save(product);
        }
    }
}