package tr.com.beb.boardgame.web.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import tr.com.beb.boardgame.domain.common.security.TokenManager;
import tr.com.beb.boardgame.domain.model.user.UserId;
import tr.com.beb.boardgame.utils.JsonUtils;

@Component
public class WebSocketDispatcher extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketDispatcher.class);

    private TokenManager tokenManager;
    private ChannelHandlerResolver channelHandlerResolver;

    public WebSocketDispatcher(TokenManager tokenManager, ChannelHandlerResolver channelHandlerResolver) {
        this.tokenManager = tokenManager;
        this.channelHandlerResolver = channelHandlerResolver;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        CustomWebSocketSession customWebSocketSession = new CustomWebSocketSession(session);
        SubscriptionManager.unsubscribeAll(customWebSocketSession);
        logger.debug("WebSocketSEssion[{}] Unsubscribed all channels after disconnecting",
                customWebSocketSession.getId());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.debug("WebSocket connection has established.");
        CustomWebSocketSession customWebSocketSession = new CustomWebSocketSession(session);
        String token = customWebSocketSession.getToken();

        try {
            UserId userId = tokenManager.parseJWT(token);
            customWebSocketSession.setUserId(userId);
            customWebSocketSession.reply("authenticated");
        } catch (Exception e) {
            logger.debug("Token is invalid. Token : " + token);
            customWebSocketSession.fail("authentication failed");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        CustomWebSocketSession customWebSocketSession = new CustomWebSocketSession(session);
        logger.debug("WebSocketSession[{}] Received message `{}`", customWebSocketSession.getId(),
                message.getPayload());

        IncomingMessage incomingMessage = JsonUtils.toObject(message.getPayload(), IncomingMessage.class);
        if (incomingMessage == null) {
            customWebSocketSession.error("Message payload is malformed. Payload: " + message.getPayload());
            return;
        }

        ChannelHandlerInvoker channelHandlerInvoker = channelHandlerResolver.getInvoker(incomingMessage);
        if (channelHandlerInvoker == null) {
            String errorDetails = "No handler found for action `" + incomingMessage.getAction() + "` at channel `"
                    + incomingMessage.getChannel() + "`";
            customWebSocketSession.error(errorDetails);
            logger.error("RealTimeSession[{}] {}", customWebSocketSession.getId(), errorDetails);
            return;
        }

        channelHandlerInvoker.handle(incomingMessage, customWebSocketSession);
    }

}
