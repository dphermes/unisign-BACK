package fr.kmcl.unisignBACK.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import fr.kmcl.unisignBACK.security.UserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.*;
import static fr.kmcl.unisignBACK.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@Component
public class JWTTokenProvider {

    // @TODO : Store secret in a more secure way (e.g encrypted on secured server)
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Generate a token once the user is logged in to pass it in the header
     * @param userPrincipal UserPrincipal: Spring Security User
     * @return String: generated token
     */
    public String generateJwtToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create().withIssuer(KMCL_SAS).withAudience(KMCL_ADMINISTRATION)
                .withIssuedAt(new Date()).withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims).withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(secret.getBytes()));
    }

    /**
     * Get the list of a user Granted Authorities encrypted in the token
     * @param token String: actual token
     * @return List<GrantedAuthority>: the list of this token authorities
     */
    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    /**
     * Get the Authentication once the user has successfully logged in
     * And send it to Spring Security context to tell Spring this user is authenticated
     * @param username String: user's username
     * @param authorities List<GrantedAuthority>: user's list of authorities
     * @param request HttpServletRequest: http request
     * @return Authentication: auth token
     */
    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken userPwdAuthToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        userPwdAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPwdAuthToken;
    }

    /**
     * Check if the token is valid
     * @param username String: user's username
     * @param token String: actual token to verify
     * @return boolean: is valid or not
     */
    public boolean isTokenValid(String username, String token) {
        JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);
    }

    /**
     * Get the token subject
     * @param token String: token
     * @return String: token subject
     */
    public String getSubject(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    /**
     * Checks expiration date and verify if token is still valid
     * @param verifier JWTVerifier: JWT Verifier
     * @param token String: actual token to verify
     * @return boolean: is token expired or not
     */
    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    /**
     * Get the claims from the token when a user wants to access information to know if they have the authority to do so
     * @param token String: actual token
     * @return String[]: list of claims from the token
     */
    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    /**
     * Calls Auth0 JWT verifier from the library
     * @return JWTVerifier: Auth0 JWT verifier
     */
    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(KMCL_SAS).build();
        } catch (JWTVerificationException exception) {
            /* Don't throw the actual exception for security matters
            ** Just send a custom message */
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }

    /**
     * Get user's claim after they've successfully logged in
     * @param user UserPrincipal: Spring Security User
     * @return String[]: list of user's claims
     */
    private String[] getClaimsFromUser(UserPrincipal user) {
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }
}
