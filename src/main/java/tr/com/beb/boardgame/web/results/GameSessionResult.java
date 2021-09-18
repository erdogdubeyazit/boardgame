package tr.com.beb.boardgame.web.results;

import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;

import tr.com.beb.boardgame.domain.model.game.GameSession;

public class GameSessionResult {
    public static ResponseEntity<ApiResult> build(GameSession gameSession) {

        ApiResult apiResult = ApiResult.blank();

        if (gameSession.getGameId() != null)
            apiResult.add("gameId", gameSession.getGameId().getValue());

        if (gameSession.getPlayerA() != null)
            apiResult.add("playerA", gameSession.getPlayerA().getValue());

        if (gameSession.getPlayerB() != null)
            apiResult.add("playerB", gameSession.getPlayerB().getValue());

        if (gameSession.getCurrentPlayer() != null)
            apiResult.add("currentPlayer", gameSession.getCurrentPlayer().getValue());

        if (gameSession.getGameStatus() != null)
            apiResult.add("gameStatus", gameSession.getGameStatus().name());

        if (gameSession.getPitCount() != null)
            apiResult.add("pitCount", gameSession.getPitCount());

        if (gameSession.getItemsPerPit() != null)
            apiResult.add("itemsPerPit", gameSession.getItemsPerPit());

        if (gameSession.getPlayerAPits() != null)
            apiResult.add("playerAPits", Stream.of(gameSession.getPlayerAPits()).map(s -> s.getItemCount()));

        if (gameSession.getPlayerBPits() != null)
            apiResult.add("playerBPits", Stream.of(gameSession.getPlayerBPits()).map(s -> s.getItemCount()));

        if (gameSession.getTankA() != null)
            apiResult.add("tankA", gameSession.getTankA().getItemCount());

        if (gameSession.getTankB() != null)
            apiResult.add("tankB", gameSession.getTankB().getItemCount());

        if (gameSession.getWinner() != null)
            apiResult.add("winner", gameSession.getWinner());

        apiResult.add("sortingReference", gameSession.getCreatedDate().getTime());

        apiResult.add("turn", gameSession.getTurn());

        return Result.ok(apiResult);
    }
}
