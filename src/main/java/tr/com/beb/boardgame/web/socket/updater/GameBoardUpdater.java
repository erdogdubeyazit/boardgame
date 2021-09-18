package tr.com.beb.boardgame.web.socket.updater;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import tr.com.beb.boardgame.domain.model.game.GameId;
import tr.com.beb.boardgame.domain.model.game.GameSession;
import tr.com.beb.boardgame.utils.JsonUtils;
import tr.com.beb.boardgame.web.socket.SubscriptionManager;

@Component
public class GameBoardUpdater {

    public void onPlayed(GameId gameId, GameSession gameSession) {
        Map<String, Object> gameSessionData = new HashMap<>();

        if (gameSession.getPlayerA() != null)
            gameSessionData.put("playerA", gameSession.getPlayerA().getValue());

        if (gameSession.getPlayerB() != null)
            gameSessionData.put("playerB", gameSession.getPlayerB().getValue());

        if (gameSession.getCurrentPlayer() != null)
            gameSessionData.put("currentPlayer", gameSession.getCurrentPlayer().getValue());

        if (gameSession.getGameStatus() != null)
            gameSessionData.put("gameStatus", gameSession.getGameStatus().name());

        if (gameSession.getPitCount() != null)
            gameSessionData.put("pitCount", gameSession.getPitCount());

        if (gameSession.getItemsPerPit() != null)
            gameSessionData.put("itemsPerPit", gameSession.getItemsPerPit());

        if (gameSession.getPlayerAPits() != null)
            gameSessionData.put("playerAPits",
                    Stream.of(gameSession.getPlayerAPits()).map(s -> s.getItemCount()).collect(Collectors.toList()));

        if (gameSession.getPlayerBPits() != null)
            gameSessionData.put("playerBPits",
                    Stream.of(gameSession.getPlayerBPits()).map(s -> s.getItemCount()).collect(Collectors.toList()));

        if (gameSession.getTankA() != null)
            gameSessionData.put("tankA", gameSession.getTankA().getItemCount());

        if (gameSession.getTankB() != null)
            gameSessionData.put("tankB", gameSession.getTankB().getItemCount());

        if (gameSession.getWinner() != null)
            gameSessionData.put("winner", gameSession.getWinner());

        gameSessionData.put("turn", gameSession.getTurn());

        Map<String, Object> data = new HashMap<>();
        data.put("type", "gamePlayed");
        data.put("gameData", gameSessionData);

        SubscriptionManager.send("/game-scene/" + gameId.getValue(), JsonUtils.toJson(data));
    }

    public void onPlayerLeft(GameId gameId) {
        Map<String, Object> gameSessionData = new HashMap<>();

        gameSessionData.put("gameId", gameId.getValue());

        Map<String, Object> data = new HashMap<>();
        data.put("type", "playerLeft");
        data.put("gameData", gameSessionData);

        SubscriptionManager.send("/game-scene/" + gameId.getValue(), JsonUtils.toJson(data));

    }

}
