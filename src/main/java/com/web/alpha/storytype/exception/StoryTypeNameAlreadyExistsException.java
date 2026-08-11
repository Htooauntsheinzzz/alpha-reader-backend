package com.web.alpha.storytype.exception;

import com.web.alpha.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public final class StoryTypeNameAlreadyExistsException extends ApiException {

    public StoryTypeNameAlreadyExistsException() {
        super(
                HttpStatus.CONFLICT,
                "STORY_TYPE_NAME_ALREADY_EXISTS",
                "Story type name already exists"
        );
    }
}
