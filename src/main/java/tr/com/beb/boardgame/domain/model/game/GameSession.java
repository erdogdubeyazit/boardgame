package tr.com.beb.boardgame.domain.model.game;

import java.util.Arrays;
import java.util.Date;

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
    private Pit[] playerAPits;
    private Pit[] playerBPits;
    private Pit tankA;
    private Pit tankB;
    private Date createdDate;
    private Date startTime;
    private Date endTime;

    private Winner winner;
    private int turn;

    public GameSession(GameId gameId, UserId playerA, UserId playerB, UserId currentPlayer, GameStatus gameStatus,
            Integer pitCount, Integer itemsPerPit, Pit[] playerAPits, Pit[] playerBPits, Pit tankA, Pit tankB,
            Date createdDate, Date startTime, Date endTime, Winner winner, int turn) {
        this.gameId = gameId;
        this.playerA = playerA;
        this.playerB = playerB;
        this.currentPlayer = currentPlayer;
        this.gameStatus = gameStatus;
        this.pitCount = pitCount;
        this.itemsPerPit = itemsPerPit;
        this.playerAPits = playerAPits;
        this.playerBPits = playerBPits;
        this.tankA = tankA;
        this.tankB = tankB;
        this.createdDate = createdDate;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public Pit[] getPlayerAPits() {
        return playerAPits;
    }

    public Pit[] getPlayerBPits() {
        return playerBPits;
    }

    public Pit getTankA() {
        return tankA;
    }

    public Pit getTankB() {
        return tankB;
    }

    public Winner getWinner() {
        return winner;
    }

    public int getTurn() {
        return turn;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "GameSession [createdDate=" + createdDate + ", currentPlayer=" + currentPlayer + ", endTime=" + endTime
                + ", gameId=" + gameId + ", gameStatus=" + gameStatus + ", itemsPerPit=" + itemsPerPit + ", pitCount="
                + pitCount + ", playerA=" + playerA + ", playerAPits=" + Arrays.toString(playerAPits) + ", playerB="
                + playerB + ", playerBPits=" + Arrays.toString(playerBPits) + ", startTime=" + startTime + ", tankA="
                + tankA + ", tankB=" + tankB + ", turn=" + turn + ", winner=" + winner + "]";
    }

}
