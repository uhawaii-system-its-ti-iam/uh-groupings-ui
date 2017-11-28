package edu.hawaii.its.holiday.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.hawaii.its.holiday.access.User;
import edu.hawaii.its.holiday.access.UserContextService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ErrorControllerAdvice {

    private static final Log logger = LogFactory.getLog(HomeController.class);

    @Autowired
    private UserContextService userContextService;

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handelIllegalArgumentException(IllegalArgumentException iae) {
        return error(iae);
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handelIllegalArgumentException(RuntimeException re) {
        return error(re);
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex) {
        String username = null;
        User user = userContextService.getCurrentUser();
        if (user != null) {
            username = user.getUsername();
        }
        logger.error("username: " + username + "; Exception: ", ex);

        return "redirect:/";
    }

    private ModelAndView error(Exception exception) {
        String username = null;
        User user = userContextService.getCurrentUser();
        if (user != null) {
            username = user.getUsername();
        }
        logger.error("username: " + username + "; Exception: ", exception.getCause());

        ModelAndView modelAndView = new ModelAndView("redirect:/error");
        modelAndView.addObject("errCode", 500);
        modelAndView.addObject("errMsg", exception.getMessage());

        return modelAndView;

    }
}