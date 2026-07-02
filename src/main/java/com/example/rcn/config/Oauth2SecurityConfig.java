package com.example.rcn.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Slf4j
@Configuration
public class Oauth2SecurityConfig {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http, ClientRegistrationRepository repo) throws Exception {
        log.debug("Configuring security");

        http.securityMatcher("/**")
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .csrf(CsrfConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/keycloak")
                        .authorizationEndpoint(Customizer.withDefaults())
                        .tokenEndpoint(Customizer.withDefaults())
                        .userInfoEndpoint(Customizer.withDefaults())
                        .successHandler(authenticationSuccessHandler())
                        )
                .authorizeHttpRequests(
                        authorize -> {
                            // Static resources - always accessible
                            authorize
                                    .requestMatchers("/static/**", "/webjars/**", "/images/**", "/actuator/health")
                                    .permitAll();

                            // Public pages - accessible without authentication
                            authorize
                                    .requestMatchers("/", "/dashboard", "/theory", "/podcast", "/article/**", "/article",
                                                   "/activity", "/about", "/donate", "/news", "/write", "/join", "/404",
                                                   "/search", "/oauth2/**", "/login")
                                    .permitAll();

                            // CMS and admin endpoints - require authentication
                            authorize
                                    .requestMatchers("/admin/**", "/cms/**", "/users/logout")
                                    .authenticated();

                            // All other requests require authentication
                            authorize.anyRequest().authenticated();
                        }
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler(repo))
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutUrl("/users/logout")
                );
        return http.build();
    }



    private LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository repository) {
        OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(repository);
        logoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
        return logoutSuccessHandler;
    }

    /**
     * Custom authentication success handler that routes users to the appropriate
     * dashboard based on their group membership in Keycloak.
     *
     * Routes:
     * - admin group -> /admin/dashboard
     * - auditor group -> /auditor/dashboard
     * - assetManager group -> /admin/dashboard
     * - departmentHead group -> /department-head/dashboard
     * - employees group -> /employee/dashboard
     * - fallback -> /dashboard
     */
    private AuthenticationSuccessHandler authenticationSuccessHandler(){
        return  (request, response, authentication) -> {
                String redirectUrl = authenticationManager.getDefaultDashboardUrl();
                log.info("Redirecting authenticated user to: {}", redirectUrl);
                response.sendRedirect(redirectUrl);
        };
    }
}
