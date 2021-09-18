package tr.com.beb.boardgame.web.socket.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.com.beb.boardgame.web.socket.Action;
import tr.com.beb.boardgame.web.socket.ChannelHandler;
import tr.com.beb.boardgame.web.socket.ChannelValue;
import tr.com.beb.boardgame.web.socket.CustomWebSocketSession;
import tr.com.beb.boardgame.web.socket.SubscriptionManager;

@ChannelHandler("/game-scene/*")
public class GameSceneHandler {

    private static final Logger logger = LoggerFactory.getLogger(GameSceneHandler.class);

    @Action("subscribe")
    public void subscribe(CustomWebSocketSession customWebSocketSession, @ChannelValue String channel) {
        logger.debug("WebSocketSession({}) subsribed to channel {}", customWebSocketSession.getId(), channel);
        SubscriptionManager.subscribe(customWebSocketSession, channel);
    }

    @Action("unsubscribe")
    public void unsubscribe(CustomWebSocketSession customWebSocketSession, @ChannelValue String channel){
        logger.debug("WebSocketSession({}) unsubsribed from channel {}", customWebSocketSession.getId(), channel);
        SubscriptionManager.unsubscribe(customWebSocketSession, channel);
    }

}
