package edu.hawaii.its.api.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public interface HttpRequestService {
    public ResponseEntity makeApiRequest(String currentUser, String uri, HttpMethod method);
}
