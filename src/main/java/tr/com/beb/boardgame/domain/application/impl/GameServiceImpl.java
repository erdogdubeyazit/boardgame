package tr.com.beb.boardgame.domain.application.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import tr.com.beb.boardgame.domain.application.GameService;
import tr.com.beb.boardgame.domain.model.Player;
import tr.com.beb.boardgame.domain.model.board.Board;
import tr.com.beb.boardgame.domain.model.board.Pit;
import tr.com.beb.boardgame.domain.model.game.Game;
import tr.com.beb.boardgame.domain.model.game.GameId;
import tr.com.beb.boardgame.domain.model.game.GameIsNotPlayableException;
import tr.com.beb.boardgame.domain.model.game.GameNotFinishedYetException;
import tr.com.beb.boardgame.domain.model.game.GameSession;
import tr.com.beb.boardgame.domain.model.game.GameSessionEntity;
import tr.com.beb.boardgame.domain.model.game.GameSessionRepository;
import tr.com.beb.boardgame.domain.model.game.GameStatus;
import tr.com.beb.boardgame.domain.model.game.IllegalGameAccessException;
import tr.com.beb.boardgame.domain.model.game.IncompatibleGameStateException;
import tr.com.beb.boardgame.domain.model.game.InvalidPitIndexException;
import tr.com.beb.boardgame.domain.model.game.PitEmptyException;
import tr.com.beb.boardgame.domain.model.game.Winner;
import tr.com.beb.boardgame.domain.model.game.GameSessionEntity.BoardEntity;
import tr.com.beb.boardgame.domain.model.user.UserId;

@Service
public class GameServiceImpl implements GameService {

    private GameSessionRepository gameSessionRepository;

    public GameServiceImpl(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    @Override
    public GameSession create(UserId userId, int pitCount, int itemsPerPit) {
        Assert.notNull(userId, "Parameter `userId` must not be null");
        Assert.isTrue(pitCount>0, "Parameter `pitCount` must not be greater than 0");
        Assert.isTrue(itemsPerPit>0, "Parameter `itemsPerPit` must not be greater than 0");

        GameSessionEntity gameSessionEntity = new GameSessionEntity();
        gameSessionEntity.setPitCount(pitCount);
        gameSessionEntity.setItemsPerPit(itemsPerPit);
        gameSessionEntity.setPlayerA(userId.getValue());
        gameSessionEntity.setGameStatus(GameStatus.CREATED);
        gameSessionEntity.setCreatedDate(new Date());

        gameSessionEntity = gameSessionRepository.save(gameSessionEntity);

        return new GameSession(
                    new GameId(gameSessionEntity.getId()), 
                    new UserId(gameSessionEntity.getPlayerA()), 
                    null, 
                    null,
                    GameStatus.CREATED, 
                    pitCount, 
                    itemsPerPit, 
                    null, 
                    null, 
                    null, 
                    null, 
                    gameSessionEntity.getCreatedDate(), 
                    null, 
                    null, 
                    null, 
                    0
                );
    }

    @Override
    public GameSession join(GameId gameId, UserId userId)
            throws IncompatibleGameStateException, NoSuchElementException, IllegalGameAccessException {

        Assert.notNull(gameId, "Parameter `gameId` must not be null");
        Assert.notNull(userId, "Parameter `userId` must not be null");

        GameSessionEntity gameSessionEntity = gameSessionRepository.findById(gameId.getValue())
                .orElseThrow(() -> new NoSuchElementException("Game not found by id. Id:" + gameId.getValue()));

        if (gameSessionEntity.getGameStatus() != GameStatus.CREATED)
            throw new IncompatibleGameStateException(
                    "Unable to join this game. Game state: " + gameSessionEntity.getGameStatus().name());

        if (gameSessionEntity.getPlayerA() == null || gameSessionEntity.getPlayerA().isEmpty())
            throw new IncompatibleGameStateException(
                    "PlayerA can not be blank. Game id : " + gameSessionEntity.getId());

        if (gameSessionEntity.getPlayerA().equals(String.valueOf(userId.getValue())))
            throw new IllegalGameAccessException("User(id:%s) can not join own game(id:%s)");

        gameSessionEntity.setStartTime(new Date());
        gameSessionEntity.setPlayerB(String.valueOf(userId.getValue()));
        gameSessionEntity.setGameStatus(GameStatus.STARTED);
        gameSessionEntity.setNextPlayer(Player.A);

        gameSessionRepository.save(gameSessionEntity);

        return new GameSession(
                gameId, 
                new UserId(gameSessionEntity.getPlayerA()), 
                new UserId(gameSessionEntity.getPlayerB()), 
                null, 
                gameSessionEntity.getGameStatus(), 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                gameSessionEntity.getCreatedDate(), 
                null, 
                null, 
                null, 
                0
            );
    }

    @Override
    public GameSession play(GameId gameId, UserId userId, int pitIndex) throws PitEmptyException,
            GameIsNotPlayableException, InvalidPitIndexException, GameNotFinishedYetException {

        Assert.notNull(gameId, "Parameter `gameId` must not be null");
        Assert.notNull(userId, "Parameter `userId` must not be null");
        Assert.isTrue(pitIndex>=0, "Parameter `pitCount` must not be greater or equal to 0");

        GameSessionEntity gameSessionEntity = gameSessionRepository.findById(gameId.getValue())
                .orElseThrow(() -> new NoSuchElementException("Game not found by id. Id:" + gameId.getValue()));

        Game game = new Game(gameSessionEntity.getPitCount(), gameSessionEntity.getItemsPerPit());
        game.setCurrentPlayer(gameSessionEntity.getNextPlayer());

        updateGameBoard(game.getBoard(), gameSessionEntity.getTurns());

        game.play(pitIndex);
        gameSessionEntity.setGameStatus(game.getGameStatus());
        gameSessionEntity.addTurn(game.getBoard());

        UserId playerA = new UserId(gameSessionEntity.getPlayerA());
        UserId playerB = new UserId(gameSessionEntity.getPlayerB());
        UserId currentPlayer = game.getCurrentPlayer().compareTo(Player.A) == 0 ? playerA : playerB;

        gameSessionEntity.setNextPlayer(game.getCurrentPlayer());

        Winner winner = game.getGameStatus() == GameStatus.COMPLETED ? game.getWinner() : null;

        int turn = gameSessionEntity.getTurns().size();

        gameSessionEntity = gameSessionRepository.save(gameSessionEntity);

        return new GameSession(
                    gameId, 
                    playerA, 
                    playerB, 
                    currentPlayer, 
                    gameSessionEntity.getGameStatus(),
                    gameSessionEntity.getPitCount(), 
                    gameSessionEntity.getItemsPerPit(),
                    game.getBoard().getPlayerPits(Player.A), 
                    game.getBoard().getPlayerPits(Player.B),
                    (Pit) game.getBoard().getPlayerTank(Player.A), 
                    (Pit) game.getBoard().getPlayerTank(Player.B),
                    gameSessionEntity.getCreatedDate(),
                    gameSessionEntity.getStartTime(),
                    gameSessionEntity.getEndTime(), 
                    winner,
                    turn
                );
    }

    private void updateGameBoard(Board board, List<BoardEntity> turns) {
        if (turns != null && turns.size() > 0) {
            BoardEntity lastBoardEntity = turns.get(turns.size() - 1);
            for (int i = 0; i < lastBoardEntity.getPits().size(); i++) {
                board.getPits()[i].setItemCount(lastBoardEntity.getPits().get(i).getItemCount());
            }
        }
    }

    @Override
    public GameSession cancel(GameId gameId, UserId userId)
            throws IncompatibleGameStateException, NoSuchElementException, IllegalGameAccessException {

        Assert.notNull(gameId, "Parameter `gameId` must not be null");
        Assert.notNull(userId, "Parameter `userId` must not be null");
        
        GameSessionEntity gameSessionEntity = gameSessionRepository.findById(gameId.getValue())
                .orElseThrow(() -> new NoSuchElementException("Game not found by id. Id:" + gameId.getValue()));

        if (gameSessionEntity.getGameStatus() != GameStatus.CREATED)
            throw new IncompatibleGameStateException(
                    "Game can not be cancelled. Game state: " + gameSessionEntity.getGameStatus().name());

        if (gameSessionEntity.getPlayerA() == null || gameSessionEntity.getPlayerA().isEmpty())
            throw new IncompatibleGameStateException(
                    "PlayerA can not be blank. Game id : " + gameSessionEntity.getId());

        if (gameSessionEntity.getPlayerA().equals(userId.getValue()) == false)
            throw new IllegalGameAccessException("Game does not belong to this user");

        gameSessionEntity.setGameStatus(GameStatus.CANCELLED);

        gameSessionRepository.save(gameSessionEntity);

        return new GameSession(
                gameId, 
                new UserId(gameSessionEntity.getPlayerA()), 
                null,
                null, 
                gameSessionEntity.getGameStatus(), 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                gameSessionEntity.getCreatedDate(), 
                null, 
                null, 
                null, 
                0
            );

    }

    @Override
    public void leave(GameId gameId, UserId userId)
            throws IncompatibleGameStateException, NoSuchElementException, IllegalGameAccessException {

        Assert.notNull(gameId, "Parameter `gameId` must not be null");
        Assert.notNull(userId, "Parameter `userId` must not be null");

        GameSessionEntity gameSessionEntity = gameSessionRepository.findById(gameId.getValue())
                .orElseThrow(() -> new NoSuchElementException("Game not found by id. Id:" + gameId.getValue()));

        if (gameSessionEntity.getGameStatus() != GameStatus.STARTED)
            throw new IncompatibleGameStateException(
                    "Unable to leave the game. Game state: " + gameSessionEntity.getGameStatus().name());

        if (gameSessionEntity.getPlayerA() == null || gameSessionEntity.getPlayerA().isEmpty())
            throw new IncompatibleGameStateException(
                    "PlayerA can not be blank. Game id : " + gameSessionEntity.getId());

        if (!(gameSessionEntity.getPlayerA().equals(userId.getValue())
                || gameSessionEntity.getPlayerB().equals(userId.getValue())))
            throw new IllegalGameAccessException(String.format("User(id:%s) is not a player of this game(id:%s)",
                    String.valueOf(userId.getValue()), String.valueOf(gameId.getValue())));

        gameSessionEntity.setInterruptedBy(
                gameSessionEntity.getPlayerA().equals(String.valueOf(userId.getValue())) ? Player.A : Player.B);

        gameSessionEntity.setGameStatus(GameStatus.INTERRUPTED);
        gameSessionEntity.setEndTime(new Date());

        gameSessionRepository.save(gameSessionEntity);

    }

    @Override
    public List<GameSession> getMyGames(UserId userId) {

        Assert.notNull(userId, "Parameter `userId` must not be null");

        return gameSessionRepository.getGamesCreatedByUserAndByStatusOrderByCreatedDateDesc(GameStatus.CREATED, userId.getValue()).stream()
                .map(e -> new GameSession(
                    new GameId(e.getId()), 
                    new UserId(e.getPlayerA()), 
                    null, 
                    null,
                    GameStatus.CREATED, 
                    e.getPitCount(), 
                    e.getItemsPerPit(), 
                    null, 
                    null, 
                    null, 
                    null, 
                    e.getCreatedDate(),
                    e.getStartTime(),
                    e.getEndTime(),
                    null, 
                    0)
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<GameSession> getAvailableGamesByUser(UserId userId) {

        Assert.notNull(userId, "Parameter `userId` must not be null");

        return gameSessionRepository.getGamesNotCreatedByUserAndByStatusOrderByCreatedDateDesc(GameStatus.CREATED, userId.getValue()).stream()
                .map(e -> new GameSession(
                    new GameId(e.getId()), 
                    new UserId(e.getPlayerA()), 
                    null, 
                    null,
                    GameStatus.CREATED, 
                    e.getPitCount(), 
                    e.getItemsPerPit(), 
                    null, 
                    null, 
                    null, 
                    null, 
                    e.getCreatedDate(),
                    e.getStartTime(),
                    e.getEndTime(),
                    null, 
                    0)
                )
                .collect(Collectors.toList());
    }

    @Override
    public GameSession loadGame(GameId gameId, UserId userId) throws IncompatibleGameStateException,
            NoSuchElementException, IllegalGameAccessException, GameNotFinishedYetException {

        Assert.notNull(gameId, "Parameter `gameId` must not be null");
        Assert.notNull(userId, "Parameter `userId` must not be null");

        GameSessionEntity gameSessionEntity = gameSessionRepository.findById(gameId.getValue())
                .orElseThrow(() -> new NoSuchElementException("Game not found by id. Id:" + gameId.getValue()));
                
        if (!(gameSessionEntity.getPlayerA().equals(userId.getValue()) || gameSessionEntity.getPlayerB().equals(userId.getValue())))
            throw new IllegalGameAccessException(
                String.format(
                    "User(id:%s) is not a player of this game(id:%s)", 
                    String.valueOf(userId.getValue()), String.valueOf(gameId.getValue())
                    )
                );

        Game game = new Game(gameSessionEntity.getPitCount(), gameSessionEntity.getItemsPerPit());
        game.setCurrentPlayer(gameSessionEntity.getNextPlayer());

        updateGameBoard(game.getBoard(), gameSessionEntity.getTurns());

        UserId playerA = new UserId(gameSessionEntity.getPlayerA());
        UserId playerB = new UserId(gameSessionEntity.getPlayerB());
        UserId currentPlayer = game.getCurrentPlayer() == Player.A ? playerA : playerB;

        Winner winner = gameSessionEntity.getGameStatus() == GameStatus.COMPLETED ? game.getWinner() : null;

        int turn = gameSessionEntity.getTurns().size();

        return new GameSession(
                    gameId, 
                    playerA, 
                    playerB, 
                    currentPlayer, 
                    gameSessionEntity.getGameStatus(),
                    gameSessionEntity.getPitCount(), 
                    gameSessionEntity.getItemsPerPit(),
                    game.getBoard().getPlayerPits(Player.A), 
                    game.getBoard().getPlayerPits(Player.B),
                    (Pit) game.getBoard().getPlayerTank(Player.A), 
                    (Pit) game.getBoard().getPlayerTank(Player.B),
                    gameSessionEntity.getCreatedDate(),
                    gameSessionEntity.getStartTime(),
                    gameSessionEntity.getEndTime(), 
                    winner,
                    turn
                );

    }

    @Override
    public List<GameSession> getActiveGamesByUser(UserId userId) {
        Assert.notNull(userId, "Parameter `userId` must not be null");
        
        List<GameSessionEntity> gameSessionEntities = gameSessionRepository.getGamesRelatedByUserByStateOrderedByStartTimeDesc(GameStatus.STARTED, userId.getValue());
        

        if(gameSessionEntities == null || (gameSessionEntities != null && gameSessionEntities.size()==0))
            return new ArrayList<GameSession>();

        return gameSessionEntities.stream().map(
            e-> new GameSession(
                    new GameId(e.getId()), 
                    null, 
                    null, 
                    null, 
                    null,
                    e.getPitCount(), 
                    e.getItemsPerPit(), 
                    null, 
                    null, 
                    null, 
                    null, 
                    null, 
                    e.getStartTime(), 
                    null, 
                    null, 
                    0
                )
        )
        .collect(Collectors.toList());
    }

}
