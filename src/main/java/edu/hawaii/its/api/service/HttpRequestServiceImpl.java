package edu.hawaii.its.api.service;

import edu.hawaii.its.api.controller.RestTemplateResponseErrorHandler;
import edu.hawaii.its.api.type.GroupingsHTTPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("httpRequestService")
public class HttpRequestServiceImpl implements HttpRequestService {

    @Value("${groupings.api.current_user}")
    private String CURRENT_USER;

    private RestTemplate restTemplate;

    @Autowired
    public HttpRequestServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        //todo should this not be declaring a new variable named restTemplate?
        restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }

    @Override
    public ResponseEntity makeApiRequest(String currentUser, String uri, HttpMethod method) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CURRENT_USER, currentUser);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        //todo why do we need the class fromm the body of the rest template rather than RestTemplate.class?
        return restTemplate.exchange(uri, method, httpEntity, String.class);
    }
}
