package edu.hawaii.its.api.type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApiErrorTest {
    private ApiError.Builder errorBuilder;

    @BeforeEach
    public void setup() {

        errorBuilder = new ApiError.Builder();
    }

    @Test
    public void testApiError() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Test error message";
        String stackTrace = "Test stack trace error message";
        String resultCode = "Test result code error message";
        String path = "Test path error message";

        errorBuilder
                .status(status)
                .message(message)
                .stackTrace(stackTrace)
                .resultCode(resultCode)
                .path(path);

        ApiError apiError = errorBuilder.build();

        assertNotNull(apiError);
        assertEquals(status, apiError.getStatus());
        assertEquals(message, apiError.getMessage());
        assertEquals(stackTrace, apiError.getStackTrace());
        assertEquals(resultCode, apiError.getResultCode());
        assertEquals(path, apiError.getPath());
    }

    @Test
    public void testDefaultTimestamp() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Test error message";
        String stackTrace = "Test stack trace error message";
        String resultCode = "FAILURE";
        String path = "Test path error message";

        errorBuilder
                .status(status)
                .message(message)
                .stackTrace(stackTrace)
                .resultCode(resultCode)
                .path(path);

        ApiError apiError = errorBuilder.build();
        assertNotNull(apiError.getTimestamp());
    }

    @Test
    public void testDefaultResultCode() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Test error message";
        String stackTrace = "Test stack trace error message";
        String path = "Test path error message";

        errorBuilder
                .status(status)
                .message(message)
                .stackTrace(stackTrace)
                .path(path);

        ApiError apiError = errorBuilder.build();
        assertEquals("FAILURE", apiError.getResultCode());
    }

    @Test
    public void testApiErrorBuilderRequiredFields() {
        // Attempting to build without required fields should throw NullPointerException
        assertThrows(NullPointerException.class, () -> errorBuilder.build());
    }

    @Test
    public void testApiErrorWithoutRequiredFields() {
        // Try building ApiError without status
        assertThrows(NullPointerException.class, () -> {
            errorBuilder = new ApiError.Builder();
            errorBuilder
                    .message("Test error message")
                    .stackTrace("Test stack trace error message")
                    .resultCode("Test result code error message")
                    .path("Test path error message")
                    .build();
        });

        // Try building ApiError without message
        assertThrows(NullPointerException.class, () -> {
            errorBuilder = new ApiError.Builder();
            errorBuilder
                    .status(HttpStatus.BAD_REQUEST)
                    .stackTrace("Test stack trace error message")
                    .resultCode("Test result code error message")
                    .path("Test path error message")
                    .build();
        });

        // Try building ApiError without stack trace
        assertThrows(NullPointerException.class, () -> {
            errorBuilder = new ApiError.Builder();
            errorBuilder
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Test error message")
                    .resultCode("Test result code error message")
                    .path("Test path error message")
                    .build();
        });

        // Try building ApiError without path
        assertThrows(NullPointerException.class, () -> {
            errorBuilder = new ApiError.Builder();
            errorBuilder
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Test error message")
                    .stackTrace("Test stack trace error message")
                    .resultCode("Test result code error message")
                    .build();
        });
    }
}
