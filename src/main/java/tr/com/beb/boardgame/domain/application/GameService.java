package tr.com.beb.boardgame.domain.application;

import java.util.List;
import java.util.NoSuchElementException;

import tr.com.beb.boardgame.domain.model.game.GameId;
import tr.com.beb.boardgame.domain.model.game.GameIsNotPlayableException;
import tr.com.beb.boardgame.domain.model.game.GameNotFinishedYetException;
import tr.com.beb.boardgame.domain.model.game.GameSession;
import tr.com.beb.boardgame.domain.model.game.IllegalGameAccessException;
import tr.com.beb.boardgame.domain.model.game.IncompatibleGameStateException;
import tr.com.beb.boardgame.domain.model.game.InvalidPitIndexException;
import tr.com.beb.boardgame.domain.model.game.PitEmptyException;
import tr.com.beb.boardgame.domain.model.user.UserId;

/**
 * Game operations
 */
public interface GameService {

        GameSession create(UserId userId, int pitCount, int itemsPerPit);

        GameSession join(GameId gameId, UserId userId)
                        throws IncompatibleGameStateException, NoSuchElementException, IllegalGameAccessException;

        GameSession loadGame(GameId gameId, UserId userId)
                        throws IncompatibleGameStateException, NoSuchElementException, IllegalGameAccessException, GameNotFinishedYetException;

        GameSession play(GameId gameId, UserId userId, int pitIndex) throws PitEmptyException,
                        GameIsNotPlayableException, InvalidPitIndexException, GameNotFinishedYetException;

        GameSession cancel(GameId gameId, UserId userId)
                        throws IncompatibleGameStateException, NoSuchElementException, IllegalGameAccessException;

        void leave(GameId gameId, UserId userId)
                        throws IncompatibleGameStateException, NoSuchElementException, IllegalGameAccessException;

        List<GameSession> getMyGames(UserId userId);

        List<GameSession> getAvailableGamesByUser(UserId userId);

        List<GameSession> getActiveGamesByUser(UserId userId);
}
