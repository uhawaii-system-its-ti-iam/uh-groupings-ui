package edu.hawaii.its.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service("httpRequestService")
public class HttpRequestService {

    @Value("${groupings.api.current_user}")
    private String CURRENT_USER;

    private final WebClient webClient;

    public HttpRequestService() {
        webClient = WebClient.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(-1))
                .build();
    }

    /*
     * Make a http request to the API with path variables.
     */
    public ResponseEntity<String> makeApiRequest(String currentUser, String uri, HttpMethod method) {
        return webClient.method(method)
                .uri(uri)
                .header(CURRENT_USER, currentUser)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    /*
     * Make a http request to the API with path variables and without CURRENT_USER in http header.
     */
    public ResponseEntity<String> makeApiRequest(String uri, HttpMethod method) {
        return webClient.method(method)
                .uri(uri)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    /*
     * Make a http request to the API with path variables and description string in the body.
     */
    public ResponseEntity<String> makeApiRequestWithBody(String currentUser, String uri, String data,
                                                         HttpMethod method) {
        return webClient.method(method)
                .uri(uri)
                .header(CURRENT_USER, currentUser)
                .bodyValue(data)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    /*
     * Make a http request to the API with path variables and description list of strings in the body.
     */
    public ResponseEntity<String> makeApiRequestWithBody(String currentUser, String uri, List<String> data,
                                                         HttpMethod method) {
        return webClient.method(method)
                .uri(uri)
                .header(CURRENT_USER, currentUser)
                .bodyValue(data)
                .retrieve()
                .toEntity(String.class)
                .block();
    }
}
