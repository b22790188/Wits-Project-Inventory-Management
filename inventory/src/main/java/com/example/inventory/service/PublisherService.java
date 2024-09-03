package com.example.inventory.service;

import com.example.inventory.dto.PublisherDto;

import java.util.List;

public interface PublisherService {

    PublisherDto createPublisher(PublisherDto publisherDto);

    PublisherDto getPublisherById(Integer id);

    List<PublisherDto> getAllPublishers();

    List<PublisherDto> searchPublishers(String keyword);

    PublisherDto updatePublisher(Integer id, PublisherDto publisherDto);

    void deletePublisher(Integer id);
}