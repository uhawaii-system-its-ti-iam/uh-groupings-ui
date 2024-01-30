package edu.hawaii.its.api.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.hawaii.its.api.service.HttpRequestService;
import edu.hawaii.its.groupings.access.UserContextService;
@RestController
@RequestMapping("/api/groupings/testing")
public class ForTestingController {

    private static final Log logger = LogFactory.getLog(ForTestingController.class);

    @Value("${url.api.2.1.base}/testing")
    private String API_2_1_BASE;

    @Autowired
    private HttpRequestService httpRequestService;

    @Autowired
    private UserContextService userContextService;

    @GetMapping(value = "/exception")
    public ResponseEntity<String> throwException() {
        logger.info("Entered REST throwException...");
        String currentUid = userContextService.getCurrentUid();
        String uri = String.format(API_2_1_BASE + "/exception");
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.GET);
    }
}
