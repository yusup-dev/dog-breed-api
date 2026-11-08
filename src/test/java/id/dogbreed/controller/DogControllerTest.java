package id.dogbreed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.dogbreed.dto.DogRequest;
import id.dogbreed.dto.DogResponse;
import id.dogbreed.exception.ResourceNotFoundException;
import id.dogbreed.service.DogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DogController.class)
class DogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DogService dogService;

    private DogResponse sampleResponse;
    private DogRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleResponse = new DogResponse(
                1L,
                "Buddy",
                "terrier",
                "american",
                3,
                "Friendly",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        sampleRequest = new DogRequest(
                "Buddy",
                "terrier",
                "american",
                3,
                "Friendly"
        );
    }

    @Test
    @DisplayName("GET /api/dogs returns 200 with a list of dogs")
    void getAllDogs_returns200() throws Exception {
        when(dogService.findAll()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/dogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Buddy"));
    }

    @Test
    @DisplayName("GET /api/dogs/{id} returns 200 for an existing dog")
    void getDogById_existingId_returns200() throws Exception {
        when(dogService.findById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/dogs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.breed").value("terrier"));
    }

    @Test
    @DisplayName("GET /api/dogs/{id} returns 404 when the dog does not exist")
    void getDogById_missingId_returns404() throws Exception {
        when(dogService.findById(anyLong()))
                .thenThrow(new ResourceNotFoundException(
                        "Dog not found with ID: 99"
                ));

        mockMvc.perform(get("/api/dogs/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/dogs returns 201 with the created dog")
    void createDog_validRequest_returns201() throws Exception {
        when(dogService.create(any(DogRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/dogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Buddy"));
    }

    @Test
    @DisplayName("POST /api/dogs returns 400 when the name is blank")
    void createDog_blankName_returns400() throws Exception {
        DogRequest badRequest = new DogRequest(
                "",
                "terrier",
                null,
                null,
                null
        );

        mockMvc.perform(post("/api/dogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/dogs/{id} returns 200 with the updated dog")
    void updateDog_validRequest_returns200() throws Exception {
        when(dogService.update(anyLong(), any(DogRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/api/dogs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Buddy"));
    }

    @Test
    @DisplayName("DELETE /api/dogs/{id} returns 204 when successful")
    void deleteDog_existingId_returns204() throws Exception {
        doNothing().when(dogService).delete(1L);

        mockMvc.perform(delete("/api/dogs/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/dogs/{id} returns 404 when the dog does not exist")
    void deleteDog_missingId_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Dog not found with ID: 99"))
                .when(dogService).delete(99L);

        mockMvc.perform(delete("/api/dogs/99"))
                .andExpect(status().isNotFound());
    }
}