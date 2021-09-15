package tr.com.beb.boardgame.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import tr.com.beb.boardgame.configuration.ApplicationProperties;
import tr.com.beb.boardgame.domain.application.UserService;
import tr.com.beb.boardgame.domain.common.security.CurrentUser;
import tr.com.beb.boardgame.domain.common.security.TokenManager;
import tr.com.beb.boardgame.domain.model.user.DefaultUserDetails;
import tr.com.beb.boardgame.domain.model.user.User;
import tr.com.beb.boardgame.domain.model.user.UserNotFoundException;
import tr.com.beb.boardgame.web.results.ApiResult;
import tr.com.beb.boardgame.web.results.MyDataResult;
import tr.com.beb.boardgame.web.results.Result;

@Controller
public class MyDataController {

    private static final Logger logger = LoggerFactory.getLogger(MyDataController.class);

    private UserService userService;
    private TokenManager tokenManager;

    private ApplicationProperties applicationProperties;

    public MyDataController(UserService userService, TokenManager tokenManager,
            ApplicationProperties applicationProperties) {
        this.userService = userService;
        this.tokenManager = tokenManager;
        this.applicationProperties = applicationProperties;
    }

    @GetMapping("/api/me")
    public ResponseEntity<ApiResult> getMyData(@CurrentUser DefaultUserDetails currentUser) {
        try {
            User user = userService.findById(currentUser.getUserId());
            String webSocketToken = tokenManager.generateJWT(currentUser.getUserId());
            String webSocketServerUrl = applicationProperties.getWebSocketServerUrl();

            return MyDataResult.build(user, webSocketToken, webSocketServerUrl);
        } catch (UserNotFoundException e) {
            logger.error("UserNotFoundException error at MyDataController.getMyData", e);
            return Result.unauthenticated();
        }
    }

}
