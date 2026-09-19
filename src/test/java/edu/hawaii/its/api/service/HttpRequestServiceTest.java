package edu.hawaii.its.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
