package tr.com.beb.boardgame.web.api.authenticate;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import tr.com.beb.boardgame.utils.JsonUtils;

public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    public AuthenticationFilter() {
        super(new AntPathRequestMatcher("/api/authentications", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, IOException {

        logger.debug("Processing login regquest");

        String requestBody = IOUtils.toString(request.getReader());
        LoginRequest loginRequest = JsonUtils.toObject(requestBody, LoginRequest.class);
        if (loginRequest == null || loginRequest.isInvalid())
            throw new InsufficientAuthenticationException("Authentication request is invalid");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.username, loginRequest.password);
        return this.getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
    }

    static class LoginRequest {
        private String username;
        private String password;

        public boolean isInvalid() {
            return StringUtils.isBlank(username) || StringUtils.isBlank(password);
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }

}
