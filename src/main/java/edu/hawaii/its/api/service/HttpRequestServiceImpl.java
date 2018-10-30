package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.GroupingsHTTPException;
import org.springframework.beans.factory.annotation.Value;
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

    @Override
    public ResponseEntity makeApiRequest(String currentUser, String uri, HttpMethod method, Class responseClass) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CURRENT_USER, currentUser);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        try {
            return restTemplate.exchange(uri, method, httpEntity, responseClass);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }
}
