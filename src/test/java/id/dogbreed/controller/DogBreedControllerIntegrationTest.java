package id.dogbreed.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import id.dogbreed.DogBreedApiApplication;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = DogBreedApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DogBreedControllerIntegrationTest {

    static WireMockServer wireMockServer;

    @Autowired
    MockMvc mockMvc;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void resetStubs() {
        wireMockServer.resetAll();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        // Point the app at WireMock instead of the real Dog CEO API
        registry.add("dog.api.base-url",
                () -> "http://localhost:" + wireMockServer.port());
    }

    // =========================================================================
    // /api/breeds – sheepdog transformation
    // =========================================================================

    @Test
    @Order(1)
    @DisplayName("GET /api/breeds promotes sheepdog sub-breeds to top-level keys")
    void listAllBreeds_sheepdogPromotion() throws Exception {
        // WireMock stub (uses statically-imported WireMock.get via urlEqualTo)
        wireMockServer.stubFor(WireMock.get(urlEqualTo("/breeds/list/all"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "message": {
                                    "sheepdog": ["english", "shetland"]
                                  },
                                  "status": "success"
                                }
                                """)));

        // MockMvc call (uses fully-qualified MockMvcRequestBuilders.get to avoid name clash)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/breeds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data['sheepdog-english']").isArray())
                .andExpect(jsonPath("$.data['sheepdog-shetland']").isArray())
                .andExpect(jsonPath("$.data.sheepdog").doesNotExist());
    }

    // =========================================================================
    // /api/breeds/shiba/images – odd image filtering
    // =========================================================================

    @Test
    @Order(2)
    @DisplayName("GET /api/breeds/shiba/images returns only odd-position images")
    void getShibaImages_returnsOddPositions() throws Exception {
        wireMockServer.stubFor(WireMock.get(urlEqualTo("/breeds/list/all"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"message": {"shiba": []}, "status": "success"}
                                """)));

        wireMockServer.stubFor(WireMock.get(urlEqualTo("/breed/shiba/images"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "message": ["img1.jpg","img2.jpg","img3.jpg","img4.jpg","img5.jpg"],
                                  "status": "success"
                                }
                                """)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/breeds/shiba/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").value("img1.jpg"))
                .andExpect(jsonPath("$.data[1]").value("img3.jpg"))
                .andExpect(jsonPath("$.data[2]").value("img5.jpg"))
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    // =========================================================================
    // /api/breeds – terrier transformation
    // =========================================================================

    @Test
    @Order(3)
    @DisplayName("GET /api/breeds promotes terrier sub-breeds with images")
    void listAllBreeds_terrierWithImages() throws Exception {
        wireMockServer.stubFor(WireMock.get(urlEqualTo("/breeds/list/all"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "message": {
                                    "terrier": ["american", "scottish"]
                                  },
                                  "status": "success"
                                }
                                """)));

        wireMockServer.stubFor(WireMock.get(urlEqualTo("/breed/terrier/american/images"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"message": ["american1.jpg", "american2.jpg"], "status": "success"}
                                """)));

        wireMockServer.stubFor(WireMock.get(urlEqualTo("/breed/terrier/scottish/images"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"message": ["scottish1.jpg"], "status": "success"}
                                """)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/breeds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.terrier").doesNotExist())
                .andExpect(jsonPath("$.data['terrier-american']").isArray())
                .andExpect(jsonPath("$.data['terrier-scottish']").isArray());
    }
}
