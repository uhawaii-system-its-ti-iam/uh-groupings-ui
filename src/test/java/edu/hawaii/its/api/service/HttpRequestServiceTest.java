package edu.hawaii.its.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import edu.hawaii.its.groupings.service.JwtService;

public class HttpRequestServiceTest {

    private HttpRequestService httpRequestService;
    private HttpServer server;
    private String apiBase;

    @BeforeEach
    public void setUp() throws IOException {
        JwtService jwtService = mock(JwtService.class);
        when(jwtService.generateToken()).thenReturn("test-token");

        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.start();
        apiBase = "http://localhost:" + server.getAddress().getPort();
        httpRequestService = new HttpRequestService(jwtService, apiBase);
    }

    @AfterEach
    public void tearDown() {
        server.stop(0);
    }

    @Test
    public void makeApiRequestPreservesApiErrorStatusAndBody() {
        String body = "{"
                + "\"status\":503,"
                + "\"resultCode\":\"BACKEND_UNAVAILABLE\","
                + "\"message\":\"Groupings data is temporarily unavailable. Please try again later.\","
                + "\"path\":\"/api/groupings/groupings\","
                + "\"timestamp\":\"2026-06-16T12:00:00\""
                + "}";
        server.createContext("/api/groupings/groupings", exchange -> sendJson(exchange, body));

        ResponseEntity<String> response = httpRequestService.makeApiRequest(
                apiBase + "/api/groupings/groupings",
                HttpMethod.GET);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(body, response.getBody());
        assertTrue(response.getBody().contains("\"resultCode\":\"BACKEND_UNAVAILABLE\""));
    }

    @Test
    public void makeApiRequestAcceptsRelativePath() {
        String body = "{\"resultCode\":\"SUCCESS\"}";
        server.createContext("/api/groupings/groupings", exchange -> sendJson(exchange, body));

        ResponseEntity<String> response = httpRequestService.makeApiRequest(
                "/api/groupings/groupings",
                HttpMethod.GET);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(body, response.getBody());
    }

    /*
     * API response headers must not be forwarded. Hop-by-hop headers such as
     * Transfer-Encoding leaking into the UI's response caused the F5 to reset the connection.
     */
    @Test
    public void makeApiRequestDoesNotForwardApiResponseHeaders() {
        String body = "{\"resultCode\":\"SUCCESS\"}";
        server.createContext("/members/memberships", exchange -> {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Connection", "close");
            exchange.getResponseHeaders().set("Keep-Alive", "timeout=5");
            exchange.getResponseHeaders().set("X-Cnection", "close");
            exchange.getResponseHeaders().set("X-Api-Header", "api-value");
            // A response length of 0 makes the server send the body with chunked transfer encoding.
            exchange.sendResponseHeaders(HttpStatus.OK.value(), 0);
            exchange.getResponseBody().write(body.getBytes(StandardCharsets.UTF_8));
            exchange.close();
        });

        ResponseEntity<String> response = httpRequestService.makeApiRequest(
                "/members/memberships",
                HttpMethod.GET);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(body, response.getBody());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        for (String header : new String[] { "Transfer-Encoding", "Content-Length", "Connection",
                "Keep-Alive", "X-Cnection", "X-Api-Header", "Date" }) {
            assertNull(response.getHeaders().getFirst(header), header + " should not be forwarded");
        }
    }

    @Test
    public void makeApiRequestHandlesEmptyResponseWithoutContentType() {
        server.createContext("/members/memberships", exchange -> {
            exchange.sendResponseHeaders(HttpStatus.NO_CONTENT.value(), -1);
            exchange.close();
        });

        ResponseEntity<String> response = httpRequestService.makeApiRequest(
                "/members/memberships",
                HttpMethod.GET);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        assertNull(response.getHeaders().getContentType());
    }

    @Test
    public void toRelativePathStripsConfiguredBase() {
        assertEquals("/members", httpRequestService.toRelativePath(apiBase + "/members"));
        assertEquals("/", httpRequestService.toRelativePath(apiBase + "/"));
        assertEquals("/", httpRequestService.toRelativePath(apiBase));
    }

    @Test
    public void resolveApiUriUsesConfiguredBaseOnly() {
        assertEquals(apiBase + "/members",
                httpRequestService.resolveApiUri(apiBase + "/members").toString());
        assertEquals(apiBase + "/members",
                httpRequestService.resolveApiUri("/members").toString());
        assertEquals(apiBase + "/groupings/group?page=1&size=2",
                httpRequestService.resolveApiUri(apiBase + "/groupings/group?page=1&size=2").toString());
    }

    @Test
    public void resolveApiUriPreservesConfiguredBasePath() {
        JwtService jwtService = mock(JwtService.class);
        HttpRequestService service = new HttpRequestService(jwtService,
                "http://localhost:8081/uhgroupingsapi/api/groupings/v2.1");

        assertEquals("http://localhost:8081/uhgroupingsapi/api/groupings/v2.1/members",
                service.resolveApiUri("http://localhost:8081/uhgroupingsapi/api/groupings/v2.1/members").toString());
        assertEquals("http://localhost:8081/uhgroupingsapi/api/groupings/v2.1/",
                service.resolveApiUri("http://localhost:8081/uhgroupingsapi/api/groupings/v2.1/").toString());
    }

    @Test
    public void constructorRequiresApiBase() {
        JwtService jwtService = mock(JwtService.class);
        assertThrows(IllegalArgumentException.class,
                () -> new HttpRequestService(jwtService, null));
        assertThrows(IllegalArgumentException.class,
                () -> new HttpRequestService(jwtService, " "));
    }

    @Test
    public void toRelativePathRejectsForeignHost() {
        assertThrows(IllegalArgumentException.class,
                () -> httpRequestService.toRelativePath("http://evil.example/members"));
        assertThrows(IllegalArgumentException.class,
                () -> httpRequestService.toRelativePath("//evil.example/members"));
        assertThrows(IllegalArgumentException.class,
                () -> httpRequestService.resolveApiUri("http://evil.example/members"));
    }

    @Test
    public void toRelativePathRejectsPrefixThatIsNotBaseBoundary() {
        JwtService jwtService = mock(JwtService.class);
        HttpRequestService service = new HttpRequestService(jwtService, "http://localhost:8081/api");

        assertThrows(IllegalArgumentException.class,
                () -> service.toRelativePath("http://localhost:8081/apix/members"));
        assertEquals("/members", service.toRelativePath("http://localhost:8081/api/members"));
    }

    private void sendJson(HttpExchange exchange, String body) throws IOException {
        assertEquals("Bearer test-token", exchange.getRequestHeaders().getFirst("Authorization"));
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(HttpStatus.SERVICE_UNAVAILABLE.value(), bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
