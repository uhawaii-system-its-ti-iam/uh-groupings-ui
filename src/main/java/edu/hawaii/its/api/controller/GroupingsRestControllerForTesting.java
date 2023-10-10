package edu.hawaii.its.api.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.hawaii.its.api.service.HttpRequestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/groupings/testing")
public class GroupingsRestControllerForTesting {

    private static final Log logger = LogFactory.getLog(GroupingsRestControllerForTesting.class);

    @Value("${url.api.2.1.base}/testing")
    private String API_2_1_BASE;

    @Autowired
    private HttpRequestService httpRequestService;

    @GetMapping(value = "/exception")
    public ResponseEntity<String> throwException(Principal principal) {
        logger.info("Entered REST throwException...");
        String uri = String.format(API_2_1_BASE + "/exception");
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }
}
