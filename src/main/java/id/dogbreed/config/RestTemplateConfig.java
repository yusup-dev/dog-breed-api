package id.dogbreed.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${dog.api.timeout.breeds-list-ms}")
    private int breedsListTimeoutMs;

    @Value("${dog.api.timeout.sub-breed-list-ms}")
    private int subBreedListTimeoutMs;

    @Bean("restTemplateBreeds")
    public RestTemplate restTemplateBreeds() {
        return buildRestTemplate(breedsListTimeoutMs);
    }

    @Bean("restTemplateSubBreed")
    public RestTemplate restTemplateSubBreed() {
        return buildRestTemplate(subBreedListTimeoutMs);
    }

    private RestTemplate buildRestTemplate(int timeoutMs) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);
        return new RestTemplate(factory);
    }
}
