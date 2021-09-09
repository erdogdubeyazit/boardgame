package tr.com.beb.boardgame.domain.model.board;

import tr.com.beb.boardgame.domain.model.Player;
import tr.com.beb.boardgame.domain.model.board.impl.SimplePitImpl;
import tr.com.beb.boardgame.domain.model.board.impl.TankImpl;

public class Board {

    private int pitCount;
    private int itemsPerPit;

    private Pit[] pits;

    public Board(int pitCount, int itemsPerPit) {
        this.pitCount = pitCount;
        this.itemsPerPit = itemsPerPit;

        initializePits();

    }

    private void initializePits() {
        pits = new Pit[2 * pitCount + 2];

        for (int i = 0; i < pitCount; i++) {
            pits[i] = new SimplePitImpl(Player.A, itemsPerPit);
            pits[pitCount + i + 1] = new SimplePitImpl(Player.B, itemsPerPit);
        }

        pits[pitCount] = new TankImpl(Player.A, 0);
        pits[2 * pitCount + 1] = new TankImpl(Player.B, 0);

    }

    public Tank getPlayerTank(Player player) {
        return (Tank) pits[pitCount + player.getCode() * (pitCount + 1)];
    }

    public Pit[] getPlayerPits(Player player) {
        Pit[] result = new Pit[pitCount];
        for (int i = 0; i < pitCount; i++) {
            result[i] = (Pit) pits[(pitCount + 1) * player.getCode() + i];
        }

        return result;
    }

    public Pit[] getPits() {
        return pits;
    }

    public Pit getOppositePit(int pitIndex) {
        return pits[pits.length - pitIndex - 2];
    }

    public int getPitCount() {
        return pitCount;
    }

    public int getItemsPerPit() {
        return itemsPerPit;
    }
}
