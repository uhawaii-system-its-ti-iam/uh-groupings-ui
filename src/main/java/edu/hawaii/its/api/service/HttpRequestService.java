package edu.hawaii.its.api.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface HttpRequestService {

    ResponseEntity<String> makeApiRequest(String currentUser, String uri, HttpMethod method);

    ResponseEntity<String> makeApiRequestWithBody(String currentUser, String uri, String data, HttpMethod method);

    ResponseEntity<String> makeApiRequestWithBody(String currentUser, String uri, List<String> data, HttpMethod method);

    ResponseEntity<String> makeApiRequestWithParameters(String currentUser, String urlTemplate, Map<String, String> params, HttpMethod method);

}
