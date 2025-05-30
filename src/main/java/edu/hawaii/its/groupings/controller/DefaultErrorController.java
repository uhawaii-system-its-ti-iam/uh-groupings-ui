package edu.hawaii.its.groupings.controller;

import java.util.Map;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.service.EmailService;

/**
 * This class serves as a layer of defense in case of an unhandled error (exception)
 * by the @ExceptionHandlers in the @ControllerAdvice class. Spring defaults to the
 * /error endpoint in case of an unhandled error, which allows us to render an HTML
 * page with attributes of interest instead of showing a JSON error object.
 */
@Controller
public class DefaultErrorController implements ErrorController {

    private static final Log logger = LogFactory.getLog(DefaultErrorController.class);

    private final DefaultErrorAttributes errorAttributes;
    private final EmailService emailService;
    private final UserContextService userContextService;

    public DefaultErrorController(DefaultErrorAttributes errorAttributes, EmailService emailService,
            UserContextService userContextService) {
        this.errorAttributes = errorAttributes;
        this.emailService = emailService;
        this.userContextService = userContextService;
    }

    @RequestMapping("/error")
    public String onError(HttpServletRequest request, Model model) {
        ServletWebRequest webRequest = new ServletWebRequest(request);

        // Get the thrown exception;
        Exception ex = (Exception) errorAttributes.getError(webRequest);

        String uid = null;
        User user = userContextService.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }
        logger.error("uid: " + uid + "; Exception: ", ex);

        emailService.sendWithStack(ex, ex.getClass().getSimpleName(), request.getRequestURI());

        // Pick desired error attributes and attach them to the model.
        Map<String, Object> attrs = errorAttributes
                .getErrorAttributes(webRequest, ErrorAttributeOptions.of(
                        ErrorAttributeOptions.Include.MESSAGE,
                        ErrorAttributeOptions.Include.PATH,
                        ErrorAttributeOptions.Include.STATUS,
                        ErrorAttributeOptions.Include.ERROR));
        attrs.put("timestamp", LocalDateTime.now());
        model.addAllAttributes(attrs);

        return "error"; // render view with the built model
    }
}
