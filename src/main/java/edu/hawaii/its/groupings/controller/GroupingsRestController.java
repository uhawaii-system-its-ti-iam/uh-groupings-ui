package edu.hawaii.its.groupings.controller;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.hawaii.its.groupings.type.Owner;
import edu.hawaii.its.groupings.api.GroupingsService;

@RestController
public class GroupingsRestController {

    private static final Log logger = LogFactory.getLog(GroupingsRestController.class);

    @Value("${app.groupings.controller.uuid}")
    private String uuid;

    @Autowired
    private GroupingsService groupingsService;

    @PostConstruct
    public void init() {
        Assert.hasLength(uuid, "Property 'app.groupings.controller.uuid' is required.");
        logger.info("GroupingsRestController started.");
    }

    @RequestMapping(value = "/api/groupings/{groupingName}/owners/",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Owner>> owners(@PathVariable String groupingName) {
        logger.info("Entered REST owners...");

        String username = "_api_groupings";
        List<Owner> owners = groupingsService.findOwners(username, groupingName);

        return ResponseEntity
                .ok()
                .body(owners);
    }
}
