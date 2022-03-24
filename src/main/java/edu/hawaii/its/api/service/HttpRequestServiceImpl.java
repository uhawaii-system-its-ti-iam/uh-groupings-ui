package edu.hawaii.its.api.service;

import edu.hawaii.its.api.controller.RestTemplateResponseErrorHandler;

import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.owasp.html.PolicyFactory;

@Service("httpRequestService")
public class HttpRequestServiceImpl implements HttpRequestService {

    private final PolicyFactory policy;

    public HttpRequestServiceImpl() {
        policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
    }

    @Value("${groupings.api.current_user}")
    private String CURRENT_USER;


    /*
     * Make an http request to the API with path variables.
     *
     * LGTM reporting a possible false positive: Groupings-1001
     */
    @Override
    public ResponseEntity<String> makeApiRequest(String currentUser, String uri, HttpMethod method) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CURRENT_USER, currentUser);
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate =
                new RestTemplateBuilder().errorHandler(new RestTemplateResponseErrorHandler()).build();
        String sanitizedUri = policy.sanitize(uri);
        return restTemplate.exchange(sanitizedUri, method, httpEntity, String.class);
    }

    /*
     * Make an http request to the API with path variables and description in the body.
     */
    @Override
    public ResponseEntity<String> makeApiRequestWithBody(String currentUser, String uri, String data,
            HttpMethod method) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CURRENT_USER, currentUser);
        HttpEntity<String> httpEntity = new HttpEntity<>(data, httpHeaders);

        RestTemplate restTemplate =
                new RestTemplateBuilder().errorHandler(new RestTemplateResponseErrorHandler()).build();
        return restTemplate.exchange(uri, method, httpEntity, String.class);
    }
}
