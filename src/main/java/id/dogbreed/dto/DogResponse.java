package id.dogbreed.dto;

import java.time.LocalDateTime;

public record DogResponse(
        Long id,
        String name,
        String breed,
        String subBreed,
        Integer age,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
