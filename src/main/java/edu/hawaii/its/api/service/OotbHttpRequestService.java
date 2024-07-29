package edu.hawaii.its.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import edu.hawaii.its.groupings.type.OotbActiveProfile;
import edu.hawaii.its.groupings.type.OotbMember;

@Service("ootbHttpRequestService")
public class OotbHttpRequestService {

    @Value("${groupings.api.current_user}")
    private String CURRENT_USER;

    private final WebClient webClient;

    public OotbHttpRequestService() {
        webClient = WebClient.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(-1))
                .build();
    }
    public ResponseEntity<OotbActiveProfile> makeApiRequestWithActiveProfileBody(String currentUser, String uri, OotbActiveProfile data,
                                                         HttpMethod method) {
        return webClient.method(method)
                .uri(uri)
                .header(CURRENT_USER, currentUser)
                .bodyValue(data)
                .retrieve()
                .toEntity(OotbActiveProfile.class)
                .block();
    }
}
