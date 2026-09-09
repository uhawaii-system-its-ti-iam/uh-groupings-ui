package edu.hawaii.its.api.service;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;

import edu.hawaii.its.groupings.service.JwtService;

@Service("httpRequestService")
public class HttpRequestService {

    private final JwtService jwtService;
    private final WebClient webClient;
    private final URI trustedApiBase;

    public HttpRequestService(JwtService jwtService,
            @Value("${groupings.api.integration.base-uri}") String trustedApiBase) {
        this.jwtService = jwtService;
        this.trustedApiBase = URI.create(trustedApiBase);
        webClient = WebClient.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(-1))
                .build();
    }

    /*
     * Make a http request to the API with path variables.
     */
    public ResponseEntity<String> makeApiRequest(String uri, HttpMethod method) {
        String safeUri = validateTrustedUri(uri);
        return toEntity(webClient.method(method)
                .uri(safeUri)
                .header("Authorization", "Bearer " + jwtService.generateToken()));
    }

    /*
     * Make an HTTP request to the API without a JWT.
     */
    public ResponseEntity<String> makeApiRequestWithoutJwt(String uri, HttpMethod method) {
        String safeUri = validateTrustedUri(uri);
        return toEntity(webClient.method(method)
                .uri(safeUri));
    }

    /*
     * Make a http request to the API with path variables and description string in the body.
     */
    public ResponseEntity<String> makeApiRequestWithBody(String uri, String data,
            HttpMethod method) {
        String safeUri = validateTrustedUri(uri);
        return toEntity(webClient.method(method)
                .uri(safeUri)
                .header("Authorization", "Bearer " + jwtService.generateToken())
                .bodyValue(data));
    }

    /*
     * Make a http request to the API with path variables and description list of strings in the body.
     */
    public ResponseEntity<String> makeApiRequestWithBody(String uri, List<String> data,
            HttpMethod method) {
        String safeUri = validateTrustedUri(uri);
        return toEntity(webClient.method(method)
                .uri(safeUri)
                .header("Authorization", "Bearer " + jwtService.generateToken())
                .bodyValue(data));
    }

    private String validateTrustedUri(String uri) {
        URI target = URI.create(uri);
        if (!sameOrigin(trustedApiBase, target)) {
            throw new IllegalArgumentException("Untrusted outbound URI: " + uri);
        }
        return target.toString();
    }

    private boolean sameOrigin(URI trustedBase, URI target) {
        return Objects.equals(trustedBase.getScheme(), target.getScheme())
                && Objects.equals(trustedBase.getHost(), target.getHost())
                && trustedBase.getPort() == target.getPort();
    }

    private ResponseEntity<String> toEntity(RequestHeadersSpec<?> request) {
        return request.exchangeToMono(response -> response.toEntity(String.class))
                .block();
    }
}
