
package id.dogbreed.controller;

import id.dogbreed.dto.ApiResponse;
import id.dogbreed.service.DogBreedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/breeds")
@RequiredArgsConstructor
@Tag(
        name = "Dog Breeds",
        description = "Retrieves dog breeds from the Dog CEO API with custom transformations"
)
public class DogBreedController {

    private final DogBreedService dogBreedService;

    @GetMapping
    @Operation(
            summary = "Get all dog breeds",
            description = "Retrieves all breeds from the Dog CEO API, promoting sheepdog and terrier sub-breeds to top-level breeds"
    )
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> listAllBreeds() {
        return ResponseEntity.ok(ApiResponse.ok(dogBreedService.listAllBreeds()));
    }

    @GetMapping("/shiba/images")
    @Operation(
            summary = "Get Shiba Inu images at odd positions",
            description = "Retrieves Shiba Inu images at odd-numbered positions only (1-based indexing: 1st, 3rd, 5th, and so on)"
    )
    public ResponseEntity<ApiResponse<List<String>>> getShibaOddImages() {
        return ResponseEntity.ok(ApiResponse.ok(dogBreedService.getShibaOddImages()));
    }
}