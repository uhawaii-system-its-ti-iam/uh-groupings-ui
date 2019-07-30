package edu.hawaii.its.groupings.configuration;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class SpringBootWebApplication extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }

    @PostConstruct
    private void checkForPw() throws IOException {
        Scanner fileScanner = new Scanner(new File("src/main/resources/custom.properties"));
        int lineID = 0;
        List lineNumbers = new ArrayList();
        Pattern pattern =  Pattern.compile("grouperClient.webService.password");
        Matcher matcher = null;
        while(fileScanner.hasNextLine()){
            String line = fileScanner.nextLine();
            lineID++;
            matcher = pattern.matcher(line);
            if(matcher.find()){
                lineNumbers.add(lineID);

            }
        }
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