package edu.hawaii.its.api.type;

import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApiError {

    private final HttpStatus status;
    private final LocalDateTime timestamp;
    private final String message;
    private final String debugMessage;
    private final List<ApiSubError> subErrors;

    private ApiError(Builder builder) {
        this.status = builder.status;
        this.timestamp = builder.timestamp != null ? builder.timestamp : LocalDateTime.now();
        this.message = builder.message;
        this.debugMessage = builder.debugMessage;
        this.subErrors = builder.subErrors;
    }

    public static class Builder {
        private HttpStatus status;
        private LocalDateTime timestamp;
        private String message;
        private String debugMessage;
        private List<ApiSubError> subErrors;

        public Builder() {
            this.subErrors = new ArrayList<>();
        }

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder debugMessage(String debugMessage) {
            this.debugMessage = debugMessage;
            return this;
        }

        public Builder addAllSubErrors(List<ApiSubError> subErrors) {
            if (subErrors != null) {
                this.subErrors.addAll(subErrors);
            }
            return this;
        }

        public Builder addSubError(ApiSubError subError) {
            this.subErrors.add(subError);
            return this;
        }

        public ApiError build() {
            Objects.requireNonNull(this.timestamp);
            Objects.requireNonNull(this.message);
            Objects.requireNonNull(this.status);
            return new ApiError(this);
        }
    }

    public HttpStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public List<ApiSubError> getSubErrors() {
        return subErrors;
    }
}
