package edu.hawaii.its.groupings.controller;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import edu.hawaii.its.groupings.api.GroupingsService;
import edu.hawaii.its.groupings.api.GroupingsServiceImpl;
import edu.hawaii.its.groupings.api.PageService;
import edu.hawaii.its.groupings.api.PageServiceImpl;
import edu.hawaii.its.groupings.api.type.Grouping;
import edu.hawaii.its.groupings.api.type.MyGroupings;


@RestController
class PageController{
     final PageService pageservice;

     private static final Log logger = LogFactory.getLog(GroupingsRestController.class);

     @Value("${app.groupings.controller.uuid}")
     private String uuid;

     @Value("${app.iam.request.form}")
     private String requestForm;

     @Autowired
     private PageService ps;

    @Autowired
    public PageController(PageService pagecontent){
      this.pageservice = pagecontent;
    }

    @RequestMapping(value = "/page", method=RequestMethod.GET)
    public ResponseEntity<MyGroupings> myGroupings(@PathVariable String username) {
        logger.info("Entered REST myGroupings...");
        return ResponseEntity
                .ok()
                .body(ps.getMyGroupings(username));
              }
}
