package edu.hawaii.its.groupings.configuration;

import edu.hawaii.its.groupings.access.CasUserDetailsServiceImpl;
import edu.hawaii.its.groupings.access.DelegatingAuthenticationFailureHandler;
import edu.hawaii.its.groupings.access.UserBuilder;
import jakarta.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apereo.cas.client.proxy.ProxyGrantingTicketStorage;
import org.apereo.cas.client.proxy.ProxyGrantingTicketStorageImpl;
import org.apereo.cas.client.session.SingleSignOutFilter;
import org.apereo.cas.client.validation.Saml11TicketValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetailsSource;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.util.Assert;
import org.springframework.ws.config.annotation.DelegatingWsConfiguration;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@EnableWebSecurity
@Configuration
@ConditionalOnProperty(name = "grouping.api.server.type", havingValue = "GROUPER", matchIfMissing = true)
public class SecurityConfig {

    private static final Log logger = LogFactory.getLog(SecurityConfig.class);

    @Value("${url.base}")
    private String appUrlBase;

    @Value("${app.url.home}")
    private String appUrlHome;

    @Value("${cas.login.url}")
    private String casLoginUrl;

    @Value("${cas.logout.url}")
    private String casLogoutUrl;

    @Value("${cas.main.url}")
    private String casMainUrl;

    @Value("${cas.saml.tolerance}")
    private long casSamlTolerance;

    @Value("${cas.send.renew:false}")
    private boolean casSendRenew;

    private final UserBuilder userBuilder;

    public SecurityConfig(UserBuilder userBuilder) {
        this.userBuilder = userBuilder;
    }

    @PostConstruct
    public void init() {
        logger.info("  appUrlHome: " + appUrlHome);
        logger.info("  appUrlBase: " + appUrlBase);
        logger.info("  casMainUrl: " + casMainUrl);
        logger.info(" casLoginUrl: " + casLoginUrl);
        logger.info("casLogoutUrl: " + casLogoutUrl);
        logger.info("casSendRenew: " + casSendRenew);

        Assert.hasLength(appUrlHome, "property 'appUrlHome' is required");
        Assert.hasLength(appUrlBase, "property 'appUrlBase' is required");
        Assert.hasLength(casLoginUrl, "property 'casLoginUrl' is required");
        Assert.hasLength(casLogoutUrl, "property 'casLogoutUrl' is required");

        logger.info("SecurityConfig started. userBuilder: " + userBuilder);
    }

    @Bean
    public ProxyGrantingTicketStorage proxyGrantingTicketStorage() {
        return new ProxyGrantingTicketStorageImpl();
    }

    @Bean
    public SingleSignOutFilter singleLogoutFilter() {
        return new SingleSignOutFilter();
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(appUrlBase + "/login/cas");
        serviceProperties.setSendRenew(casSendRenew);
        serviceProperties.setAuthenticateAllArtifacts(true);

        return serviceProperties;
    }

    @Bean
    public CasAuthenticationEntryPoint casProcessingFilterEntryPoint() {
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(casLoginUrl);
        entryPoint.setServiceProperties(serviceProperties());
        return entryPoint;
    }

    @Bean
    public LogoutFilter logoutFilter() {
        return new LogoutFilter(appUrlHome, new SecurityContextLogoutHandler());
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setKey("an_id_for_this_auth_provider_only");
        provider.setAuthenticationUserDetailsService(authenticationUserDetailsService());
        provider.setServiceProperties(serviceProperties());

        Saml11TicketValidator ticketValidator = new Saml11TicketValidator(casMainUrl);
        ticketValidator.setTolerance(casSamlTolerance);
        provider.setTicketValidator(ticketValidator);

        return provider;
    }

    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> authenticationUserDetailsService() {
        return new CasUserDetailsServiceImpl(userBuilder);
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter(AuthenticationManager authenticationManager) {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);

        filter.setProxyAuthenticationFailureHandler(authenticationFailureHandler());
        filter.setAuthenticationFailureHandler(authenticationFailureHandler());

        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler());

        ServiceAuthenticationDetailsSource authenticationDetailsSource =
                new ServiceAuthenticationDetailsSource(serviceProperties());
        filter.setAuthenticationDetailsSource(authenticationDetailsSource);

        filter.setProxyGrantingTicketStorage(proxyGrantingTicketStorage());
        filter.setProxyReceptorUrl("/receptor");
        filter.setServiceProperties(serviceProperties());

        return filter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler =
                new SavedRequestAwareAuthenticationSuccessHandler();
        authenticationSuccessHandler.setAlwaysUseDefaultTargetUrl(false);
        authenticationSuccessHandler.setDefaultTargetUrl(appUrlHome);
        return authenticationSuccessHandler;
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
    public OotbStaticUserAuthenticationFilter ootbStaticUserAuthenticationFilter() {
        return new OotbStaticUserAuthenticationFilter(null, "");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CasAuthenticationFilter casAuthenticationFilter)
            throws Exception {
        http.sessionManagement((session) -> session.sessionFixation().migrateSession());

        http.authorizeHttpRequests((auths) -> auths
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
                .addFilter(casAuthenticationFilter)
                .addFilterBefore(logoutFilter(), LogoutFilter.class)
                .exceptionHandling((exception) -> exception.authenticationEntryPoint(casProcessingFilterEntryPoint()))
                .csrf((csrf) -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(antMatcher("/api/**"), antMatcher("/logout")))
                .logout((logout) -> logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutUrl("/logout")
                        .logoutSuccessUrl(appUrlHome));

        return http.build();
    }
}
