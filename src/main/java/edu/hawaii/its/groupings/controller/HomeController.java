package edu.hawaii.its.groupings.controller;

import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.service.EmailService;
import edu.hawaii.its.groupings.type.Feedback;

@Controller
public class HomeController {

    private static final Log logger = LogFactory.getLog(HomeController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserContextService userContextService;

    // Mapping to home.
    @GetMapping(value = { "/", "/home" })
    public String home(Map<String, Locale> locale) {
        logger.info("User at home. The client locale is " + locale);
        return "home";
    }

    @GetMapping(value = "/about")
    public String about() {
        logger.info("User at about.");
        return "about";
    }

    @GetMapping(value = "/404")
    public String invalid() {
        return "redirect:/";
    }

    @GetMapping(value = "/uhuuid-error")
    public String uhUuidError(Model model,
            @SessionAttribute("login.error.message") String errormsg,
            @SessionAttribute("login.error.exception.message") String exceptionmsg) {
        logger.info("User at uhuuid-error.");
        model.addAttribute("loginErrorMessage", errormsg);
        model.addAttribute("loginErrorExceptionMessage", exceptionmsg);
        return "uhuuid-error";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/admin")
    public String admin() {
        logger.info("User at admin.");
        return "admin";
    }

    @PreAuthorize("hasRole('UH')")
    @GetMapping(value = "/memberships")
    public String memberships() {
        logger.info("User at memberships.");
        return "memberships";
    }

    //@PreAuthorize("hasRole('ADMIN') || hasRole('OWNER')")
    @GetMapping(value = "/groupings")
    public String groupings() {
        logger.info("User at groupings.");
        return "groupings";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/login")
    public String login() {
        logger.info("User has logged in.");
        return "home";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/feedback")
    public String feedbackForm(Model model, HttpSession session) {
        logger.info("User has entered feedback page.");
        Feedback sessionFeedback = (Feedback) session.getAttribute("feedback");
        if (sessionFeedback != null) {
            sessionFeedback.setType("problem");
            sessionFeedback.setEmail(userContextService.getCurrentUid() + "@hawaii.edu");
            model.addAttribute("feedback", sessionFeedback);
        } else {
            Feedback feedback = new Feedback();
            feedback.setType("general");
            feedback.setEmail(userContextService.getCurrentUid() + "@hawaii.edu");
            model.addAttribute("feedback", feedback);
        }
        return "feedback";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/feedback")
    public String feedbackSubmit(@ModelAttribute Feedback feedback, Model model,
            RedirectAttributes redirectAttributes) {
        logger.info("User has submitted feedback.");
        emailService.send(feedback);
        // Ensure the feedback form is reset after submission.
        model.addAttribute("feedback", new Feedback());
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/feedback";
    }

    /**
     * Modal Pages
     */
    @GetMapping(value = "/modal/{modalName}")
    public String openModalPath(@PathVariable String modalName) {
        return "modal/" + modalName;
    }

}
