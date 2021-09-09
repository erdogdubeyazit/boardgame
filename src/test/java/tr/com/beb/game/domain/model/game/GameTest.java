package tr.com.beb.game.domain.model.game;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import tr.com.beb.boardgame.domain.model.Player;
import tr.com.beb.boardgame.domain.model.board.Board;
import tr.com.beb.boardgame.domain.model.game.Game;
import tr.com.beb.boardgame.domain.model.game.GameAlreadyFinishedException;
import tr.com.beb.boardgame.domain.model.game.GameNotFinishedYetException;
import tr.com.beb.boardgame.domain.model.game.GameStatus;
import tr.com.beb.boardgame.domain.model.game.InvalidPitIndexException;
import tr.com.beb.boardgame.domain.model.game.PitEmptyException;
import tr.com.beb.boardgame.domain.model.game.Winner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class GameTest {

    @Test
    public void simple_move_should_succeed() {
        Exception ex = null;
        try {
            Game game = new Game(6, 6);
            game.play(0);
            Board board = game.getBoard();

            assertEquals(0, board.getPits()[0].getItemCount());
            for (int i = 1; i <= 5; i++)
                assertEquals(7, board.getPits()[i].getItemCount());
        } catch (PitEmptyException | GameAlreadyFinishedException | InvalidPitIndexException e) {
            e.printStackTrace();
            ex = e;
        }
        assertEquals(null, ex);
    }

    @Test
    public void opponent_tank_should_be_ignored_while_sowing() {
        Exception ex = null;
        try {
            Game game = new Game(6, 9);
            game.play(5);
            Board board = game.getBoard();

            assertEquals(0, board.getPits()[5].getItemCount());
            assertEquals(1, board.getPits()[6].getItemCount());
            for (int i = 7; i <= 12; i++)
                assertEquals(10, board.getPits()[i].getItemCount());
            assertEquals(0, board.getPits()[13].getItemCount());
            assertEquals(10, board.getPits()[0].getItemCount());

        } catch (PitEmptyException | GameAlreadyFinishedException | InvalidPitIndexException e) {
            e.printStackTrace();
            ex = e;
        }
        assertEquals(null, ex);
    }

    @Test
    public void opposite_pit_should_be_collected_when_last_item_falls_into_own_empty_pit() {
        Exception ex = null;
        try {
            Game game = new Game(6, 6);
            Board board = game.getBoard();

            board.getPits()[0].setItemCount(1);
            board.getPits()[1].setItemCount(0);

            assertEquals(6, board.getOppositePit(1).getItemCount());
            game.play(0);

            assertEquals(7, board.getPits()[6].getItemCount());
            assertEquals(0, board.getOppositePit(1).getItemCount());

        } catch (PitEmptyException | GameAlreadyFinishedException | InvalidPitIndexException e) {
            e.printStackTrace();
            ex = e;
        }
        assertEquals(null, ex);
    }

    @Test
    public void last_item_in_own_tank_should_give_extra_turn() {

        Exception ex = null;
        try {
            Game game = new Game(6, 6);
            game.play(0);

            assertEquals(Player.A, game.getCurrentPlayer());

            game = new Game(6, 6);
            game.play(1);
            assertEquals(Player.B, game.getCurrentPlayer());

        } catch (PitEmptyException | GameAlreadyFinishedException | InvalidPitIndexException e) {
            e.printStackTrace();
            ex = e;
        }
        assertEquals(null, ex);

    }

    @Test
    public void test_winner() {

        Exception ex = null;
        try {
            Game game = new Game(6, 6);
            Board board = game.getBoard();
            for (int i = 0; i < 5; i++)
                board.getPits()[i].setItemCount(0);

            board.getPits()[5].setItemCount(1);
            board.getPlayerTank(Player.A).addItems(35);

            for (int i = 7; i <= 12; i++)
                board.getPits()[i].setItemCount(5);

            game.play(5);

            assertEquals(GameStatus.COMPLETED, game.getGameStatus());
            Winner winner = game.getWinner();

            assertEquals(Player.A, winner.getPlayer());
            assertEquals(36, winner.getPoints());

        } catch (PitEmptyException | GameAlreadyFinishedException | InvalidPitIndexException
                | GameNotFinishedYetException e) {
            e.printStackTrace();
            ex = e;
        }
        assertEquals(null, ex);

    }

    @Test
    public void test_tie() {

        Exception ex = null;
        try {
            Game game = new Game(6, 6);
            Board board = game.getBoard();
            for (int i = 0; i < 5; i++)
                board.getPits()[i].setItemCount(0);

            board.getPits()[5].setItemCount(1);
            board.getPlayerTank(Player.A).addItems(35);

            game.play(5);

            assertEquals(GameStatus.COMPLETED, game.getGameStatus());
            Winner winner = game.getWinner();

            assertNull(winner);

        } catch (PitEmptyException | GameAlreadyFinishedException | InvalidPitIndexException
                | GameNotFinishedYetException e) {
            e.printStackTrace();
            ex = e;
        }
        assertEquals(null, ex);

    }

    @Test(expected = GameNotFinishedYetException.class)
    public void getWinner_with_uncompleted_game_should_fail() throws Exception {
        Game game = new Game(6, 6);
        game.getWinner();
    }

    @Test(expected = GameAlreadyFinishedException.class)
    public void playing_with_closed_game_should_fail() throws Exception {
        Game game = new Game(6, 6);
        game.setGameStatus(GameStatus.COMPLETED);
        game.play(0);
        game.setGameStatus(GameStatus.INTERRUPTED);
        game.play(0);
    }

    @Test(expected = InvalidPitIndexException.class)
    public void playing_with_invalid_pit_index_should_fail() throws Exception {
        Game game = new Game(6, 6);
        game.play(-1);

    }

    @Test(expected = InvalidPitIndexException.class)
    public void playing_with_tank_should_fail() throws Exception {
        Game game = new Game(6, 6);
        game.play(6);
    }

    @Test(expected = InvalidPitIndexException.class)
    public void playing_with_opponent_pit_should_fail() throws Exception {
        Game game = new Game(6, 6);
        game.play(7);
    }

    @Test(expected = PitEmptyException.class)
    public void playing_with_empty_pit_should_fail() throws Exception {
        Game game = new Game(6, 6);
        game.getBoard().getPits()[0].setItemCount(0);
        game.play(0);
    }

}
