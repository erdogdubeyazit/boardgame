package tr.com.beb.boardgame.web.results;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import tr.com.beb.boardgame.domain.model.user.User;

public class MyDataResult {

    public static ResponseEntity<ApiResult> build(User user, String webSocketToken, String webSocketServerUrl) {

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", user.getUserId().getValue());
        userData.put("name", String.format("%s %s", user.getName(), user.getSurname()));
        userData.put("token", webSocketToken);

        Map<String, Object> settings = new HashMap<>();
        settings.put("webSocketServerUrl", webSocketServerUrl);

        ApiResult apiResult = ApiResult.blank().add("user", userData).add("settings", settings);

        return Result.ok(apiResult);
    }

}
