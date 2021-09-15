package tr.com.beb.boardgame.domain.model.game;

import java.util.Arrays;

import tr.com.beb.boardgame.domain.model.board.Pit;
import tr.com.beb.boardgame.domain.model.user.UserId;

public class GameSession {

    private GameId gameId;
    private UserId playerA;
    private UserId playerB;
    private UserId currentPlayer;
    private GameStatus gameStatus;
    private Integer pitCount;
    private Integer itemsPerPit;
    private Pit[] pits;
    private Winner winner;
    private int turn;

    public GameSession(GameId gameId, UserId playerA, UserId playerB, UserId currentPlayer, GameStatus gameStatus,
            Integer pitCount, Integer itemsPerPit, Pit[] pits, Winner winner, int turn) {
        this.gameId = gameId;
        this.playerA = playerA;
        this.playerB = playerB;
        this.currentPlayer = currentPlayer;
        this.gameStatus = gameStatus;
        this.pitCount = pitCount;
        this.itemsPerPit = itemsPerPit;
        this.pits = pits;
        this.winner = winner;
        this.turn = turn;
    }

    public GameId getGameId() {
        return gameId;
    }

    public UserId getPlayerA() {
        return playerA;
    }

    public UserId getPlayerB() {
        return playerB;
    }

    public UserId getCurrentPlayer() {
        return currentPlayer;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public Integer getPitCount() {
        return pitCount;
    }

    public Integer getItemsPerPit() {
        return itemsPerPit;
    }

    public Pit[] getPits() {
        return pits;
    }

    public Winner getWinner() {
        return winner;
    }

    public int getTurn() {
        return turn;
    }

    @Override
    public String toString() {
        return "GameSession [currentPlayer=" + currentPlayer + ", gameId=" + gameId + ", gameStatus=" + gameStatus
                + ", itemsPerPit=" + itemsPerPit + ", pitCount=" + pitCount + ", pits=" + Arrays.toString(pits)
                + ", playerA=" + playerA + ", playerB=" + playerB + ", turn=" + turn + ", winner=" + winner + "]";
    }

    

}
