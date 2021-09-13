package tr.com.beb.boardgame.web.api.authenticate;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import tr.com.beb.boardgame.utils.JsonUtils;
import tr.com.beb.boardgame.web.results.ApiResult;

public class DefaultLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        response.setStatus(HttpStatus.OK.value());
        JsonUtils.write(response.getWriter(), ApiResult.message("logged-out"));

    }

}
