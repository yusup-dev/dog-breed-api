package id.dogbreed.service;

import id.dogbreed.dto.DogRequest;
import id.dogbreed.dto.DogResponse;

import java.util.List;

public interface DogService {

    List<DogResponse> findAll();

    DogResponse findById(Long id);

    DogResponse create(DogRequest request);

    DogResponse update(Long id, DogRequest request);

    void delete(Long id);
}
