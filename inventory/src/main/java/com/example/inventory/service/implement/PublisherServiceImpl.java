package com.example.inventory.service.implement;

import com.example.inventory.dto.PublisherDto;
import com.example.inventory.entity.Publisher;
import com.example.inventory.exception.notfound.PublisherNotFoundException;
import com.example.inventory.repository.PublisherRepository;
import com.example.inventory.service.PublisherService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherServiceImpl(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Override
    public PublisherDto createPublisher(PublisherDto publisherDto) {
        Publisher publisher = convertToEntity(publisherDto);
        Publisher savedPublisher = publisherRepository.save(publisher);
        return convertToDto(savedPublisher);
    }

    @Override
    public PublisherDto getPublisherById(Integer id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new PublisherNotFoundException(id));
        return convertToDto(publisher);
    }

    @Override
    public List<PublisherDto> getAllPublishers() {
        return publisherRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<PublisherDto> searchPublishers(String keyword) {
        List<Publisher> publishers = publisherRepository.findByPublisherNameContainingIgnoreCase(keyword);
        return publishers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PublisherDto updatePublisher(Integer id, PublisherDto publisherDto) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new PublisherNotFoundException(id));
        publisher.setPublisherName(publisherDto.getPublisherName());
        Publisher updatedPublisher = publisherRepository.save(publisher);
        return convertToDto(updatedPublisher);
    }

    @Override
    public void deletePublisher(Integer id) {
        if (!publisherRepository.existsById(id)) {
            throw new PublisherNotFoundException(id);
        }
        publisherRepository.deleteById(id);
    }

    private Publisher convertToEntity(PublisherDto publisherDto) {
        Publisher publisher = new Publisher();
        publisher.setPublisherId(publisherDto.getPublisherId());
        publisher.setPublisherName(publisherDto.getPublisherName());
        return publisher;
    }

    private PublisherDto convertToDto(Publisher publisher) {
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setPublisherId(publisher.getPublisherId());
        publisherDto.setPublisherName(publisher.getPublisherName());
        return publisherDto;
    }
}