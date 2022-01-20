package fr.kmcl.unisignBACK.exception.model;

import com.auth0.jwt.exceptions.TokenExpiredException;
import fr.kmcl.unisignBACK.model.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandlerGnrl implements ErrorController {
    private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact your administrator.";
    private static final String ACCOUNT_DISABLED = "Your account has been disabled. If this is an error, please contact your administrator.";
    private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s' request.";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "An error occurred while processing the request.";
    private static final String INCORRECT_CREDENTIALS = "Username / password incorrect. Please try again.";
    private static final String ERROR_PROCESSING_FILE = "Error occurred while processing file.";
    private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission.";
    public static final String ERROR_PATH = "/error";

    /**
     * Custom Handler for Locked Account Exception
     * @return ResponseEntity<HttpResponse>
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> accountLockedException() {
        return createHttpResponse(BAD_REQUEST, ACCOUNT_LOCKED);
    }

    /**
     * Custom Handler for Disabled Account Exception
     * @return ResponseEntity<HttpResponse>
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException() {
        return createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED);
    }

    /**
     * Custom Handler for Not Allowed Method Exception
     * @return ResponseEntity<HttpResponse>
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    /**
     * Custom Handler for Bad Request - Incorrect Credentials Exception
     * @return ResponseEntity<HttpResponse>
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {
        return createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    /**
     * Custom Handler for Forbidden Access Denied Exception
     * @return ResponseEntity<HttpResponse>
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    /**
     * Custom Handler Exception when the token is expired
     * @return ResponseEntity<HttpResponse>
     */
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception) {
        return createHttpResponse(UNAUTHORIZED, exception.getMessage());
    }

    /**
     * Custom Handler for Email already exists Exception
     * @return ResponseEntity<HttpResponse>
     */
    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> emailExistException(EmailExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    /**
     * Custom Handler for Username already exists Exception
     * @return ResponseEntity<HttpResponse>
     */
    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<HttpResponse> usernameExistException(UsernameExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    /**
     * Custom Handler for Email Not Found Exception
     * @return ResponseEntity<HttpResponse>
     */
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    /**
     * Custom Handler for User Not Found Exception
     * @return ResponseEntity<HttpResponse>
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    /**
     * Custom Handler for Regular Exception if error passes through all others
     * @return ResponseEntity<HttpResponse>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
        log.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
    }

    /**
     * Custom Handler for General Not Found Exception
     * @return ResponseEntity<HttpResponse>
     */
    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    /**
     * Custom Handler for IOException
     * @return ResponseEntity<HttpResponse>
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> iOException(IOException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }

    /**
     * Exception Handler main class to build our custom HttpResponse
     * @param httpStatus: httpStatus
     * @param message: error message
     * @return ResponseEntity<> containing our custom httpResponse and status
     */
    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        HttpResponse httpResponse = new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(httpResponse, httpStatus);
    }

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> notFound404() {
        return createHttpResponse(NOT_FOUND, "There is no mapping for this url");
    }
}
