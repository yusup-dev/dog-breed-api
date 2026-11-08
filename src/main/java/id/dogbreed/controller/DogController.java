package id.dogbreed.controller;

import id.dogbreed.dto.ApiResponse;
import id.dogbreed.dto.DogRequest;
import id.dogbreed.dto.DogResponse;
import id.dogbreed.service.DogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dogs")
@RequiredArgsConstructor
@Tag(
        name = "Dogs CRUD",
        description = "CRUD operations for dog records stored in the H2 database"
)
public class DogController {

    private final DogService dogService;

    @GetMapping
    @Operation(
            summary = "Get all dogs",
            description = "Retrieves a list of all dog records"
    )
    public ResponseEntity<ApiResponse<List<DogResponse>>> getAllDogs() {
        return ResponseEntity.ok(ApiResponse.ok(dogService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a dog by ID",
            description = "Retrieves a dog record by its unique identifier"
    )
    public ResponseEntity<ApiResponse<DogResponse>> getDogById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(dogService.findById(id)));
    }

    @PostMapping
    @Operation(
            summary = "Create a new dog",
            description = "Creates a new dog record using the provided request data"
    )
    public ResponseEntity<ApiResponse<DogResponse>> createDog(
            @Valid @RequestBody DogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(dogService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a dog",
            description = "Updates an existing dog record identified by its ID"
    )
    public ResponseEntity<ApiResponse<DogResponse>> updateDog(
            @PathVariable Long id,
            @Valid @RequestBody DogRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(dogService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a dog",
            description = "Deletes a dog record by its unique identifier"
    )
    public ResponseEntity<Void> deleteDog(@PathVariable Long id) {
        dogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
