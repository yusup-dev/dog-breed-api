package id.dogbreed.service.strategy;

import java.util.List;
import java.util.Map;

public interface BreedProcessingStrategy {

    void process(Map<String, List<String>> result, String breed, List<String> subBreeds);

    String supportedBreed();
}
