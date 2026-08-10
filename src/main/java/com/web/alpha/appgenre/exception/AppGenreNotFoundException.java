package com.web.alpha.appgenre.exception;

import com.web.alpha.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public final class AppGenreNotFoundException extends ApiException {

    public AppGenreNotFoundException() {
        super(HttpStatus.NOT_FOUND, "APP_GENRE_NOT_FOUND", "App genre not found");
    }
}
