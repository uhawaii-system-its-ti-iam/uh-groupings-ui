package edu.hawaii.its.groupings.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.service.EmailService;
import edu.hawaii.its.groupings.type.Feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Locale;
import java.util.Map;

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

    @GetMapping(value = "/modal/apiError")
    public String apiError() {
        return "modal/apiError";
    }

    @GetMapping(value = "/modal/preferenceErrorModal")
    public String preferenceErrorModal() {
        return "modal/preferenceErrorModal";
    }

    @GetMapping(value = "/modal/addModal")
    public String addModal() {
        return "modal/addModal";
    }

    @GetMapping(value = "/modal/multiAddModal")
    public String multiAddModal() {
        return "modal/multiAddModal";
    }

    @GetMapping(value = "/modal/removeModal")
    public String removeModal() {
        return "modal/removeModal";
    }

    @GetMapping(value = "/modal/multiRemoveModal")
    public String multiRemoveModal() {
        return "modal/multiRemoveModal";
    }

    @GetMapping(value = "/modal/resetModal")
    public String resetModal() {
        return "modal/resetModal";
    }

    @GetMapping(value = "/modal/successfulGroupResetModal")
    public String successfulGroupResetModal() {
        return "modal/successfulGroupResetModal";
    }

    @GetMapping(value = "/modal/removeFromGroupsModal")
    public String removeFromGroupsModal() {
        return "modal/removeFromGroupsModal";
    }

    @GetMapping(value = "/modal/emptyGroupModal")
    public String emptyGroupModal() {
        return "modal/emptyGroupModal";
    }

    @GetMapping(value = "/modal/syncDestModal")
    public String syncDestModal() {
        return "modal/syncDestModal";
    }

    @GetMapping(value = "/modal/removeErrorModal")
    public String removeErrorModal() {
        return "modal/removeErrorModal";
    }

    @GetMapping(value = "/modal/timeoutModal")
    public String timeoutModal() {
        return "modal/timeoutModal";
    }

    @GetMapping(value = "/modal/roleErrorModal")
    public String roleErrorModal() {
        return "modal/roleErrorModal";
    }

    @GetMapping(value = "/modal/ownerErrorModal")
    public String ownerErrorModal() {
        return "modal/ownerErrorModal";
    }

    @GetMapping(value = "/modal/optErrorModal")
    public String optErrorModal() {
        return "modal/optErrorModal";
    }

    @GetMapping(value = "/modal/importModal")
    public String importModal() {
        return "modal/importModal";
    }

    @GetMapping(value = "/modal/importConfirmationModal")
    public String importConfirmationModal() {
        return "modal/importConfirmationModal";
    }

    @GetMapping(value = "/modal/importSuccessModal")
    public String importSuccessModal() {
        return "modal/importSuccessModal";
    }

    @GetMapping(value = "modal/importErrorModal")
    public String importErrorModal() {
        return "modal/importErrorModal";
    }

    @GetMapping(value = "/modal/dynamicModal")
    public String dynamicModal() {
        return "modal/dynamicModal";
    }

    @GetMapping(value = "/modal/groupingOwnersModal")
    public String groupingOwnersModal() {
        return "modal/groupingOwnersModal";
    }
}
