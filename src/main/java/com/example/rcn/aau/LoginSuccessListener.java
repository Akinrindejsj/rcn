package com.example.rcn.aau;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import lombok.extern.slf4j.Slf4j;

/**
 * Logs the group(s) of the user that just logged in via Keycloak/OIDC.
 * Triggered once per interactive login (not on every authenticated request).
 */
@Slf4j
public class LoginSuccessListener {

    @EventListener
    public void onLoginSuccess(InteractiveAuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof DefaultOidcUser oidcUser) {
            Object group = oidcUser.getClaims().get("groups");
            String name = (String) oidcUser.getClaims().get("name");
            log.info("User logged in: {} | group(s): {}", name, group);
        } else {
            log.info("User logged in (non-OIDC principal): {}", principal);
        }
    }
}
