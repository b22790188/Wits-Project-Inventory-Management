package com.example.inventory.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "book")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Integer productId;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @Temporal(TemporalType.DATE)
    @Column(name = "published_date")
    private Date publishedDate;

    @Column(unique = true, length = 20)
    private String isbn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
