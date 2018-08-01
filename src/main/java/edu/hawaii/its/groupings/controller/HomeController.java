package edu.hawaii.its.groupings.controller;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

import edu.hawaii.its.groupings.service.EmailService;
import edu.hawaii.its.groupings.type.Feedback;
import edu.hawaii.its.groupings.access.UserContextService;

@Controller
public class HomeController {

    private static final Log logger = LogFactory.getLog(HomeController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserContextService userContextService;

    // Mapping to home.
    @RequestMapping(value = {"/", "/home"}, method = {RequestMethod.GET})
    public String home(Map<String, Object> model, Locale locale) {
        logger.info("User at home. The client locale is " + locale);
        return "home";
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public String info(Locale locale, Model model) {
        logger.info("User at info.");
        return "info";
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/feedback")
    public String feedbackForm(Model model, HttpSession session) {
        logger.info("User has entered feedback page.");
        Feedback sessionFeedback = (Feedback) session.getAttribute("feedback");
        if (sessionFeedback != null) {
            sessionFeedback.setType("problem");
            sessionFeedback.setEmail(userContextService.getCurrentUsername() + "@hawaii.edu");
            model.addAttribute("feedback", sessionFeedback);
            session.removeAttribute("feedback");
        } else {
            Feedback feedback = new Feedback();
            feedback.setType("general");
            feedback.setEmail(userContextService.getCurrentUsername() + "@hawaii.edu");
            model.addAttribute("feedback", feedback);
        }
        return "feedback";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/feedback")
    public String feedbackSubmit(@ModelAttribute Feedback feedback, Model model, RedirectAttributes redirectAttributes) {
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
    @RequestMapping(value = "/modal/infoModal", method = RequestMethod.GET)
    public String infoModal(Locale locale, Model model) {
        return "modal/infoModal";
    }

    @RequestMapping(value = "/modal/checkModal", method = RequestMethod.GET)
    public String checkModal(Locale locale, Model model) {
        return "modal/checkModal";
    }

    @RequestMapping(value = "/modal/apiError", method = RequestMethod.GET)
    public String apiError(Locale locale, Model model) {
        return "modal/apiError";
    }

    @RequestMapping(value = "/modal/preferenceErrorModal", method = RequestMethod.GET)
    public String preferenceErrorModal(Locale locale, Model model) {
        return "modal/preferenceErrorModal";
    }

    @RequestMapping(value = "/modal/addModal", method = RequestMethod.GET)
    public String addModal(Locale locale, Model model) {
        return "modal/addModal";
    }

    @RequestMapping(value = "/modal/removeModal", method = RequestMethod.GET)
    public String removeModal(Locale locale, Model model) {
        return "modal/removeModal";
    }

    @RequestMapping(value = "/modal/optModal", method = RequestMethod.GET)
    public String optModal(Locale locale, Model model) {
        return "modal/optModal";
    }

    @RequestMapping(value = "/modal/confirmAddModal", method = RequestMethod.GET)
    public String confirmAddModal(Locale locale, Model model) {
        return "modal/confirmAddModal";
    }

}
