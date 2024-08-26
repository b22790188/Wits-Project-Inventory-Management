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

    @Override
    public ProductDto getProductById(Integer id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return convertToDto(product);
    }

    @Override
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::convertToDto);
    }

    @Override
    public Page<ProductDto> getProductsByCategory(Category category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable).map(this::convertToDto);
    }

    @Override
    public Page<ProductDto> searchProductsByTitle(String keyword, Pageable pageable) {
        Page<Product> productsPage = productRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        return productsPage.map(this::convertToDto);
    }

    @Override
    @Transactional
    public ProductDto updateProductPartially(Integer id, Map<String, Object> updates) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        updates.forEach((key, value) -> {
            String camelCaseKey = convertToCamelCase(key);

            Field field = ReflectionUtils.findField(Product.class, camelCaseKey);
            if (field == null) {
                throw new BadRequestException("Field '" + key + "' does not exist in Product class.");
            }
            field.setAccessible(true);
            Object convertedValue = convertValue(field, value);
            ReflectionUtils.setField(field, existingProduct, convertedValue);
        });

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Integer id){
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product with id " + id + " not found");
        }
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
        productDto.setPublisherId(product.getPublisher().getPublisherId());

        return productDto;
    }

    private Object convertValue(Field field, Object value) {
        Class<?> fieldType = field.getType();
    
        if (fieldType.equals(BigDecimal.class)) {
            return new BigDecimal(value.toString());
        } else if (fieldType.equals(Integer.class) && value instanceof Number) {
            return ((Number) value).intValue();
        } else if (fieldType.equals(Double.class) && value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (fieldType.equals(Float.class) && value instanceof Number) {
            return ((Number) value).floatValue();
        } else if (fieldType.equals(Date.class) && value instanceof String) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                return formatter.parse((String) value);
            } catch (ParseException e) {
                throw new BadRequestException("Invalid date format for field: " + field.getName());
            }
        }
    
        return value;
    }

    private String convertToCamelCase(String snakeCase) {
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
    
        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                nextUpperCase = true;
            } else if (nextUpperCase) {
                result.append(Character.toUpperCase(c));
                nextUpperCase = false;
            } else {
                result.append(c);
            }
        }
    
        return result.toString();
    }
    
}
