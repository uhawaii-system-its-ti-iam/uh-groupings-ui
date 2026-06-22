package edu.hawaii.its.groupings.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import jakarta.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.ws.config.annotation.DelegatingWsConfiguration;

import edu.hawaii.its.api.controller.OotbRestController;
import edu.hawaii.its.groupings.access.DelegatingAuthenticationFailureHandler;
import edu.hawaii.its.groupings.service.OotbActiveUserProfileService;

@EnableWebSecurity
@Configuration
@Profile("ootb")
public class OotbSecurityConfig {

    private static final Log logger = LogFactory.getLog(OotbSecurityConfig.class);
    private final OotbRestController ootbRestController;
    private final OotbStaticUserAuthenticationFilter ootbStaticUserAuthenticationFilter;
    private final OotbActiveUserProfileService ootbActiveUserProfileService;
    @Value("${url.base}")
    private String appUrlBase;

    public OotbSecurityConfig(OotbRestController ootbRestController,
            OotbStaticUserAuthenticationFilter ootbStaticUserAuthenticationFilter,
            OotbActiveUserProfileService ootbActiveUserProfileService) {
        this.ootbRestController = ootbRestController;
        this.ootbStaticUserAuthenticationFilter = ootbStaticUserAuthenticationFilter;
        this.ootbActiveUserProfileService = ootbActiveUserProfileService;
    }

    @PostConstruct
    public void init() {
        updateActiveDefaultUser();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DelegatingAuthenticationFailureHandler authenticationFailureHandler() {
        return new DelegatingAuthenticationFailureHandler(appUrlBase);
    }

    @Bean
    public DelegatingWsConfiguration delegatingWsConfiguration() {
        return new DelegatingWsConfiguration();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(ootbStaticUserAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((auths)
                        -> auths
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/announcements/**").permitAll()
                        .requestMatchers("/api/**").hasRole("UH")
                        .requestMatchers("/currentUser").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/fonts/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/javascript/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/home").permitAll()
                        .requestMatchers("/about").permitAll()
                        .requestMatchers("/feedback").hasRole("UH")
                        .requestMatchers(("/denied")).permitAll()
                        .requestMatchers(("/404")).permitAll()
                        .requestMatchers("/login").hasRole("UH")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/groupings/**").hasAnyRole("ADMIN", "OWNER")
                        .requestMatchers("/memberships/**").hasRole("UH")
                        .requestMatchers("/modal/apiError").permitAll()
                        .requestMatchers("/uhuuid-error").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/testing/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .csrf((csrf) -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/**", "/logout"))
                .httpBasic(withDefaults());
        return http.build();
    }

    public void updateActiveDefaultUser() {
        ootbRestController.updateActiveDefaultUser(ootbActiveUserProfileService.findGivenNameForAdminRole());
    }
}