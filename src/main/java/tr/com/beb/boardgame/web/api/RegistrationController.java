package tr.com.beb.boardgame.web.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import tr.com.beb.boardgame.domain.application.UserService;
import tr.com.beb.boardgame.domain.model.user.RegistrationException;
import tr.com.beb.boardgame.domain.model.user.UsernameExistsException;
import tr.com.beb.boardgame.web.payload.RegistrationPayload;
import tr.com.beb.boardgame.web.results.ApiResult;
import tr.com.beb.boardgame.web.results.Result;

@Controller
public class RegistrationController {

    private UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/registrations")
    public ResponseEntity<ApiResult> register(@Valid @RequestBody RegistrationPayload payload) {
        try {
            userService.register(payload.toCommand());
            return Result.created();
        } catch (RegistrationException e) {
            String errorMessage = "Registration failed";
            if (e instanceof UsernameExistsException)
                errorMessage = "Username already exists";
            return Result.failure(errorMessage);
        }
    }

}
