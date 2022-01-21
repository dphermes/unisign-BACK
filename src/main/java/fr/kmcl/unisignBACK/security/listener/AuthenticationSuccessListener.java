package fr.kmcl.unisignBACK.security.listener;

import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.service.LoginAttemptService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired)) // https://www.baeldung.com/spring-injection-lombok
public class AuthenticationSuccessListener {

    private LoginAttemptService loginAttemptService;

    /**
     * Listener to check if logging attempt succeeded
     * if so : remove user from the Brute Force Attack Cache
     * @param event AuthenticationSuccessEvent: Event fired if log in success
     */
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof AppUser) {
            AppUser user = (AppUser) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
