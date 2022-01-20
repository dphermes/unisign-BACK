package fr.kmcl.unisignBACK.resource;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.kmcl.unisignBACK.exception.model.EmailExistException;
import fr.kmcl.unisignBACK.exception.model.ExceptionHandlerGnrl;
import fr.kmcl.unisignBACK.exception.model.UserNotFoundException;
import fr.kmcl.unisignBACK.model.AppRole;
import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserResource extends ExceptionHandlerGnrl {

    private final UserService userService;

    @GetMapping("/errorTesting")
    public String showError() throws UserNotFoundException {
        throw new UserNotFoundException("This user was not found");
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @GetMapping(path = "/user/{username}")
    @PreAuthorize("hasAuthority('user:read')")
    public AppUser getUser(@PathVariable("username") String username) {
        return userService.getUser(username);
    }

    @PostMapping("/user/save")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @PostMapping("/role/save")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<AppRole> saveRole(@RequestBody AppRole role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/role/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping("/role/addtouser")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form) {
        userService.addRoleToUser(form.getUsername(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();
                AppUser user = userService.getUser(username);
                String accessToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURI())
                        .withClaim("roles", user.getRoles().stream().map(AppRole::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception e) {
                log.error("Error logging in: {}", e.getMessage());
                response.setHeader("Error", e.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
           throw new RuntimeException("Refresh token is missing");
        }
    }
}

@Data
class RoleToUserForm {
    private String username;
    private String roleName;
}
