package edu.hawaii.its.groupings.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.hawaii.its.groupings.service.CampusService;
import edu.hawaii.its.groupings.type.Campus;

@RestController
public class CampusRestController {

    private static final Log logger = LogFactory.getLog(CampusRestController.class);

    @Autowired
    private CampusService campusService;

    @GetMapping(value = "/api/campuses",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Campus>> campuses() {
        logger.info("Entered REST campuses...");
        List<Campus> data = campusService.findActualAll();
        return ResponseEntity
                .ok()
                .body(data);
    }
}
