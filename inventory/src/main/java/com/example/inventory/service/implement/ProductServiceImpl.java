package com.example.inventory.service.implement;

import com.example.inventory.dto.ProductDto;
import com.example.inventory.entity.Product;
import com.example.inventory.entity.Author;
import com.example.inventory.entity.Category;
import com.example.inventory.entity.Publisher;
import com.example.inventory.exception.BadRequestException;
import com.example.inventory.exception.notfound.AuthorNotFoundException;
import com.example.inventory.exception.notfound.ProductNotFoundException;
import com.example.inventory.exception.notfound.PublisherNotFoundException;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.repository.AuthorRepository;
import com.example.inventory.repository.InventoryMovementRepository;
import com.example.inventory.repository.PublisherRepository;
import com.example.inventory.service.ProductService;

import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
        private final InventoryMovementRepository inventoryMovementRepository;


    public ProductServiceImpl(ProductRepository productRepository,
                              AuthorRepository authorRepository,
                              PublisherRepository publisherRepository,
                              InventoryMovementRepository inventoryMovementRepository) {
        this.productRepository = productRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
    }

    @Override
    public void createProduct(ProductDto productDto) {
        productDto.setProductId(null);
        Product product = convertToEntity(productDto);
    try {
        productRepository.save(product);
    } catch (DataIntegrityViolationException e) {
        String errorMessage = e.getMessage();
        if (errorMessage != null && errorMessage.contains("Duplicate entry")) {
            if (errorMessage.contains("for key 'book.isbn'")) {
                throw new BadRequestException("ISBN already exists");
            }
        }
        throw new BadRequestException("Data integrity violation: " + errorMessage);
    } catch (TransactionSystemException e) {
        throw new BadRequestException("Transaction system error: " + e.getMessage());
    }
    }

    @Override
    public ProductDto getProductById(Integer id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return convertToDto(product);
    }

    @Override
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        Pageable sortedByUpdatedAtDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "updatedAt"));
        return productRepository.findAll(sortedByUpdatedAtDesc).map(this::convertToDto);   
    }

    @Override
    public Page<ProductDto> getProductsByCategory(Category category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable).map(this::convertToDto);
    }

    @Override
    public Page<ProductDto> searchProductsByTitle(String keyword, Pageable pageable) {
        Pageable sortedByUpdatedAtDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Product> productsPage = productRepository.findByTitleContainingIgnoreCase(keyword, sortedByUpdatedAtDesc);
        return productsPage.map(this::convertToDto);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Integer id, ProductDto productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    
        existingProduct.setTitle(productDto.getTitle());
        existingProduct.setIsbn(productDto.getIsbn());
        existingProduct.setPublishedDate(productDto.getPublishedDate());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setQuantity(productDto.getQuantity());
        existingProduct.setCategory(productDto.getCategory());
        existingProduct.setDescription(productDto.getDescription());
    
        Integer authorId = productDto.getAuthorId();
        if (authorId != null) {
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new AuthorNotFoundException(authorId));
            existingProduct.setAuthor(author);
        }
    
        Integer publisherId = productDto.getPublisherId();
        if (publisherId != null) {
            Publisher publisher = publisherRepository.findById(publisherId)
                    .orElseThrow(() -> new PublisherNotFoundException(publisherId));
            existingProduct.setPublisher(publisher);
        }
    
        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDto(updatedProduct);
    }      
    

    @Override
    @Transactional
    public void deleteProduct(Integer id){
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        inventoryMovementRepository.deleteByProduct_ProductId(id);
        productRepository.deleteById(id);
    }

    private Product convertToEntity(ProductDto productDto) {
        Product product = new Product();
        product.setProductId(productDto.getProductId());
        product.setTitle(productDto.getTitle());
        product.setPublishedDate(productDto.getPublishedDate());
        product.setIsbn(productDto.getIsbn());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        product.setDescription(productDto.getDescription());
        product.setCategory(productDto.getCategory());

        setProductRelationships(product, productDto);

        return product;
    }

    private void setProductRelationships(Product product, ProductDto productDto) {
        Author author = authorRepository.findById(productDto.getAuthorId())
                .orElseThrow(() -> new AuthorNotFoundException(productDto.getAuthorId()));
        product.setAuthor(author);

        Publisher publisher = publisherRepository.findById(productDto.getPublisherId())
                .orElseThrow(() -> new PublisherNotFoundException(productDto.getPublisherId()));
        product.setPublisher(publisher);
    }

    private ProductDto convertToDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setProductId(product.getProductId());
        productDto.setTitle(product.getTitle());
        productDto.setPublishedDate(product.getPublishedDate());
        productDto.setIsbn(product.getIsbn());
        productDto.setPrice(product.getPrice());
        productDto.setQuantity(product.getQuantity());
        productDto.setDescription(product.getDescription());
        productDto.setCategory(product.getCategory());
        
        productDto.setAuthorId(product.getAuthor().getAuthorId());
        productDto.setAuthorName(product.getAuthor().getAuthorName());
        productDto.setBio(product.getAuthor().getBio());
    
        productDto.setPublisherId(product.getPublisher().getPublisherId());
        productDto.setPublisherName(product.getPublisher().getPublisherName());
        return productDto;
    }
}
