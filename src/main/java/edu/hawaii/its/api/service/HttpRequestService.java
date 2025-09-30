package edu.hawaii.its.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.service.JwtService;

@Service("httpRequestService")
public class HttpRequestService {

    private final JwtService jwtService;
    private final UserContextService userContextService;
    private final WebClient webClient;

    public HttpRequestService(JwtService jwtService, UserContextService userContextService) {
        this.userContextService = userContextService;
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
                .header("Authorization", "Bearer " + generateJWT())
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
                .header("Authorization", "Bearer " + generateJWT())
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
                .header("Authorization", "Bearer " + generateJWT())
                .bodyValue(data)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    private String generateJWT() {
        User user = userContextService.getCurrentUser();
        return jwtService.generateToken(user);
    }
}
