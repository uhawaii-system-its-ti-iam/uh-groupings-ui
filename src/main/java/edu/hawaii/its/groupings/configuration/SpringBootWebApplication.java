package edu.hawaii.its.groupings.configuration;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = "edu.hawaii.its")
public class SpringBootWebApplication extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }

    /**
     * Post Construct runs the method only once after bean initialization, these will run if there
     * are no beans to initialize. This should run before the actual application runs.
     */
    @PostConstruct
    private void checkForPwd() throws IOException {
        // Access the file regardless of the environment using Spring ClassPathResource.
        // The file name should be unique to the project.
        File resource = new ClassPathResource("custom.properties").getFile();
        Scanner fileScanner = new Scanner(resource);
        // Tracker for the line the pattern is found on.
        int lineID = 0;
        ArrayList<Integer> lineNumbers = new ArrayList<>();
        // Uses a string to make a pattern to compare to.
        Pattern pattern = Pattern.compile("grouperClient.webService.password");
        Matcher matcher = null;
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            lineID++;
            // Creates a matcher object to look through the line given.
            matcher = pattern.matcher(line);
            if (matcher.find()) {
                lineNumbers.add(lineID);
            }
        }

        // If lineNumbers is not empty, spits out the error message to the terminal.
        Assert.isTrue(lineNumbers.isEmpty(), "Please remove the password from the custom.properties file.");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return configureApplication(builder);
    }

    private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {
        return builder.sources(SpringBootWebApplication.class).bannerMode(Banner.Mode.OFF);
    }

    @Bean
    public WebSecurityConfigurerAdapter webSecurityConfigurerAdapter() {
        return new SecurityConfig();
    }
}