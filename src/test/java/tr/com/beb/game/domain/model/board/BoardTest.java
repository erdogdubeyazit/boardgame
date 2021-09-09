package tr.com.beb.game.domain.model.board;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import tr.com.beb.boardgame.domain.model.Player;
import tr.com.beb.boardgame.domain.model.board.Board;
import tr.com.beb.boardgame.domain.model.board.Pit;
import tr.com.beb.boardgame.domain.model.game.InvalidPitIndexException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class BoardTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board(6, 6);
    }

    @Test
    public void test_getPlayerTank() {
        Pit expectedPit = board.getPits()[6];
        Pit playerTank = (Pit) board.getPlayerTank(Player.A);

        assertEquals(expectedPit, playerTank);

        expectedPit = board.getPits()[13];
        playerTank = (Pit) board.getPlayerTank(Player.B);

        assertEquals(expectedPit, playerTank);
    }

    @Test
    public void test_getPlayerPits() {

        Pit[] playerPits = board.getPlayerPits(Player.A);
        Pit[] expectedPits = new Pit[6];
        for (int i = 0; i < 6; i++)
            expectedPits[i] = board.getPits()[i];
        assertArrayEquals(expectedPits, playerPits);

        playerPits = board.getPlayerPits(Player.B);
        expectedPits = new Pit[6];
        for (int i = 7; i < 13; i++)
            expectedPits[i - 7] = board.getPits()[i];
        assertArrayEquals(expectedPits, playerPits);

    }

    @Test
    public void test_getOppositePit() throws Exception {
        Pit expected = board.getPits()[11];
        Pit actual = board.getOppositePit(1);
        assertEquals(expected, actual);

        expected = board.getPits()[2];
        actual = board.getOppositePit(10);
        assertEquals(expected, actual);
    }

    @Test(expected = InvalidPitIndexException.class)
    public void getOppositePit_with_tank_shoul_fail() throws Exception {
        board.getOppositePit(6);
    }
}
