package edu.hawaii.its.holiday.controller;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    private static final Log logger = LogFactory.getLog(HomeController.class);

    //Mapping to home
    @RequestMapping(value = { "/", "/home" }, method = { RequestMethod.GET })
    public String home(Map<String, Object> model, Locale locale) {
        logger.info("User at home. The client locale is " + locale);
        return "home";
    }

    @GetMapping(value={"/campus","/campuses"})
    public String campus(){
        logger.debug("User at campus");
        return "campus";
    }

    @RequestMapping(value = "/faq", method = RequestMethod.GET)
    public String faq(Locale locale, Model model) {
        logger.info("User at faq.");
        return "faq";
    }

    @RequestMapping(value = "/404", method = RequestMethod.GET)
    public String invalid() {
        return "redirect:/";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String admin(Locale locale, Model model) {
        logger.info("User at admin.");
        return "admin";
    }

    @PreAuthorize("hasRole('UH')")
    @RequestMapping(value = "/memberships", method = RequestMethod.GET)
    public String memberships(Locale locale, Model model) {
        logger.info("User at memberships.");
        return "memberships";
    }

    @PreAuthorize("hasRole('ADMIN') || hasRole('OWNER')")
    @RequestMapping(value = "/groupings", method = RequestMethod.GET)
    public String groupings(Locale locale, Model model) {
        logger.info("User at groupings.");
        return "groupings";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Locale locale, Model model) {
        logger.info("User has logged in.");
        return "redirect:home";
    }
}
