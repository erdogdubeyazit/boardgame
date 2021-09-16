package tr.com.beb.boardgame.web.socket;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import tr.com.beb.boardgame.domain.model.user.UserId;
import tr.com.beb.boardgame.utils.JsonUtils;

public class CustomWebSocketSession {

    private static final Logger logger = LoggerFactory.getLogger(CustomWebSocketSession.class);
    private static final String USER_ID = "USER_ID";

    private WebSocketSession webSocketSession;

    public CustomWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public String getId() {
        return webSocketSession.getId();
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setUserId(UserId userId) {
        addAttribute(USER_ID, userId);
    }

    public UserId getUserId() {
        return getAttribute(USER_ID);
    }

    void addAttribute(String key, Object value) {
        webSocketSession.getAttributes().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        Object value = webSocketSession.getAttributes().get(key);
        if (value == null)
            return null;
        return (T) value;
    }

    public String getToken() {
        URI uri = webSocketSession.getUri();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(uri).build();
        return uriComponents.getQueryParams().getFirst("token");
    }

    public void error(String error) {
        send(WebSocketMessages.error(error));
    }

    public void fail(String failure) {
        send(WebSocketMessages.failure(failure));
    }

    public void reply(String reply) {
        send(WebSocketMessages.reply(reply));
    }

    private void send(Object message) {
        try {
            String messageString = JsonUtils.toJson(message);
            webSocketSession.sendMessage(new TextMessage(messageString));
        } catch (IOException e) {
            logger.error("Message cannnot be sent via web socket session", e);
        }
    }
}
