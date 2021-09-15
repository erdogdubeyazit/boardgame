package tr.com.beb.boardgame.domain.model.game;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import tr.com.beb.boardgame.domain.common.model.AbstractEntity;
import tr.com.beb.boardgame.domain.model.Player;

@Document(collection = "games")
@TypeAlias("game_session")
public class GameSessionEntity extends AbstractEntity {

    private Integer pitCount;
    private Integer itemsPerPit;
    private String playerA;
    private String playerB;
    private GameStatus gameStatus;
    private Player interruptedBy;
    private Winner winner;
    private Date createdDate;
    private Date startTime;
    private Date endTime;
    private Player nextPlayer;
    private List<BoardEntity> turns;

    public GameSessionEntity() {
    }

    public GameSessionEntity(Integer pitCount, Integer itemsPerPit, String playerA, String playerB,
            GameStatus gameStatus, Player interruptedBy, Winner winner, Date startTime, Date endTime, Player nextPlayer,
            List<BoardEntity> turns) {
        this.pitCount = pitCount;
        this.itemsPerPit = itemsPerPit;
        this.playerA = playerA;
        this.playerB = playerB;
        this.gameStatus = gameStatus;
        this.interruptedBy = interruptedBy;
        this.winner = winner;
        this.startTime = startTime;
        this.endTime = endTime;
        this.nextPlayer = nextPlayer;
        this.turns = turns;
        this.createdDate = new Date();
    }

    public Integer getPitCount() {
        return pitCount;
    }

    public void setPitCount(Integer pitCount) {
        this.pitCount = pitCount;
    }

    public Integer getItemsPerPit() {
        return itemsPerPit;
    }

    public void setItemsPerPit(Integer itemsPerPit) {
        this.itemsPerPit = itemsPerPit;
    }

    public String getPlayerA() {
        return playerA;
    }

    public void setPlayerA(String playerA) {
        this.playerA = playerA;
    }

    public String getPlayerB() {
        return playerB;
    }

    public void setPlayerB(String playerB) {
        this.playerB = playerB;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public Player getInterruptedBy() {
        return interruptedBy;
    }

    public void setInterruptedBy(Player interruptedBy) {
        this.interruptedBy = interruptedBy;
    }

    public Winner getWinner() {
        return winner;
    }

    public void setWinner(Winner winner) {
        this.winner = winner;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Player getNextPlayer() {
        return nextPlayer;
    }

    public void setNextPlayer(Player nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    public List<BoardEntity> getTurns() {
        return turns;
    }

    public void setTurns(List<BoardEntity> turns) {
        this.turns = turns;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GameSessionEntity other = (GameSessionEntity) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }

    

    @Override
    public String toString() {
        return "GameSessionEntity [createdDate=" + createdDate + ", endTime=" + endTime + ", gameStatus=" + gameStatus
                + ", interruptedBy=" + interruptedBy + ", itemsPerPit=" + itemsPerPit + ", nextPlayer=" + nextPlayer
                + ", pitCount=" + pitCount + ", playerA=" + playerA + ", playerB=" + playerB + ", startTime="
                + startTime + ", turns=" + turns + ", winner=" + winner + "]";
    }



    public static class BoardEntity {

        private Player player;
        private int turnIndex;
        private List<PitEntity> pits;

        public BoardEntity() {
        }

        public BoardEntity(Player player, int turnIndex, List<PitEntity> pits) {
            this.player = player;
            this.turnIndex = turnIndex;
            this.pits = pits;
        }

        public Player getPlayer() {
            return player;
        }

        public void setPlayer(Player player) {
            this.player = player;
        }

        public int getTurnIndex() {
            return turnIndex;
        }

        public void setTurnIndex(int turnIndex) {
            this.turnIndex = turnIndex;
        }

        public List<PitEntity> getPits() {
            return pits;
        }

        public void setPits(List<PitEntity> pits) {
            this.pits = pits;
        }

        public static class PitEntity {
            private int index;
            private int itemCount;

            public PitEntity() {
            }

            public PitEntity(int index, int itemCount) {
                this.index = index;
                this.itemCount = itemCount;
            }

            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public int getItemCount() {
                return itemCount;
            }

            public void setItemCount(int itemCount) {
                this.itemCount = itemCount;
            }

        }

    }

}
