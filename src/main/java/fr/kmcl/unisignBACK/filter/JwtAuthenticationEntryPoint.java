package fr.kmcl.unisignBACK.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.kmcl.unisignBACK.model.HttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static fr.kmcl.unisignBACK.constant.SecurityConstant.FORBIDDEN_MESSAGE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    /**
     * Override default "Access Denied" message when accessing information while not logged in
     * It is just to send a prettier message, but it's way cooler
     * @param request: HttpServletRequest
     * @param response: HttpServletResponse
     * @param exception: AuthenticationException
     * @throws IOException: exception
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        HttpResponse httpResponse = new HttpResponse(FORBIDDEN.value(), FORBIDDEN, FORBIDDEN.getReasonPhrase().toUpperCase(), FORBIDDEN_MESSAGE);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        // Take the httpServlet response (response) and stream our custom httpResponse object into it
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }
}
