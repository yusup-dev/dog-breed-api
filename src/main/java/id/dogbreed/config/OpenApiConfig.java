package id.dogbreed.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI dogBreedOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dog Breed API")
                        .description(
                                "REST API that consumes dog breed data from https://dog.ceo/dog-api/. " +
                                "Includes breed listing with special transformations for shiba, sheepdog, " +
                                "and terrier breeds, plus a full CRUD endpoint for stored dogs.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Technical Test")
                                .email("dev@dogbreed.id"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
