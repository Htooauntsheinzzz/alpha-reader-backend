package com.web.alpha.storytype.exception;

import com.web.alpha.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public final class StoryTypeNotFoundException extends ApiException {

    public StoryTypeNotFoundException() {
        super(HttpStatus.NOT_FOUND, "STORY_TYPE_NOT_FOUND", "Story type not found");
    }
}
