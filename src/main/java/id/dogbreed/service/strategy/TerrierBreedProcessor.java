package id.dogbreed.service.strategy;

import id.dogbreed.client.DogApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TerrierBreedProcessor implements BreedProcessingStrategy {

    private final DogApiClient dogApiClient;

    @Override
    public String supportedBreed() {
        return "terrier";
    }

    @Override
    public void process(Map<String, List<String>> result, String breed, List<String> subBreeds) {
        if (subBreeds == null || subBreeds.isEmpty()) {
            result.put(breed, Collections.emptyList());
            return;
        }

        List<String> breedKeys = subBreeds.stream()
                .map(sub -> breed + "-" + sub)
                .toList();

        List<CompletableFuture<List<String>>> futures = breedKeys.stream()
                .map(dogApiClient::fetchImagesAsync)
                .toList();

        awaitAll(futures);

        for (int i = 0; i < breedKeys.size(); i++) {
            result.put(breedKeys.get(i), resolveQuietly(futures.get(i)));
        }

        log.debug("Terrier: {} sub-breeds processed concurrently", breedKeys.size());
    }

    private void awaitAll(List<CompletableFuture<List<String>>> futures) {
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception ex) {
            log.warn("Some terrier image requests failed: {}", ex.getMessage());
        }
    }

    private <T> List<T> resolveQuietly(CompletableFuture<List<T>> future) {
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            log.warn("Timeout resolving terrier image future");
            future.cancel(true);
            return Collections.emptyList();
        } catch (InterruptedException | ExecutionException ex) {
            log.warn("CompletableFuture resolution failed: {}", ex.getMessage());
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        }
    }
}
