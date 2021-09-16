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
    private Pit[] playerAPits;
    private Pit[] playerBPits;
    private Pit tankA;
    private Pit tankB;
    
    private Winner winner;
    private int turn;
    
    public GameSession(GameId gameId, UserId playerA, UserId playerB, UserId currentPlayer, GameStatus gameStatus,
            Integer pitCount, Integer itemsPerPit, Pit[] playerAPits, Pit[] playerBPits, Pit tankA, Pit tankB,
            Winner winner, int turn) {
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

    @Override
    public String toString() {
        return "GameSession [currentPlayer=" + currentPlayer + ", gameId=" + gameId + ", gameStatus=" + gameStatus
                + ", itemsPerPit=" + itemsPerPit + ", pitCount=" + pitCount + ", playerA=" + playerA + ", playerAPits="
                + Arrays.toString(playerAPits) + ", playerB=" + playerB + ", playerBPits="
                + Arrays.toString(playerBPits) + ", tankA=" + tankA + ", tankB=" + tankB + ", turn=" + turn
                + ", winner=" + winner + "]";
    }



    

}
