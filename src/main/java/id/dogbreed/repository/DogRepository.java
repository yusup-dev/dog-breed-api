package id.dogbreed.repository;

import id.dogbreed.model.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {

    List<Dog> findByBreedIgnoreCase(String breed);

    List<Dog> findByBreedIgnoreCaseAndSubBreedIgnoreCase(String breed, String subBreed);
}
