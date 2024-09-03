package com.example.inventory.controller;

import com.example.inventory.dto.PublisherDto;
import com.example.inventory.dto.general.ApiResponse;
import com.example.inventory.service.PublisherService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/1.0/publisher")
@Validated
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PublisherDto>> createPublisher(@Valid @RequestBody PublisherDto publisherDto) {
        PublisherDto createdPublisher = publisherService.createPublisher(publisherDto);
        ApiResponse<PublisherDto> response = new ApiResponse<>(createdPublisher);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PublisherDto>>> getAllPublishers() {
        List<PublisherDto> publishers = publisherService.getAllPublishers();
        ApiResponse<List<PublisherDto>> response = new ApiResponse<>(publishers);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponse<PublisherDto>> getPublisherById(@RequestParam(required = true) @NotNull Integer id) {
        PublisherDto publisherDto = publisherService.getPublisherById(id);
        ApiResponse<PublisherDto> response = new ApiResponse<>(publisherDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PublisherDto>>> searchPublishers(@RequestParam String keyword) {
        List<PublisherDto> publishers = publisherService.searchPublishers(keyword);
        ApiResponse<List<PublisherDto>> response = new ApiResponse<>(publishers);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<PublisherDto>> updatePublisher(@RequestParam(required = true) @NotNull Integer id, @RequestBody PublisherDto publisherDto) {
        PublisherDto updatedPublisher = publisherService.updatePublisher(id, publisherDto);
        ApiResponse<PublisherDto> response = new ApiResponse<>(updatedPublisher);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePublisher(@RequestParam(required = true) @NotNull Integer id) {
        publisherService.deletePublisher(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}