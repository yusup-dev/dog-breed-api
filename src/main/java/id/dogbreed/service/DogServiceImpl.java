package id.dogbreed.service;

import id.dogbreed.dto.DogRequest;
import id.dogbreed.dto.DogResponse;
import id.dogbreed.exception.ResourceNotFoundException;
import id.dogbreed.mapper.DogMapper;
import id.dogbreed.model.Dog;
import id.dogbreed.repository.DogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DogServiceImpl implements DogService {

    private final DogRepository dogRepository;
    private final DogMapper dogMapper;

    @Override
    public List<DogResponse> findAll() {
        return dogRepository.findAll()
                .stream()
                .map(dogMapper::toResponse)
                .toList();
    }

    @Override
    public DogResponse findById(Long id) {
        return dogRepository.findById(id)
                .map(dogMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dog with ID " + id + " was not found"));    }

    @Override
    @Transactional
    public DogResponse create(DogRequest request) {
        Dog saved = dogRepository.save(dogMapper.toEntity(request));
        log.info("Dog created: id={}", saved.getId());
        return dogMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public DogResponse update(Long id, DogRequest request) {
        Dog dog = findDogOrThrow(id);
        dogMapper.updateEntity(dog, request);
        Dog updated = dogRepository.save(dog);
        log.info("Dog updated: id={}", id);
        return dogMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Dog dog = findDogOrThrow(id);
        dogRepository.delete(dog);
        log.info("Dog deleted: id={}", id);
    }

    private Dog findDogOrThrow(Long id) {
        return dogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dog not found with ID: " + id));
    }
}
