package id.dogbreed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record BreedsAllResponse(
        @JsonProperty("message") Map<String, List<String>> message,
        @JsonProperty("status") String status
) {}
