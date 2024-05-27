package edu.hawaii.its.groupings.configuration;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.ws.config.annotation.DelegatingWsConfiguration;

import edu.hawaii.its.groupings.access.DelegatingAuthenticationFailureHandler;

@EnableWebSecurity
@Configuration
@ConditionalOnProperty(name = "grouping.api.server.type", havingValue = "OOTB")
public class OotbSecurityConfig {

    private static final Log logger = LogFactory.getLog(OotbSecurityConfig.class);

    @Value("${ootb.active.user.profile}")
    private String userProfile;

    @Value("${url.base}")
    private String appUrlBase;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration,
            UserDetailsService userDetailsService) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(List.of(provider));
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
                .addFilterBefore(ootbStaticUserAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((auths)
                        -> auths
                        .requestMatchers(antMatcher("/")).permitAll()
                        .requestMatchers(antMatcher("/announcements/**")).permitAll()
                        .requestMatchers(antMatcher("/api/**")).hasRole("UH")
                        .requestMatchers(antMatcher("/currentUser")).permitAll()
                        .requestMatchers(antMatcher("/css/**")).permitAll()
                        .requestMatchers(antMatcher("/fonts/**")).permitAll()
                        .requestMatchers(antMatcher("/images/**")).permitAll()
                        .requestMatchers(antMatcher("/javascript/**")).permitAll()
                        .requestMatchers(antMatcher("/webjars/**")).permitAll()
                        .requestMatchers(antMatcher("/home")).permitAll()
                        .requestMatchers(antMatcher("/about")).permitAll()
                        .requestMatchers(antMatcher("/feedback")).hasRole("UH")
                        .requestMatchers(antMatcher("/denied")).permitAll()
                        .requestMatchers(antMatcher("/404")).permitAll()
                        .requestMatchers(antMatcher("/login")).hasRole("UH")
                        .requestMatchers(antMatcher("/admin/**")).hasRole("ADMIN")
                        .requestMatchers(antMatcher("/groupings/**")).hasAnyRole("ADMIN", "OWNER")
                        .requestMatchers(antMatcher("/memberships/**")).hasRole("UH")
                        .requestMatchers(antMatcher("/modal/apiError")).permitAll()
                        .requestMatchers(antMatcher("/uhuuid-error")).permitAll()
                        .requestMatchers(antMatcher("/error")).permitAll()
                        .requestMatchers(antMatcher("/testing/**")).hasRole("ADMIN")
                        .anyRequest().authenticated())
                .csrf((csrf) -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(antMatcher("/api/**"), antMatcher("/logout")))
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public OotbStaticUserAuthenticationFilter ootbStaticUserAuthenticationFilter() {
        return new OotbStaticUserAuthenticationFilter(userDetailsService(), userProfile);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new OotbUserDetailsManager();
    }
}
