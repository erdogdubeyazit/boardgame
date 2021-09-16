package tr.com.beb.boardgame.web.socket;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.socket.TextMessage;

import tr.com.beb.boardgame.utils.JsonUtils;

public class WebSocketMessages {

    static TextMessage reply(String reply) {
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("type", "reply");
        messageMap.put("message", reply);
        return new TextMessage(JsonUtils.toJson(messageMap));
    }

    static TextMessage error(String error) {
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("type", "error");
        messageMap.put("message", error);
        return new TextMessage(JsonUtils.toJson(messageMap));
    }

    static TextMessage failure(String failure) {
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("type", "failure");
        messageMap.put("message", failure);
        return new TextMessage(JsonUtils.toJson(messageMap));
    }

    static TextMessage channelMessage(String channel, String payload) {
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("channel", channel);
        messageMap.put("payload", payload);
        return new TextMessage(JsonUtils.toJson(messageMap));
    }

}
