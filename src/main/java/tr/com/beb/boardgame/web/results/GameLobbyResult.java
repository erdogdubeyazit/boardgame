package tr.com.beb.boardgame.web.results;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;

import tr.com.beb.boardgame.domain.model.game.GameSession;

public class GameLobbyResult {

    public static ResponseEntity<ApiResult> build(List<GameSession> myGames,List<GameSession> availableGames, List<GameSession> activeGames) {

        ApiResult apiResult = ApiResult.blank();

        List<GameSessionData> myGamesData = myGames
                .stream().map(g -> new GameSessionData(
                        g.getGameId().getValue(), 
                        g.getPlayerA().getValue(),
                        g.getPitCount(), 
                        g.getItemsPerPit(), 
                        g.getCreatedDate().getTime()
                        )
                    )
                .collect(Collectors.toList());

        apiResult.add("myGames", myGamesData);

        List<GameSessionData> availableGamesData = availableGames
                .stream().map(g -> new GameSessionData(
                        g.getGameId().getValue(), 
                        g.getPlayerA().getValue(),
                        g.getPitCount(), 
                        g.getItemsPerPit(), 
                        g.getCreatedDate().getTime()
                        )
                    )
                .collect(Collectors.toList());

        apiResult.add("availableGames", availableGamesData);

        List<GameSessionData> activeGamesData = activeGames
                .stream().map(g -> new GameSessionData(
                        g.getGameId().getValue(), 
                        null,
                        g.getPitCount(), 
                        g.getItemsPerPit(), 
                        g.getStartTime().getTime()
                        )
                    )
                .collect(Collectors.toList());

        apiResult.add("activeGames", activeGamesData);

        return Result.ok(apiResult);

    }

    private static class GameSessionData {
        private String gameId;
        private String playerA;
        private Integer pitCount;
        private Integer itemsPerPit;
        private Long sortingReference;

        public GameSessionData(String gameId, String playerA, Integer pitCount, Integer itemsPerPit,
                Long sortingReference) {
            this.gameId = gameId;
            this.playerA = playerA;
            this.pitCount = pitCount;
            this.itemsPerPit = itemsPerPit;
            this.sortingReference = sortingReference;
        }

        public String getGameId() {
            return gameId;
        }

        public Integer getPitCount() {
            return pitCount;
        }

        public Integer getItemsPerPit() {
            return itemsPerPit;
        }

        public String getPlayerA() {
            return playerA;
        }

        public Long getSortingReference() {
            return sortingReference;
        }
    }

}
