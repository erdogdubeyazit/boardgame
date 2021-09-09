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
        gameStatus = GameStatus.OPEN;
        currentPlayer = Player.A;
    }

    public void play(int pitIndex) throws PitEmptyException, GameAlreadyFinishedException, InvalidPitIndexException {

        if (gameStatus != GameStatus.OPEN)
            throw new GameAlreadyFinishedException();

        Pit[] pits = board.getPits();
        if (pitIndex >= pits.length)
            throw new InvalidPitIndexException("Pit index is out of bounds");

        // SimplePit startPit = (SimplePit)
        SimplePit startPit = (SimplePit) pits[(board.getPitCount() + 1) * currentPlayer.getCode() + pitIndex];

        int itemsToProcess = startPit.collectItems();

        if (itemsToProcess == 0)
            throw new PitEmptyException();

        int actualPitIndex = (board.getPitCount() + 1) * currentPlayer.getCode() + pitIndex;

        if (pits[actualPitIndex] instanceof Tank)
            throw new InvalidPitIndexException("This index refers to a tank");

        Pit actualPit = pits[(actualPitIndex) % (pits.length)];
        while (itemsToProcess > 0) {
            boolean addItemResult = actualPit.addItem(currentPlayer);
            if (addItemResult)
                itemsToProcess--;
            actualPitIndex = (actualPitIndex + 1) % (pits.length);
            actualPit = pits[(actualPitIndex) % (pits.length)];
        }

        // Get last processed indexx and pit
        int lastProcessedIndex = (pits.length + actualPitIndex - 1) % (pits.length);
        Pit lastProcessedPit = pits[lastProcessedIndex];

        Player nextPlayer = currentPlayer;
        if (!(lastProcessedPit instanceof Tank))
            nextPlayer = changePlayer(currentPlayer);

        if (lastProcessedPit instanceof SimplePit && lastProcessedPit.getPlayer() == currentPlayer
                && lastProcessedPit.getItemCount() == 1) {
            int ownPitIndex = lastProcessedIndex - (board.getPitCount() + 1) * currentPlayer.getCode();
            collectOwnAndOppositePitsIntoTank(ownPitIndex);
        }

        if (playerOutOfItems(currentPlayer)) {
            closeGame();
        } else {
            currentPlayer = nextPlayer;
        }

    }

    public Winner getWinner() {
        Pit tankA = (Pit) board.getPlayerTank(Player.A);
        Pit tankB = (Pit) board.getPlayerTank(Player.B);

        int itemCountA = tankA.getItemCount();
        int itemCountB = tankB.getItemCount();

        Winner winner = new Winner();
        if (itemCountA > itemCountB) {
            winner.setPlayer(Player.A);
            winner.setPoints(itemCountA);
        } else {
            winner.setPlayer(Player.B);
            winner.setPoints(itemCountB);
        }

        return winner;
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

    private void collectOwnAndOppositePitsIntoTank(int ownPitIndex) {
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

}
