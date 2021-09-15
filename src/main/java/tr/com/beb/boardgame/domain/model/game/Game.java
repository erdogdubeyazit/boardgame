package tr.com.beb.boardgame.domain.model.game;

import tr.com.beb.boardgame.domain.model.Player;
import tr.com.beb.boardgame.domain.model.board.Board;
import tr.com.beb.boardgame.domain.model.board.Pit;
import tr.com.beb.boardgame.domain.model.board.SimplePit;
import tr.com.beb.boardgame.domain.model.board.Tank;

public class Game {

    private Board board;
    private Player currentPlayer;
    private GameStatus gameStatus;

    public Game(int pitCount, int itemsPerPit) {
        this.board = new Board(pitCount, itemsPerPit);
        gameStatus = GameStatus.STARTED;
        currentPlayer = Player.A;
    }

    public void play(int pitIndex) throws PitEmptyException, GameIsNotPlayableException, InvalidPitIndexException {

        if (gameStatus != GameStatus.STARTED)
            throw new GameIsNotPlayableException();

        Pit[] pits = board.getPits();
        if (pitIndex < 0 || pits[pitIndex] instanceof Tank || pits[pitIndex].getPlayer() != currentPlayer
                || pitIndex >= pits.length)
            throw new InvalidPitIndexException("Pit index is out of bounds");

        int actualPitIndex = (board.getPitCount() + 1) * currentPlayer.getCode() + pitIndex;
        // SimplePit startPit = (SimplePit)
        SimplePit startPit = (SimplePit) pits[actualPitIndex];

        int itemsToProcess = startPit.collectItems();

        if (itemsToProcess == 0)
            throw new PitEmptyException();

        if (pits[actualPitIndex] instanceof Tank)
            throw new InvalidPitIndexException("This index refers to a tank");

        Pit actualPit = (Pit) startPit;
        while (itemsToProcess > 0) {
            int nextPitIndex = (actualPitIndex + 1) % (pits.length);
            Pit nextPit = pits[nextPitIndex];
            boolean addItemResult = nextPit.addItem(currentPlayer);
            if (addItemResult)
                itemsToProcess--;

            actualPitIndex = nextPitIndex;
            actualPit = nextPit;
        }

        Player nextPlayer = currentPlayer;
        if (!(actualPit instanceof Tank))
            nextPlayer = changePlayer(currentPlayer);

        if (actualPit instanceof SimplePit && actualPit.getPlayer() == currentPlayer && actualPit.getItemCount() == 1) {
            int ownPitIndex = actualPitIndex - (board.getPitCount() + 1) * currentPlayer.getCode();
            collectOwnAndOppositePitsIntoOwnTank(ownPitIndex);
        }

        if (playerOutOfItems(currentPlayer)) {
            closeGame();
        } else {
            currentPlayer = nextPlayer;
        }

    }

    public Winner getWinner() throws GameNotFinishedYetException {
        if (gameStatus != GameStatus.COMPLETED)
            throw new GameNotFinishedYetException();
        Pit tankA = (Pit) board.getPlayerTank(Player.A);
        Pit tankB = (Pit) board.getPlayerTank(Player.B);

        int itemCountA = tankA.getItemCount();
        int itemCountB = tankB.getItemCount();

        Winner winner = new Winner();
        if (itemCountA > itemCountB) {
            winner.setPlayer(Player.A);
            winner.setPoints(itemCountA);
            return winner;
        } else if (itemCountA < itemCountB) {
            winner.setPlayer(Player.B);
            winner.setPoints(itemCountB);
            return winner;
        } else
            return null;

    }

    private void closeGame() {
        gameStatus = GameStatus.COMPLETED;
        collectAllUserItemsIntoUserTanks();
    }

    private boolean playerOutOfItems(Player player) {
        int sum = 0;
        Pit[] playerPits = board.getPlayerPits(player);
        for (int i = 0; i < playerPits.length; i++)
            sum += playerPits[i].getItemCount();
        return sum == 0;
    }

    private Player changePlayer(Player player) {
        return player == Player.A ? Player.B : Player.A;
    }

    private void collectOwnAndOppositePitsIntoOwnTank(int ownPitIndex) throws InvalidPitIndexException {
        SimplePit ownPit = (SimplePit) board.getPits()[ownPitIndex];
        SimplePit oppositePit = (SimplePit) board.getOppositePit(ownPitIndex);

        int itemCount = ownPit.collectItems();
        itemCount += oppositePit.collectItems();

        Tank playerTank = board.getPlayerTank(currentPlayer);

        playerTank.addItems(itemCount);
    }

    private void collectAllUserItemsIntoUserTanks() {
        // A
        collectPitsIntoTank(Player.A);
        // B
        collectPitsIntoTank(Player.B);
    }

    private void collectPitsIntoTank(Player player) {
        Tank tank = board.getPlayerTank(player);
        Pit[] pits = board.getPlayerPits(player);
        int sum = 0;
        for (int i = 0; i < pits.length; i++)
            sum += ((SimplePit) pits[i]).collectItems();

        tank.addItems(sum);

    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

}
