package id.dogbreed.service.factory;

import id.dogbreed.service.strategy.BreedProcessingStrategy;
import id.dogbreed.service.strategy.DefaultBreedProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Component
public class BreedProcessorFactory {

    private final Map<String, BreedProcessingStrategy> strategyRegistry;
    private final BreedProcessingStrategy defaultStrategy;

    public BreedProcessorFactory(List<BreedProcessingStrategy> strategies) {
        this.strategyRegistry = strategies.stream()
                .filter(s -> !DefaultBreedProcessor.DEFAULT_BREED.equals(s.supportedBreed()))
                .collect(Collectors.toMap(BreedProcessingStrategy::supportedBreed, Function.identity()));

        this.defaultStrategy = strategies.stream()
                .filter(s -> DefaultBreedProcessor.DEFAULT_BREED.equals(s.supportedBreed()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("DefaultBreedProcessor tidak ditemukan di Spring context"));

        log.info("BreedProcessorFactory ready — {} strategy terdaftar: {}",
                strategyRegistry.size(), strategyRegistry.keySet());
    }

    public BreedProcessingStrategy getStrategy(String breedName) {
        return strategyRegistry.getOrDefault(breedName, defaultStrategy);
    }
}
