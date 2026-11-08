package id.dogbreed.client;

import id.dogbreed.dto.BreedImagesResponse;
import id.dogbreed.dto.BreedsAllResponse;
import id.dogbreed.dto.SubBreedListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class DogApiClientImpl implements DogApiClient  {

    private final RestTemplate restTemplateBreeds;
    private final RestTemplate restTemplateSubBreed;
    private final String baseUrl;

    public DogApiClientImpl(
            @Qualifier("restTemplateBreeds") RestTemplate restTemplateBreeds,
            @Qualifier("restTemplateSubBreed") RestTemplate restTemplateSubBreed,
            @Value("${dog.api.base-url}") String baseUrl) {
        this.restTemplateBreeds = restTemplateBreeds;
        this.restTemplateSubBreed = restTemplateSubBreed;
        this.baseUrl = baseUrl;
    }

    // ── breeds/list/all  (timeout 5 000 ms) ──────────────────────────────────

    @Override
    @Cacheable("breedsAll")
    public BreedsAllResponse fetchAllBreeds() {
        String url = baseUrl + "/breeds/list/all";
        log.info("Fetching all breeds: {}", url);
        return restTemplateBreeds.getForObject(url, BreedsAllResponse.class);
    }

    // ── breed/{name}/list  (timeout 2 000 ms) ────────────────────────────────
    @Override
    public List<String> fetchSubBreeds(String breedName) {
        String url = baseUrl + "/breed/" + breedName + "/list";
        log.info("Fetching sub-breeds for '{}': {}", breedName, url);
        return Optional.ofNullable(restTemplateSubBreed.getForObject(url, SubBreedListResponse.class))
                .map(SubBreedListResponse::message)
                .orElse(Collections.emptyList());
    }

    // ── breed/{name}/images  (async, reuse breedsTemplate) ───────────────────
    @Override
    @Async("dogApiExecutor")
    public CompletableFuture<List<String>> fetchImagesAsync(String breedPath) {
        String url = buildImageUrl(breedPath);
        log.info("Fetching images for '{}': {}", breedPath, url);
        try {
            List<String> images = Optional.ofNullable(
                            restTemplateBreeds.getForObject(url, BreedImagesResponse.class))
                    .map(BreedImagesResponse::message)
                    .orElse(Collections.emptyList());
            return CompletableFuture.completedFuture(images);
        } catch (Exception ex) {
            log.warn("Failed to fetch images for '{}': {}", breedPath, ex.getMessage());
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
    }

    private String buildImageUrl(String breedPath) {
        String[] parts = breedPath.split("-", 2);
        return parts.length == 2
                ? baseUrl + "/breed/" + parts[0] + "/" + parts[1] + "/images"
                : baseUrl + "/breed/" + parts[0] + "/images";
    }
}
