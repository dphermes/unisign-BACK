package fr.kmcl.unisignBACK.filter;

import fr.kmcl.unisignBACK.utility.JWTTokenProvider;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static fr.kmcl.unisignBACK.constant.SecurityConstant.OPTIONS_HTTP_METHOD;
import static fr.kmcl.unisignBACK.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@Component
@AllArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private JWTTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // If it's options to collect information about the server we have to let it go through
        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            response.setStatus(OK.value());
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authorizationHeader.substring(TOKEN_PREFIX.length());
            String username = jwtTokenProvider.getSubject(token);
            if (jwtTokenProvider.isTokenValid(username, token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
                Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
