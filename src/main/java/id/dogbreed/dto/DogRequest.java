package id.dogbreed.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DogRequest(

        @NotBlank(message = "Nama tidak boleh kosong")
        @Size(max = 100)
        String name,

        @NotBlank(message = "Ras tidak boleh kosong")
        @Size(max = 100)
        String breed,

        @Size(max = 100)
        String subBreed,

        @Min(value = 0, message = "Umur tidak boleh negatif")
        Integer age,

        @Size(max = 500)
        String description
) {}
