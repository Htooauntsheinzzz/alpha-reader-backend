package com.web.alpha.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.web.alpha.appgenre.exception.AppGenreNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalApiExceptionHandlerTest {

    private final GlobalApiExceptionHandler handler = new GlobalApiExceptionHandler();

    @Test
    void returnsSharedErrorFormatForModuleException() {
        MockHttpServletRequest request = new MockHttpServletRequest(
                "GET",
                "/api/v1/admin/content/story-genres/999"
        );

        ResponseEntity<ApiErrorResponse> response = handler.handleApiException(
                new AppGenreNotFoundException(),
                request
        );

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("APP_GENRE_NOT_FOUND", response.getBody().code());
        assertEquals("App genre not found", response.getBody().message());
        assertEquals("/api/v1/admin/content/story-genres/999", response.getBody().path());
        assertTrue(response.getBody().fieldErrors().isEmpty());
    }
}
