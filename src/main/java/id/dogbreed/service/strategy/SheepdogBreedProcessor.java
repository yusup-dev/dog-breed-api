package id.dogbreed.service.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SheepdogBreedProcessor implements BreedProcessingStrategy {

    @Override
    public String supportedBreed() {
        return "sheepdog";
    }

    @Override
    public void process(Map<String, List<String>> result, String breed, List<String> subBreeds) {
        if (subBreeds == null || subBreeds.isEmpty()) {
            result.put(breed, Collections.emptyList());
            return;
        }
        subBreeds.forEach(sub -> result.put(breed + "-" + sub, Collections.emptyList()));
        log.debug("Sheepdog: {} sub-breeds promoted", subBreeds.size());
    }
}
