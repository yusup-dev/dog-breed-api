package id.dogbreed.client;

import id.dogbreed.dto.BreedsAllResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DogApiClient {

    BreedsAllResponse fetchAllBreeds();

    List<String> fetchSubBreeds(String breedName);

    CompletableFuture<List<String>> fetchImagesAsync(String breedPath);
}
