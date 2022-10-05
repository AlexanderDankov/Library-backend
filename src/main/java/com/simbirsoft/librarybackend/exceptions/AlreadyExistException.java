package com.simbirsoft.librarybackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.service.Response;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AlreadyExistException extends RuntimeException {

    public AlreadyExistException(String message) {
        super(message);
    }

}
