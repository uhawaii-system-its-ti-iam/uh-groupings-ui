package edu.hawaii.its.api.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public interface HttpRequestService {

    ResponseEntity<String> makeApiRequest(String currentUser, String uri, HttpMethod method);

    ResponseEntity<String> makeApiRequestWithBody(String currentUser, String uri, String data, HttpMethod method);

}
