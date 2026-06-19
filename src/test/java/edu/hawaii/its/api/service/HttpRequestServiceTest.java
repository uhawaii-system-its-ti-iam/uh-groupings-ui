package edu.hawaii.its.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @BeforeEach
    public void setUp() throws IOException {
        JwtService jwtService = mock(JwtService.class);
        when(jwtService.generateToken()).thenReturn("test-token");
        httpRequestService = new HttpRequestService(jwtService);

        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.start();
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
                "http://localhost:" + server.getAddress().getPort() + "/api/groupings/groupings",
                HttpMethod.GET);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(body, response.getBody());
        assertTrue(response.getBody().contains("\"resultCode\":\"BACKEND_UNAVAILABLE\""));
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
