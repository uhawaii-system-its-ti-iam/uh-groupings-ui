package edu.hawaii.its.groupings.configuration;

import java.io.File;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        File seal = new File("C:/Users/Aaron/seal/");
        File css = new File("C:/Users/Aaron/override/");

        if (seal.isDirectory() && seal.list().length == 1) {
            registry.addResourceHandler("images/seal/**")
                    .addResourceLocations("file:///C:/Users/Aaron/seal/");
        }

        if (css.isDirectory() && css.list().length == 1) {
            registry.addResourceHandler("css/override/**")
                    .addResourceLocations("file:///C:/Users/Aaron/override/");
        }
    }
}
