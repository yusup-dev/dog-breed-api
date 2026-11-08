package id.dogbreed.service;

import java.util.List;
import java.util.Map;

public interface DogBreedService {

    Map<String, List<String>> listAllBreeds();

    List<String> getShibaOddImages();
}
