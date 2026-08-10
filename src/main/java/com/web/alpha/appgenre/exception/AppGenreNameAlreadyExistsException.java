package com.web.alpha.appgenre.exception;

import com.web.alpha.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public final class AppGenreNameAlreadyExistsException extends ApiException {

    public AppGenreNameAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "APP_GENRE_NAME_ALREADY_EXISTS", "App genre name already exists");
    }

    public AppGenreNameAlreadyExistsException(Throwable cause) {
        super(
                HttpStatus.CONFLICT,
                "APP_GENRE_NAME_ALREADY_EXISTS",
                "App genre name already exists",
                cause
        );
    }
}
