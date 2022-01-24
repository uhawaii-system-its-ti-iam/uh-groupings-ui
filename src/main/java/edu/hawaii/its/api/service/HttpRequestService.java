package edu.hawaii.its.api.service;

import edu.hawaii.its.api.controller.RestTemplateResponseErrorHandler;

import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.access.UserContextService;
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
public class HttpRequestService{

	@Autowired
	private UserContextService userContextService;

    @Value("${groupings.api.current_user}")
    private String KEY_CURRENT_USER;

    /*
     * Make an http request to the API with path variables.
     */
    @SuppressWarnings("lgtm[java/xss]")
    public ResponseEntity<String> makeApiRequest(String user, String uri, HttpMethod method) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(KEY_CURRENT_USER, user);
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

		RestTemplate restTemplate = restTemplate();
        return restTemplate.exchange(uri, method, httpEntity, String.class);
    }

	public ResponseEntity<String> makeApiRequest(String uri, HttpMethod method) {
		return makeApiRequest(currentUser().getName(), uri, method);
	}

    /*
     * Make an http request to the API with path variables and description in the body.
     */
    @SuppressWarnings("lgtm[java/xss]")
    public ResponseEntity<String> makeApiRequestWithBody(String user, String uri, String data,
            HttpMethod method) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(KEY_CURRENT_USER, user);
        HttpEntity<String> httpEntity = new HttpEntity<>(data, httpHeaders);

		RestTemplate restTemplate = restTemplate();
        return restTemplate.exchange(uri, method, httpEntity, String.class);
    }

	public ResponseEntity<String> makeApiRequestWithBody(String uri, String data, HttpMethod method) {
		return makeApiRequestWithBody(currentUser().getName(), uri, data, method);
	}

	private User currentUser() {
		return userContextService.getCurrentUser();
	}

	private RestTemplate restTemplate() {
		return new RestTemplateBuilder()
				.errorHandler(new RestTemplateResponseErrorHandler())
				.build();
	}
}
