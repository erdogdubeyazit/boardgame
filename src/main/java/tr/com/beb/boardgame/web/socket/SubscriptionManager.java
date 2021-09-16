package tr.com.beb.boardgame.web.socket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketSession;

import tr.com.beb.boardgame.domain.model.user.UserId;

public final class SubscriptionManager {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionManager.class);

    // <Channel,Set of subscribed web socket sessions>
    private static final Map<String, Set<WebSocketSession>> subscriptions = new HashMap<>();

    // <Session id, set of subscribed channels>
    private static final Map<String, Set<String>> subscribedChannels = new HashMap<>();

    public static void subscribe(CustomWebSocketSession customWebSocketSession, String channel) {
        Assert.hasText(channel, "Parameter `channel` must not be null");

        Set<WebSocketSession> subscribers = subscriptions.computeIfAbsent(channel, s -> new HashSet<>());
        subscribers.add(customWebSocketSession.getWebSocketSession());

        UserId userId = customWebSocketSession.getUserId();
        logger.debug("WebSocketSession[{}] Subscribed user[id={}] to channel `{}`", customWebSocketSession.getId(),
                userId, channel);
        // Also add the channel to subscribed channels
        Set<String> channels = subscribedChannels.computeIfAbsent(customWebSocketSession.getId(), c -> new HashSet<>());
        channels.add(channel);
    }

    public static void unsubscribe(CustomWebSocketSession customWebSocketSession, String channel) {
        Assert.hasText(channel, "Parameter `channel` must not be empty");
        Assert.notNull(customWebSocketSession, "Parameter `customWebSocketSession` must not be null");

        Set<WebSocketSession> subscribers = subscriptions.get(channel);
        if (subscribers != null) {
            subscribers.remove(customWebSocketSession.getWebSocketSession());
            UserId userId = customWebSocketSession.getUserId();
            logger.debug("WebSocketSession[{}] Unsubscribed user[id={}] from channel `{}`",
                    customWebSocketSession.getId(), userId, channel);
        }

        // Also remove the channel from subscribed channels
        Set<String> channels = subscribedChannels.get(customWebSocketSession.getId());
        if (channels != null)
            channels.remove(channel);

    }

    public static void unsubscribeAll(CustomWebSocketSession customWebSocketSession) {
        Set<String> channels = subscribedChannels.get(customWebSocketSession.getId());
        if (channels == null) {
            logger.debug("WebSocketSession[{}] No channels to unsubscribe.", customWebSocketSession.getId());
            return;
        }

        channels.stream().forEach(c -> unsubscribe(customWebSocketSession, c));

        // Also remove subscribed channels
        subscribedChannels.remove(customWebSocketSession.getId());
    }

    public static void send(String channel, String payload) {
        Assert.hasText(channel, "Parameter `channel` must not be empty");
        Assert.hasText(payload, "Parameter `payload` must not be null");

        Set<WebSocketSession> subscribers = subscriptions.get(channel);
        if (subscribers == null || subscriptions.isEmpty()) {
            logger.debug("No subscribers of channel `{}` found", channel);
            return;
        }

        subscribers.stream().forEach(ws -> sendTo(ws, channel, payload));
    }

    private static void sendTo(WebSocketSession subscriber, String channel, String payload) {
        try {
            subscriber.sendMessage(WebSocketMessages.channelMessage(channel, payload));
            logger.debug("WebSocketSession[{}] Send message `{}` to subscriber at channel `{}`", subscriber.getId(),
                    payload, channel);
        } catch (Exception e) {
            logger.error("Failed to send message to subscriber `" + subscriber.getId() + "` of channel `" + channel
                    + "`. Message: " + payload, e);
        }
    }

}
