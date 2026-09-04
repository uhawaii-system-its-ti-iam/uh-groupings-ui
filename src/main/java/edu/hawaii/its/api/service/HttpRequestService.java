package edu.hawaii.its.api.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import edu.hawaii.its.groupings.service.JwtService;

@Service("httpRequestService")
public class HttpRequestService {

    /**
     * The content type used for every response relayed back to the browser. The charset is stated
     * explicitly so that the bytes written by the servlet container always match the Content-Length
     * header it computes, even when the payload contains non-ASCII characters (e.g. ʻokina, kahakō).
     */
    private static final MediaType RELAY_CONTENT_TYPE =
            new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);

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
        return relay(webClient.method(method)
                .uri(uri)
                .header("Authorization", "Bearer " + jwtService.generateToken())
                .retrieve()
                .toEntity(String.class)
                .block());
    }

    /*
     * Make a http request to the API with path variables and description string in the body.
     */
    public ResponseEntity<String> makeApiRequestWithBody(String uri, String data,
            HttpMethod method) {
        return relay(webClient.method(method)
                .uri(uri)
                .header("Authorization", "Bearer " + jwtService.generateToken())
                .bodyValue(data)
                .retrieve()
                .toEntity(String.class)
                .block());
    }

    /*
     * Make a http request to the API with path variables and description list of strings in the body.
     */
    public ResponseEntity<String> makeApiRequestWithBody(String uri, List<String> data,
            HttpMethod method) {
        return relay(webClient.method(method)
                .uri(uri)
                .header("Authorization", "Bearer " + jwtService.generateToken())
                .bodyValue(data)
                .retrieve()
                .toEntity(String.class)
                .block());
    }

    /**
     * Rebuild the API's response so that only the status code and body are relayed onward.
     *
     * <p>The {@code ResponseEntity} returned by {@code WebClient} carries the API's raw wire headers.
     * Returning it directly from a Spring MVC controller copies those headers onto the response sent
     * to the browser, which leaks hop-by-hop and framing headers such as {@code Transfer-Encoding},
     * {@code Content-Length}, {@code Connection} and {@code Keep-Alive}, and duplicates {@code Date}
     * and the Spring Security headers that this application already emits. A response advertising
     * both {@code Content-Length} and {@code Transfer-Encoding}, or a stale {@code Content-Length}
     * that no longer matches the re-encoded body, is treated as a desync/smuggling attempt by strict
     * intermediaries (F5 BIG-IP, most WAFs) and is dropped, while lenient ones (Apache mod_proxy)
     * silently repair it. Discarding the upstream headers lets the servlet container frame the
     * response correctly.
     */
    private ResponseEntity<String> relay(ResponseEntity<String> response) {
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(response.getStatusCode())
                .contentType(RELAY_CONTENT_TYPE)
                .body(response.getBody());
    }
}
