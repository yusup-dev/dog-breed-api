package id.dogbreed.service.strategy;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class DefaultBreedProcessor implements BreedProcessingStrategy {

    public static final String DEFAULT_BREED = "__default__";

    @Override
    public String supportedBreed() {
        return DEFAULT_BREED;
    }

    @Override
    public void process(Map<String, List<String>> result, String breed, List<String> subBreeds) {
        result.put(breed, Collections.emptyList());
    }
}
