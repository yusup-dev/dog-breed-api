package id.dogbreed.mapper;

import id.dogbreed.dto.DogRequest;
import id.dogbreed.dto.DogResponse;
import id.dogbreed.model.Dog;
import org.springframework.stereotype.Component;

@Component
public class DogMapper {

    public DogResponse toResponse(Dog dog) {
        return new DogResponse(
                dog.getId(),
                dog.getName(),
                dog.getBreed(),
                dog.getSubBreed(),
                dog.getAge(),
                dog.getDescription(),
                dog.getCreatedAt(),
                dog.getUpdatedAt()
        );
    }

    public Dog toEntity(DogRequest request) {
        return new Dog(
                request.name(),
                request.breed(),
                request.subBreed(),
                request.age(),
                request.description()
        );
    }

    public void updateEntity(Dog dog, DogRequest request) {
        dog.setName(request.name());
        dog.setBreed(request.breed());
        dog.setSubBreed(request.subBreed());
        dog.setAge(request.age());
        dog.setDescription(request.description());
    }
}