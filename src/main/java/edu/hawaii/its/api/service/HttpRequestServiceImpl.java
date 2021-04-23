package edu.hawaii.its.api.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.hawaii.its.api.controller.GroupingsRestController;
import edu.hawaii.its.api.controller.RestTemplateResponseErrorHandler;

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

    private static final Log logger = LogFactory.getLog(GroupingsRestController.class);

    @Value("${groupings.api.current_user}")
    private String CURRENT_USER;

    /*
     * Make an http request to the API with path variables.
     */
    @SuppressWarnings("lgtm[java/xss]")
    @Override
    public ResponseEntity<String> makeApiRequest(String currentUser, String uri, HttpMethod method) {
        logger.info("API request started.");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CURRENT_USER, currentUser);
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate =
                new RestTemplateBuilder().errorHandler(new RestTemplateResponseErrorHandler()).build();
        return restTemplate.exchange(uri, method, httpEntity, String.class);
    }

    /*
     * Make an http request to the API with path variables and description in the body.
     */
    @SuppressWarnings("lgtm[java/xss]")
    @Override
    public ResponseEntity<String> makeApiRequestWithBody(String currentUser, String uri, String data,
            HttpMethod method) {
        logger.info("API request with body started.");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CURRENT_USER, currentUser);
        HttpEntity<String> httpEntity = new HttpEntity<>(data, httpHeaders);

        RestTemplate restTemplate =
                new RestTemplateBuilder().errorHandler(new RestTemplateResponseErrorHandler()).build();
        return restTemplate.exchange(uri, method, httpEntity, String.class);
    }
}
