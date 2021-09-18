package tr.com.beb.boardgame.web.socket.updater;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import tr.com.beb.boardgame.domain.model.game.GameId;
import tr.com.beb.boardgame.domain.model.game.GameSession;
import tr.com.beb.boardgame.utils.JsonUtils;
import tr.com.beb.boardgame.web.socket.SubscriptionManager;

@Component
public class GameLobbyUpdater {

    public void onGameCreated(GameId gameId, GameSession gameSession) {
        Map<String, Object> gameSessionData = new HashMap<>();

        gameSessionData.put("gameId", gameId.getValue());
        gameSessionData.put("gameStatus", gameSession.getGameStatus().name());
        gameSessionData.put("playerA", gameSession.getPlayerA().getValue());
        gameSessionData.put("pitCount", gameSession.getPitCount());
        gameSessionData.put("itemsPerPit", gameSession.getItemsPerPit());
        gameSessionData.put("sortingReference", gameSession.getCreatedDate().getTime());

        Map<String, Object> data = new HashMap<>();
        data.put("type", "gameCreated");
        data.put("gameData", gameSessionData);

        SubscriptionManager.send("/game-lobby/", JsonUtils.toJson(data));
    }

    public void onGameCancelled(GameId gameId, GameSession gameSession) {
        Map<String, Object> gameSessionData = new HashMap<>();

        gameSessionData.put("gameId", gameId.getValue());
        gameSessionData.put("gameStatus", gameSession.getGameStatus().name());
        gameSessionData.put("playerA", gameSession.getPlayerA().getValue());
        gameSessionData.put("sortingReference", gameSession.getCreatedDate().getTime());

        Map<String, Object> data = new HashMap<>();
        data.put("type", "gameCancelled");
        data.put("gameData", gameSessionData);

        SubscriptionManager.send("/game-lobby/", JsonUtils.toJson(data));
    }

    public void onGameStarted(GameId gameId, GameSession gameSession) {
        Map<String, Object> gameSessionData = new HashMap<>();

        gameSessionData.put("gameId", gameId.getValue());
        gameSessionData.put("gameStatus", gameSession.getGameStatus().name());
        gameSessionData.put("playerA", gameSession.getPlayerA().getValue());
        gameSessionData.put("playerB", gameSession.getPlayerB().getValue());
        gameSessionData.put("sortingReference", gameSession.getCreatedDate().getTime());

        Map<String, Object> data = new HashMap<>();
        data.put("type", "gameStarted");
        data.put("gameData", gameSessionData);

        SubscriptionManager.send("/game-lobby/", JsonUtils.toJson(data));

    }

    public void onPlayerLeft(GameId gameId) {
        Map<String, Object> gameSessionData = new HashMap<>();

        gameSessionData.put("gameId", gameId.getValue());

        Map<String, Object> data = new HashMap<>();
        data.put("type", "playerLeft");
        data.put("gameData", gameSessionData);

        SubscriptionManager.send("/game-lobby/", JsonUtils.toJson(data));

    }

}
