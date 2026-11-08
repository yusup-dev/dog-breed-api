package id.dogbreed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SubBreedListResponse(
        @JsonProperty("message") List<String> message,
        @JsonProperty("status") String status
) {}
