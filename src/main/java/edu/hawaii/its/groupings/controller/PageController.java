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

import edu.hawaii.its.groupings.controller.GroupingsService;
import edu.hawaii.its.groupings.controller.GroupingsServiceImpl;
import edu.hawaii.its.groupings.controller.PageService;
import edu.hawaii.its.groupings.controller.PageServiceImpl;
import edu.hawaii.its.groupings.api.type.Grouping;
// import edu.hawaii.its.groupings.controller.PersonRepository;
import edu.hawaii.its.groupings.api.type.MyGroupings;
import org.springframework.data.domain.Page;


@RestController
class PageController{
     final PageService pageservice;

     private static final Log logger = LogFactory.getLog(GroupingsRestController.class);

     private PersonService personService;

     @Value("${app.groupings.controller.uuid}")
     private String uuid;

     @Value("${app.iam.request.form}")
     private String requestForm;

     @Autowired
     private PageService ps;

    @Autowired
    public void PageController(PageService pagecontent){
      this.pageservice = pagecontent;
    }
    //this will be the call so when i get the user name, it gets it in pages from the Page class
    //and it gets it from 
    // /{username}/myGroupings/
    @RequestMapping(value = "{username}/members/pages", method=RequestMethod.GET)
        public Page<Grouping> listAllByPage(String username){
            Page<Grouping> groups = personService.listAllByPage(username);
            return groups;
        }

    // maybe i should stick with the tutorial for now. lets see if i can do that tutorial within here
    // public ResponseEntity<MyGroupings> myGroupings(@PathVariable String username) {
    //     logger.info("Entered REST myGroupings...");
    //     return ResponseEntity
    //             .ok()
    //             .body(ps.getMyGroupings(username));
    //           }
}
