package com.example.inventory.service.implement;

import com.example.inventory.dto.ProductDto;
import com.example.inventory.entity.Product;
import com.example.inventory.entity.Author;
import com.example.inventory.entity.Category;
import com.example.inventory.entity.Publisher;
import com.example.inventory.exception.AuthorNotFoundException;
import com.example.inventory.exception.BadRequestException;
import com.example.inventory.exception.ProductNotFoundException;
import com.example.inventory.exception.PublisherNotFoundException;
import com.example.inventory.exception.ResourceNotFoundException;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.repository.AuthorRepository;
import com.example.inventory.repository.PublisherRepository;
import com.example.inventory.service.ProductService;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import java.lang.reflect.Field;
import org.springframework.util.ReflectionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.data.domain.Pageable;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              AuthorRepository authorRepository,
                              PublisherRepository publisherRepository) {
        this.productRepository = productRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
    }

    @Override
    public void createProduct(ProductDto productDto) {
        productDto.setProductId(null);
        Product product = convertToEntity(productDto);
    try {
        productRepository.save(product);
    } catch (DataIntegrityViolationException e) {
        throw new BadRequestException("Data integrity violation: " + e.getMessage());
    } catch (TransactionSystemException e) {
        throw new BadRequestException("Transaction system error: " + e.getMessage());
    }
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
        productDto.setPublisherId(product.getPublisher().getPublisherId());

        return productDto;
    }
}
