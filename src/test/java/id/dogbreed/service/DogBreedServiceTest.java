package id.dogbreed.service;

import id.dogbreed.client.DogApiClient;
import id.dogbreed.dto.BreedsAllResponse;
import id.dogbreed.service.factory.BreedProcessorFactory;
import id.dogbreed.service.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogBreedServiceTest {

    @Mock
    private DogApiClient dogApiClient;

    private DogBreedServiceImpl dogBreedService;

    @BeforeEach
    void setUp() {
        List<BreedProcessingStrategy> strategies = List.of(
                new TerrierBreedProcessor(dogApiClient),
                new ShibaBreedProcessor(dogApiClient),
                new SheepdogBreedProcessor(),
                new DefaultBreedProcessor()
        );

        BreedProcessorFactory factory = new BreedProcessorFactory(strategies);
        ShibaBreedProcessor shibaProcessor = new ShibaBreedProcessor(dogApiClient);

        dogBreedService = new DogBreedServiceImpl(
                dogApiClient,
                factory,
                shibaProcessor
        );
    }

    // ── listAllBreeds – general cases ────────────────────────────────────────

    @Test
    @DisplayName("listAllBreeds returns an empty map when the API response is null")
    void listAllBreeds_nullResponse_returnsEmptyMap() {
        when(dogApiClient.fetchAllBreeds()).thenReturn(null);

        assertThat(dogBreedService.listAllBreeds()).isEmpty();
    }

    @Test
    @DisplayName("listAllBreeds includes regular breeds with an empty image list")
    void listAllBreeds_regularBreed_hasEmptyImages() {
        BreedsAllResponse response = new BreedsAllResponse(
                Map.of("labrador", List.of("retriever")),
                "success"
        );

        when(dogApiClient.fetchAllBreeds()).thenReturn(response);

        Map<String, List<String>> result = dogBreedService.listAllBreeds();

        assertThat(result).containsKey("labrador");
        assertThat(result.get("labrador")).isEmpty();
    }

    // ── Shiba – images at odd-numbered positions ──────────────────────────────

    @Test
    @DisplayName("getShibaOddImages returns images at odd-numbered positions using 1-based indexing")
    void getShibaOddImages_returnsOddPositionImages() {
        List<String> allImages = Arrays.asList(
                "img1.jpg",
                "img2.jpg",
                "img3.jpg",
                "img4.jpg",
                "img5.jpg"
        );

        when(dogApiClient.fetchImagesAsync("shiba"))
                .thenReturn(CompletableFuture.completedFuture(allImages));

        List<String> result = dogBreedService.getShibaOddImages();

        assertThat(result).containsExactly(
                "img1.jpg",
                "img3.jpg",
                "img5.jpg"
        );
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("getShibaOddImages returns a single image when only one image is available")
    void getShibaOddImages_singleImage_returnsSingleImage() {
        when(dogApiClient.fetchImagesAsync("shiba"))
                .thenReturn(CompletableFuture.completedFuture(
                        List.of("only.jpg")
                ));

        assertThat(dogBreedService.getShibaOddImages())
                .containsExactly("only.jpg");
    }

    @Test
    @DisplayName("getShibaOddImages returns an empty list when no images are available")
    void getShibaOddImages_noImages_returnsEmpty() {
        when(dogApiClient.fetchImagesAsync("shiba"))
                .thenReturn(CompletableFuture.completedFuture(
                        Collections.emptyList()
                ));

        assertThat(dogBreedService.getShibaOddImages()).isEmpty();
    }

    // ── Sheepdog – sub-breed promotion ────────────────────────────────────────

    @Test
    @DisplayName("listAllBreeds promotes sheepdog sub-breeds to top-level entries")
    void listAllBreeds_sheepdog_promotesSubBreeds() {
        BreedsAllResponse response = new BreedsAllResponse(
                Map.of("sheepdog", Arrays.asList("english", "shetland")),
                "success"
        );

        when(dogApiClient.fetchAllBreeds()).thenReturn(response);

        Map<String, List<String>> result = dogBreedService.listAllBreeds();

        assertThat(result).doesNotContainKey("sheepdog");
        assertThat(result).containsKey("sheepdog-english");
        assertThat(result).containsKey("sheepdog-shetland");
        assertThat(result.get("sheepdog-english")).isEmpty();
        assertThat(result.get("sheepdog-shetland")).isEmpty();
    }

    @Test
    @DisplayName("listAllBreeds keeps sheepdog unchanged when it has no sub-breeds")
    void listAllBreeds_sheepdogNoSubBreeds_keepsAsIs() {
        BreedsAllResponse response = new BreedsAllResponse(
                Map.of("sheepdog", Collections.emptyList()),
                "success"
        );

        when(dogApiClient.fetchAllBreeds()).thenReturn(response);

        assertThat(dogBreedService.listAllBreeds())
                .containsKey("sheepdog");
    }

    // ── Terrier – sub-breed promotion and concurrent image fetching ───────────

    @Test
    @DisplayName("listAllBreeds promotes terrier sub-breeds and retrieves their images")
    void listAllBreeds_terrier_promotesSubBreedsWithImages() {
        BreedsAllResponse response = new BreedsAllResponse(
                Map.of("terrier", Arrays.asList("american", "scottish")),
                "success"
        );

        when(dogApiClient.fetchAllBreeds()).thenReturn(response);

        when(dogApiClient.fetchImagesAsync("terrier-american"))
                .thenReturn(CompletableFuture.completedFuture(
                        List.of("american1.jpg", "american2.jpg")
                ));

        when(dogApiClient.fetchImagesAsync("terrier-scottish"))
                .thenReturn(CompletableFuture.completedFuture(
                        List.of("scottish1.jpg")
                ));

        Map<String, List<String>> result = dogBreedService.listAllBreeds();

        assertThat(result).doesNotContainKey("terrier");
        assertThat(result.get("terrier-american"))
                .containsExactly("american1.jpg", "american2.jpg");
        assertThat(result.get("terrier-scottish"))
                .containsExactly("scottish1.jpg");
    }

    @Test
    @DisplayName("listAllBreeds handles terrier image-fetching failures gracefully")
    void listAllBreeds_terrier_imagesFetchFailure_returnsEmpty() {
        BreedsAllResponse response = new BreedsAllResponse(
                Map.of("terrier", List.of("bedlington")),
                "success"
        );

        when(dogApiClient.fetchAllBreeds()).thenReturn(response);

        when(dogApiClient.fetchImagesAsync("terrier-bedlington"))
                .thenReturn(CompletableFuture.completedFuture(
                        Collections.emptyList()
                ));

        Map<String, List<String>> result = dogBreedService.listAllBreeds();

        assertThat(result).containsKey("terrier-bedlington");
        assertThat(result.get("terrier-bedlington")).isEmpty();
    }
}