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
        webClient = WebClient.builder().build();
    }

    /*
     * Make a http request to the API with path variables.
     *
     * LGTM reporting a possible false positive: Groupings-1001
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
     * Make a http request to the API with path variables and description in the body.
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
