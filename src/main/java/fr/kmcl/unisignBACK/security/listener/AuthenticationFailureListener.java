package fr.kmcl.unisignBACK.security.listener;

import fr.kmcl.unisignBACK.service.LoginAttemptService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired)) // https://www.baeldung.com/spring-injection-lombok
public class AuthenticationFailureListener {

    private LoginAttemptService loginAttemptService;

    /**
     * Listener to check if logging attempt failed
     * if so : add user to the Brute Force Attack Cache
     * @param event AuthenticationFailureBadCredentialsEvent: Event fired if log in failure
     */
    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof String) {
            String username = (String) event.getAuthentication().getPrincipal();
            loginAttemptService.addUserToLoginAttemptCache(username);
        }
    }
}
