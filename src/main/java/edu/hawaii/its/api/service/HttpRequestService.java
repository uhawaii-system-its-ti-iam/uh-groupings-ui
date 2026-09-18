package edu.hawaii.its.api.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.util.UriComponentsBuilder;

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
    private final String apiBase;
    private final WebClient webClient;

    public HttpRequestService(JwtService jwtService, @Value("${url.api.2.1.base}") String apiBase) {
        this.jwtService = jwtService;
        this.apiBase = trimTrailingSlash(apiBase);
        if (this.apiBase == null || this.apiBase.isBlank()) {
            throw new IllegalArgumentException("Property 'url.api.2.1.base' is required.");
        }
        webClient = WebClient.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(-1))
                .build();
    }

    /*
     * Make a http request to the API with path variables.
     */
    public ResponseEntity<String> makeApiRequest(String uri, HttpMethod method) {
        return relay(toEntity(webClient.method(method)
                .uri(resolveApiUri(uri))
                .header("Authorization", "Bearer " + jwtService.generateToken())));
    }

    /*
     * Make an HTTP request to the API without a JWT.
     */
    public ResponseEntity<String> makeApiRequestWithoutJwt(String uri, HttpMethod method) {
        return relay(toEntity(webClient.method(method)
                .uri(resolveApiUri(uri))));
    }

    /*
     * Make a http request to the API with path variables and description string in the body.
     */
    public ResponseEntity<String> makeApiRequestWithBody(String uri, String data,
            HttpMethod method) {
        return relay(toEntity(webClient.method(method)
                .uri(resolveApiUri(uri))
                .header("Authorization", "Bearer " + jwtService.generateToken())
                .bodyValue(data)));
    }

    /*
     * Make a http request to the API with path variables and description list of strings in the body.
     */
    public ResponseEntity<String> makeApiRequestWithBody(String uri, List<String> data,
            HttpMethod method) {
        return relay(toEntity(webClient.method(method)
                .uri(resolveApiUri(uri))
                .header("Authorization", "Bearer " + jwtService.generateToken())
                .bodyValue(data)));
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

    /**
     * Build the request URI from the configured API base only. Controllers may still pass full
     * {@code url.api.2.1.base + path} strings; those are reduced to a relative path first so a
     * caller cannot point WebClient at a different host.
     */
    URI resolveApiUri(String uri) {
        String relative = toRelativePath(uri);
        String path = relative;
        String query = null;
        int queryIndex = relative.indexOf('?');
        if (queryIndex >= 0) {
            path = relative.substring(0, queryIndex);
            query = relative.substring(queryIndex + 1);
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiBase);
        if (!"/".equals(path)) {
            builder.path(path);
        } else {
            builder.path("/");
        }
        if (query != null && !query.isEmpty()) {
            builder.replaceQuery(query);
        }
        return builder.build(true).toUri();
    }

    /**
     * Reduce a full API URL or relative path to a path (and optional query) under the configured base.
     */
    String toRelativePath(String uri) {
        if (uri == null || uri.isEmpty()) {
            throw new IllegalArgumentException("API request path must not be empty.");
        }

        String path;
        if (startsWithConfiguredBase(uri)) {
            path = uri.substring(apiBase.length());
            if (path.isEmpty()) {
                path = "/";
            }
        } else if (uri.startsWith("/") && !uri.startsWith("//") && !uri.contains("://")) {
            path = uri;
        } else {
            throw new IllegalArgumentException("API request must target the configured API base URL.");
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.contains("://") || path.startsWith("//")) {
            throw new IllegalArgumentException("API request path must be relative to the configured API base URL.");
        }
        return path;
    }

    private boolean startsWithConfiguredBase(String uri) {
        if (!uri.startsWith(apiBase)) {
            return false;
        }
        if (uri.length() == apiBase.length()) {
            return true;
        }
        char next = uri.charAt(apiBase.length());
        return next == '/' || next == '?';
    }

    private static String trimTrailingSlash(String base) {
        if (base != null && base.endsWith("/")) {
            return base.substring(0, base.length() - 1);
        }
        return base;
    }

    private ResponseEntity<String> toEntity(RequestHeadersSpec<?> request) {
        return request.exchangeToMono(response -> response.toEntity(String.class))
                .block();
    }
}
