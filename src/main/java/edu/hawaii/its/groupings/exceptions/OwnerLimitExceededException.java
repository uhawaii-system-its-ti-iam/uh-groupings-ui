package edu.hawaii.its.groupings.exceptions;

import edu.hawaii.its.api.type.ApiSubError;

import java.util.List;
import java.util.ArrayList;

public class OwnerLimitExceededException extends RuntimeException {

    private List<ApiSubError> subErrors;

    public OwnerLimitExceededException(String message) {
        super(message);
        this.subErrors = new ArrayList<>();
    }

    public List<ApiSubError> getSubErrors() {
        return subErrors;
    }

    public OwnerLimitExceededException addSubError(ApiSubError subError) {
        this.subErrors.add(subError);
        return this;
    }
}
