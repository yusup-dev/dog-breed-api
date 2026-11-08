package id.dogbreed.service;

import id.dogbreed.dto.DogRequest;
import id.dogbreed.dto.DogResponse;
import id.dogbreed.exception.ResourceNotFoundException;
import id.dogbreed.mapper.DogMapper;
import id.dogbreed.model.Dog;
import id.dogbreed.repository.DogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogServiceTest {

    @Mock
    private DogRepository dogRepository;

    @Mock
    private DogMapper dogMapper;

    @InjectMocks
    private DogServiceImpl dogService;

    private Dog sampleDog;
    private DogResponse sampleResponse;
    private DogRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleDog = new Dog(
                "Buddy",
                "terrier",
                "american",
                3,
                "Friendly terrier"
        );
        sampleDog.setId(1L);

        sampleResponse = new DogResponse(
                1L,
                "Buddy",
                "terrier",
                "american",
                3,
                "Friendly terrier",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        sampleRequest = new DogRequest(
                "Buddy",
                "terrier",
                "american",
                3,
                "Friendly terrier"
        );
    }

    // ── findAll ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll returns a list of DogResponse objects")
    void findAll_returnsDogResponseList() {
        when(dogRepository.findAll()).thenReturn(List.of(sampleDog));
        when(dogMapper.toResponse(sampleDog)).thenReturn(sampleResponse);

        List<DogResponse> result = dogService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Buddy");
        assertThat(result.get(0).breed()).isEqualTo("terrier");
    }

    @Test
    @DisplayName("findAll returns an empty list when no data exists")
    void findAll_empty_returnsEmptyList() {
        when(dogRepository.findAll()).thenReturn(List.of());

        assertThat(dogService.findAll()).isEmpty();
    }

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById returns a DogResponse when the dog is found")
    void findById_existingId_returnsDogResponse() {
        when(dogRepository.findById(1L)).thenReturn(Optional.of(sampleDog));
        when(dogMapper.toResponse(sampleDog)).thenReturn(sampleResponse);

        DogResponse result = dogService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Buddy");
    }

    @Test
    @DisplayName("findById throws ResourceNotFoundException when the dog is not found")
    void findById_missingId_throwsException() {
        when(dogRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dogService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── create ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("create saves a new dog and returns its DogResponse")
    void create_validRequest_returnsDogResponse() {
        when(dogMapper.toEntity(sampleRequest)).thenReturn(sampleDog);
        when(dogRepository.save(sampleDog)).thenReturn(sampleDog);
        when(dogMapper.toResponse(sampleDog)).thenReturn(sampleResponse);

        DogResponse result = dogService.create(sampleRequest);

        assertThat(result.name()).isEqualTo("Buddy");
        verify(dogRepository).save(sampleDog);
    }

    // ── update ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("update modifies the dog's fields and returns the updated response")
    void update_existingDog_returnsUpdatedResponse() {
        DogResponse updatedResponse = new DogResponse(
                1L,
                "Max",
                "shiba",
                null,
                5,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        DogRequest updateRequest = new DogRequest(
                "Max",
                "shiba",
                null,
                5,
                null
        );

        when(dogRepository.findById(1L)).thenReturn(Optional.of(sampleDog));
        when(dogRepository.save(sampleDog)).thenReturn(sampleDog);
        when(dogMapper.toResponse(sampleDog)).thenReturn(updatedResponse);

        DogResponse result = dogService.update(1L, updateRequest);

        assertThat(result.name()).isEqualTo("Max");
        assertThat(result.breed()).isEqualTo("shiba");
    }

    @Test
    @DisplayName("update throws ResourceNotFoundException when the dog is not found")
    void update_missingId_throwsException() {
        when(dogRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dogService.update(99L, sampleRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── delete ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete removes the dog when it exists")
    void delete_existingId_callsDelete() {
        when(dogRepository.findById(1L)).thenReturn(Optional.of(sampleDog));
        doNothing().when(dogRepository).delete(sampleDog);

        dogService.delete(1L);

        verify(dogRepository).delete(sampleDog);
    }

    @Test
    @DisplayName("delete throws ResourceNotFoundException when the dog is not found")
    void delete_missingId_throwsException() {
        when(dogRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dogService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}