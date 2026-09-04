package edu.hawaii.its.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import edu.hawaii.its.groupings.service.JwtService;

import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

/**
 * Verifies that responses relayed from the API are re-framed by this application rather than
 * being forwarded verbatim.
 *
 * <p>Regression test for the announcements/memberships outage seen behind the F5 proxy: the
 * upstream response headers were copied onto the response sent to the browser, which produced a
 * duplicated {@code Transfer-Encoding: chunked} header once the servlet container added its own
 * framing. Strict intermediaries reject such a message (RFC 9112 section 6.1), while Apache
 * silently re-framed it, which is why the fault only appeared once Apache left the request path.
 */
public class HttpRequestServiceTest {

    private DisposableServer server;
    private HttpRequestService httpRequestService;

    /** Headers the upstream API emits that must not be relayed onward. */
    private static final List<String> LEAKY_HEADERS = List.of(
            HttpHeaders.TRANSFER_ENCODING,
            HttpHeaders.CONNECTION,
            HttpHeaders.DATE,
            "Strict-Transport-Security",
            "X-Frame-Options");

    @BeforeEach
    public void setUp() {
        // Stand in for the API. Sending a body without a content length makes the response
        // chunked, which is exactly what the real API does for these endpoints.
        server = HttpServer.create()
                .port(0)
                .handle((request, response) -> {
                    response.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    response.header("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains");
                    response.header("X-Frame-Options", "DENY");
                    return response.sendString(Mono.just("{\"resultCode\":\"SUCCESS\",\"announcements\":[]}"));
                })
                .bindNow();

        JwtService jwtService = mock(JwtService.class);
        when(jwtService.generateToken()).thenReturn("test-token");
        httpRequestService = new HttpRequestService(jwtService);
    }

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.disposeNow();
        }
    }

    private String uri() {
        return "http://localhost:" + server.port() + "/announcements";
    }

    @Test
    public void relayedResponseDoesNotForwardUpstreamFramingHeaders() {
        ResponseEntity<String> response = httpRequestService.makeApiRequest(uri(), HttpMethod.GET);

        for (String leaked : LEAKY_HEADERS) {
            assertFalse(response.getHeaders().containsHeader(leaked),
                    "Upstream header must not be relayed to the browser: " + leaked);
        }
    }

    @Test
    public void relayedResponsePreservesStatusAndBody() {
        ResponseEntity<String> response = httpRequestService.makeApiRequest(uri(), HttpMethod.GET);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("{\"resultCode\":\"SUCCESS\",\"announcements\":[]}", response.getBody());
    }

    @Test
    public void relayedResponseDeclaresJsonWithAnExplicitCharset() {
        ResponseEntity<String> response = httpRequestService.makeApiRequest(uri(), HttpMethod.GET);

        MediaType contentType = response.getHeaders().getContentType();
        assertTrue(MediaType.APPLICATION_JSON.isCompatibleWith(contentType),
                "Expected JSON content type but was: " + contentType);
        // Without an explicit charset the body may be re-encoded with a different one than the
        // container uses to compute Content-Length, truncating payloads that contain the
        // Hawaiian okina and kahako.
        assertEquals(StandardCharsets.UTF_8, contentType.getCharset(),
                "Content-Type must state the charset so Content-Length matches the bytes written");
    }

    @Test
    public void relayedResponseCarriesOnlyTheContentTypeHeader() {
        ResponseEntity<String> response = httpRequestService.makeApiRequest(uri(), HttpMethod.GET);

        assertEquals(List.of(HttpHeaders.CONTENT_TYPE.toLowerCase()),
                response.getHeaders().headerNames().stream().map(String::toLowerCase).sorted().toList(),
                "Only Content-Type should be set; the container supplies the framing headers");
    }

    @Test
    public void relayedResponseWithBodyDoesNotForwardUpstreamFramingHeaders() {
        ResponseEntity<String> response =
                httpRequestService.makeApiRequestWithBody(uri(), List.of("uid"), HttpMethod.POST);

        for (String leaked : LEAKY_HEADERS) {
            assertFalse(response.getHeaders().containsHeader(leaked),
                    "Upstream header must not be relayed to the browser: " + leaked);
        }
    }
}

