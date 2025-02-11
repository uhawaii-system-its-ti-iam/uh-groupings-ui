package edu.hawaii.its.api.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class ApiErrorTest {
    private ApiError.Builder errorBuilder;

    @BeforeEach
    public void setup() {
        errorBuilder = new ApiError.Builder();
    }

    @Test
    public void testApiErrorWithMultipleSubErrors() {
        LocalDateTime timestamp = LocalDateTime.now();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Test error message";
        String debugMessage = "Debugging error message";

        // Create ApiSubError instances
        ApiSubError subError1 = new ApiValidationError("testing object 1", "membership service", "id: 12", "Membership access denied");
        ApiSubError subError2 = new ApiValidationError("testing object 2", "member service", "id: 30", "There is no member");

        // Create a list of ApiSubError
        List<ApiSubError> subErrors = new ArrayList<>();
        subErrors.add(subError1);
        subErrors.add(subError2);

        errorBuilder
                .status(status)
                .timestamp(timestamp)
                .message(message)
                .debugMessage(debugMessage);

        // Build ApiError by adding subErrors in a loop
        for (ApiSubError subError : subErrors) {
            errorBuilder.addSubError(subError);
        }

        // Build ApiError (Standard Failure Object)
        ApiError apiError = errorBuilder.build();

        assertNotNull(apiError);
        assertEquals(status, apiError.getStatus());
        assertEquals(timestamp, apiError.getTimestamp());
        assertEquals(message, apiError.getMessage());
        assertEquals(debugMessage, apiError.getDebugMessage());

        // Check if the subErrors in the ApiError match the ones added
        assertEquals(subErrors.size(), apiError.getSubErrors().size());
        assertTrue(apiError.getSubErrors().containsAll(subErrors));
    }

    @Test
    public void testApiErrorWithoutSubErrorsMethods() {
        LocalDateTime timestamp = LocalDateTime.now();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Test message";
        ApiError apiError = new ApiError.Builder()
                .status(status)
                .timestamp(timestamp)
                .message(message)
                .build();

        assertEquals(status, apiError.getStatus());
        assertEquals(timestamp, apiError.getTimestamp());
        assertEquals(message, apiError.getMessage());
        assertNull(apiError.getDebugMessage());
        assertTrue(apiError.getSubErrors().isEmpty());
    }

    @Test
    public void testApiErrorBuilderRequiredFields() {
        // Attempting to build without required fields should throw NullPointerException
        assertThrows(NullPointerException.class, () -> errorBuilder.build());
    }

    @Test
    public void testApiErrorWithoutRequiredFields() {

        // Try building ApiError without timestamp
        assertThrows(NullPointerException.class, () -> {
            errorBuilder
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Test error message")
                    .debugMessage("Debugging error message")
                    .build();
        });

        // Try building ApiError without message
        assertThrows(NullPointerException.class, () -> {
            errorBuilder = new ApiError.Builder();
            errorBuilder
                    .status(HttpStatus.BAD_REQUEST)
                    .timestamp(LocalDateTime.now())
                    .debugMessage("Debugging error message")
                    .build();
        });

        // Try building ApiError without status
        assertThrows(NullPointerException.class, () -> {
            errorBuilder = new ApiError.Builder();
            errorBuilder
                    .timestamp(LocalDateTime.now())
                    .message("Test error message")
                    .debugMessage("Debugging error message")
                    .build();
        });
    }

}
