package id.dogbreed.service;

import id.dogbreed.client.DogApiClient;
import id.dogbreed.dto.BreedsAllResponse;
import id.dogbreed.service.factory.BreedProcessorFactory;
import id.dogbreed.service.strategy.ShibaBreedProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DogBreedServiceImpl implements DogBreedService {

    private final DogApiClient dogApiClient;
    private final BreedProcessorFactory processorFactory;
    private final ShibaBreedProcessor shibaProcessor;

    @Override
    public Map<String, List<String>> listAllBreeds() {
        BreedsAllResponse response = dogApiClient.fetchAllBreeds();

        if (response == null || response.message() == null) {
            log.warn("Dog CEO API returned empty response");
            return Collections.emptyMap();
        }

        Map<String, List<String>> result = new LinkedHashMap<>();
        response.message().forEach((breed, subBreeds) ->
                processorFactory.getStrategy(breed).process(result, breed, subBreeds)
        );

        return Collections.unmodifiableMap(result);
    }

    @Override
    public List<String> getShibaOddImages() {
        return shibaProcessor.getOddImages();
    }
}
