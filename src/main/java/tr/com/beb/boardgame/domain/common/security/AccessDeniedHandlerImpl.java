package tr.com.beb.boardgame.domain.common.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import tr.com.beb.boardgame.domain.model.user.DefaultUserDetails;

public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private final static Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Access to `" + request.getRequestURI() + "` denied.");
        }

        if (request.getRequestURI().startsWith("/api/")) {
            if (request.getUserPrincipal() instanceof DefaultUserDetails) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            response.sendRedirect("/login");
        }
    }
}
