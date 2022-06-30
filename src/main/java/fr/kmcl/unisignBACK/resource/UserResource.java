package fr.kmcl.unisignBACK.resource;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.kmcl.unisignBACK.exception.model.*;
import fr.kmcl.unisignBACK.model.Agency;
import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.model.HttpResponse;
import fr.kmcl.unisignBACK.security.UserPrincipal;
import fr.kmcl.unisignBACK.service.UserService;
import fr.kmcl.unisignBACK.utility.JWTTokenProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.kmcl.unisignBACK.constant.FileConstant.*;
import static fr.kmcl.unisignBACK.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static fr.kmcl.unisignBACK.constant.UserImplConstant.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@RestController
@RequestMapping(path = {"/", "/api/v1/user"})
@AllArgsConstructor
@Slf4j
public class UserResource extends ExceptionHandlerGnrl {

    private final UserService userService;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;

    /**
     * Login user
     * @param user AppUser: user Entity
     * @return ResponseEntity<AppUser>: return the user with a Jwt, user's info and HttpStatus
     */
    @PostMapping("/login")
    public ResponseEntity<AppUser> loginUser(@RequestBody AppUser user) {
        authenticate(user.getUsername(), user.getPassword());
        AppUser loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }

    /**
     * Save a user in the database
     * @param user AppUser: user to save
     * @return ResponseEntity<AppUser>: a user and a httpStatus
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    /**
     * Register a new user when he/she doesn't have any account
     * @param user AppUser: new user to register
     * @return ResponseEntity<AppUser>: a response entity with the new user and the OK http status
     * @throws UserNotFoundException: UserNotFoundException exception can be thrown
     * @throws EmailExistException: EmailExistException exception can be thrown
     * @throws UsernameExistException: UsernameExistException exception can be thrown
     * @throws MessagingException: MessagingException exception can be thrown
     */
    @PostMapping("/register")
    public ResponseEntity<AppUser> registerUser(@RequestBody AppUser user)
            throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        AppUser newUser = userService.registerUser(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(newUser, OK);
    }

    /**
     * Add a new user from another account with all user infos
     * @param firstName String: new user's first name
     * @param lastName String: new user's last name
     * @param username String: new user's username
     * @param email String: new user's username
     * @param role String: new user's role
     * @param isActive String: if user is active (true) or not (false)
     * @param isNonLocked String: if user is not locked (true) or is locked (false)
     * @param profileImage MultipartFile: new user's profile Image file
     * @return ResponseEntity<AppUser>: a response entity with the new user and the OK http status
     * @throws UserNotFoundException: UserNotFoundException exception can be thrown
     * @throws EmailExistException: EmailExistException exception can be thrown
     * @throws IOException: IOException exception can be thrown exception can be thrown
     * @throws UsernameExistException: UsernameExistException exception can be thrown
     */
    @PostMapping("/add")
    public ResponseEntity<AppUser> addNewUser(@RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("agencyLabel") String agencyLabel,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNonLocked") String isNonLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotImageFileException {
        AppUser newUser = userService.addNewUser(firstName, lastName, username, email, agencyLabel, role, Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(newUser, OK);
    }

    /**
     *
     * @param user
     * @return
     * @throws UserNotFoundException
     * @throws EmailExistException
     * @throws IOException
     * @throws UsernameExistException
     * @throws NotImageFileException
     */
    @PostMapping("/update")
    public ResponseEntity<AppUser> updateUser(@RequestBody AppUser user)
            throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotImageFileException {
        AppUser updatedUser = userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, OK);
    }

    /**
     * Delete a user if we have according authority
     * @param username String: user's username
     * @return ResponseEntity<HttpResponse>: httpStatus and message if found
     */
    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return response(OK, USER_DELETED_SUCCESSFULLY);
    }

    /**
     * Fetch a user by his username
     * @param username String: user's username
     * @return ResponseEntity<AppUser>: The user and httpStatus
     */
    @GetMapping(path = "/find/{username}")
    public ResponseEntity<AppUser> getUser(@PathVariable("username") String username) {
        AppUser user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, OK);
    }

    /**
     * Fetch all users
     * @return ResponseEntity<List<AppUser>>: The list of all users and a httpStatus
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<AppUser>> getAllUsers() {
        List<AppUser> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }

    /**
     * Reset a user's password
     * @param email String: user's email
     * @return ResponseEntity<HttpResponse>: httpStatus and message if found
     * @throws EmailNotFoundException: EmailNotFoundException exception can be thrown
     * @throws MessagingException: MessagingException exception can be thrown
     */
    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException {
        userService.resetPassword(email);
        return response(OK, EMAIL_SENT + email);
    }

    /**
     * Update user's profile image
     * @param username String: user's username
     * @param profileImage MultipartFile: the new profile Image (JPG)
     * @return ResponseEntity<AppUser>: The user and a httpStatus
     * @throws UserNotFoundException: UserNotFoundException exception can be thrown
     * @throws EmailExistException: EmailExistException exception can be thrown
     * @throws IOException: IOException exception can be thrown
     * @throws UsernameExistException: UsernameExistException exception can be thrown
     */
    @PostMapping("/update-profile-image")
    public ResponseEntity<AppUser> updateProfileImage(@RequestParam("username") String username,
                                                      @RequestParam(value = "profileImage") MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotImageFileException {
        AppUser user = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(user, OK);
    }

    /**
     * Fetch a user's profile image
     * @param username String: user's username
     * @param fileName String: file name
     * @return byte[]: Image in bytes
     * @throws IOException: IOException exception can be thrown
     */
    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    /**
     * Fetch a temporary profile image from external API if user doesn't upload his image
     * @param username String: user's username
     * @return byte[]: Image in bytes
     * @throws IOException: IOException exception can be thrown
     */
    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            // Control the number of byte analyzed at once for performance purpose
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Custom method to return a response entity if another method doesn't return anything initially
     * @param httpStatus HttpStatus: httpStatus
     * @param message String: message to build a response entity
     * @return ResponseEntity<HttpResponse>: custom response entity
     */
    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase(), message), httpStatus);
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
                AppUser user = userService.findUserByUsername(username);
                String accessToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURI())
                        .withClaim("role", user.getRole())
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

    /**
     * Builds and sends JWT token headers
     * @param user UserPrincipal: user
     * @return HttpHeaders
     */
    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

    /**
     * Authenticate a user
     * @param username String: user's username
     * @param password String: user's password
     */
    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

//    @GetMapping("/errorTesting")
//    public String showError() throws UserNotFoundException {
//        throw new UserNotFoundException("This user was not found");
//    }
}

@Data
class RoleToUserForm {
    private String username;
    private String roleName;
}
