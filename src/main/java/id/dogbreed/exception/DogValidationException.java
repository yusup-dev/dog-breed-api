package id.dogbreed.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class DogValidationException extends RuntimeException {

    public DogValidationException(String message) {
        super(message);
    }
}
