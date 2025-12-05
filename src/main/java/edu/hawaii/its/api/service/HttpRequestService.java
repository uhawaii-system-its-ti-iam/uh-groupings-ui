package edu.hawaii.its.api.service;

import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import edu.hawaii.its.groupings.service.JwtService;

@Service("httpRequestService")
public class HttpRequestService {

    private final JwtService jwtService;
    private final WebClient webClient;

    public HttpRequestService(JwtService jwtService) {
        this.jwtService = jwtService;
        webClient = WebClient.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(-1))
                .build();
    }

    /*
     * Make a http request to the API with path variables.
     */
    public ResponseEntity<String> makeApiRequest(String uri, HttpMethod method) {
        return webClient.method(method)
                .uri(uri)
                .header("Authorization", "Bearer " + jwtService.generateToken())
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    /*
     * Make a http request to the API with path variables and description string in the body.
     */
    public ResponseEntity<String> makeApiRequestWithBody(String uri, String data,
            HttpMethod method) {
        return webClient.method(method)
                .uri(uri)
                .header("Authorization", "Bearer " + jwtService.generateToken())
                .bodyValue(data)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    /*
     * Make a http request to the API with path variables and description list of strings in the body.
     */
    public ResponseEntity<String> makeApiRequestWithBody(String uri, List<String> data,
            HttpMethod method) {
        return webClient.method(method)
                .uri(uri)
                .header("Authorization", "Bearer " + jwtService.generateToken())
                .bodyValue(data)
                .retrieve()
                .toEntity(String.class)
                .block();
    }
}
