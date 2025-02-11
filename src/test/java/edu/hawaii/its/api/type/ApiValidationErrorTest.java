package edu.hawaii.its.api.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class ApiValidationErrorTest {
    private ApiValidationError validationError;

    @BeforeEach
    public void setup() {
        validationError = new ApiValidationError("TestObject", "TestField", "RejectedValue", "Test message");
    }

    @Test
    public void testConstructorAndGetter() {
        assertEquals("TestObject", validationError.getObject());
        assertEquals("TestField", validationError.getField());
        assertEquals("RejectedValue", validationError.getRejectedValue());
        assertEquals("Test message", validationError.getMessage());
    }

    @Test
    public void testSetters() {
        validationError.setObject("NewObject");
        validationError.setField("NewField");
        validationError.setRejectedValue("NewRejectedValue");
        validationError.setMessage("New message");

        assertEquals("NewObject", validationError.getObject());
        assertEquals("NewField", validationError.getField());
        assertEquals("NewRejectedValue", validationError.getRejectedValue());
        assertEquals("New message", validationError.getMessage());
    }

    @Test
    public void testConstructorWithLessParameters() {
        ApiValidationError errorWithLessParams = new ApiValidationError("TestObject", "Test message");
        assertEquals("TestObject", errorWithLessParams.getObject());
        assertNull(errorWithLessParams.getField());
        assertNull(errorWithLessParams.getRejectedValue());
        assertEquals("Test message", errorWithLessParams.getMessage());
    }

    @Test
    public void testApiValidationErrorIntegrationWithApiError() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        LocalDateTime timestamp = LocalDateTime.now();
        String message = "Error message";

        ApiValidationError subError1 = new ApiValidationError("Object1", "Field1", "Value1", "Invalid value for Field1");
        ApiValidationError subError2 = new ApiValidationError("Object2", "Field2", "Value2", "Invalid value for Field2");

        ApiError apiError = new ApiError.Builder()
                .status(status)
                .timestamp(timestamp)
                .message(message)
                .addAllSubErrors(Arrays.asList(subError1, subError2))
                .build();

        assertNotNull(apiError.getSubErrors());
        assertEquals(2, apiError.getSubErrors().size());
        assertTrue(apiError.getSubErrors().contains(subError1));
        assertTrue(apiError.getSubErrors().contains(subError2));
    }
}
