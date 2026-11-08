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
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShibaBreedProcessor implements BreedProcessingStrategy {

    private final DogApiClient dogApiClient;

    @Override
    public String supportedBreed() {
        return "shiba";
    }

    @Override
    public void process(Map<String, List<String>> result, String breed, List<String> subBreeds) {
        result.put(breed, getOddImages());
    }

    public List<String> getOddImages() {
        List<String> images = resolveQuietly(dogApiClient.fetchImagesAsync(supportedBreed()));

        List<String> oddPositions = IntStream.range(0, images.size())
                .filter(i -> i % 2 == 0)
                .mapToObj(images::get)
                .toList();

        int size = oddPositions.size() % 2 == 0 ? oddPositions.size() - 1 : oddPositions.size();
        return oddPositions.subList(0, Math.max(size, 0));
    }

    private List<String> resolveQuietly(CompletableFuture<List<String>> future) {
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            log.warn("Timeout while resolving shiba images");
            future.cancel(true);
            return Collections.emptyList();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted while resolving shiba images", ex);
            return Collections.emptyList();
        } catch (ExecutionException ex) {
            log.warn("Failed to resolve shiba images: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }
}
