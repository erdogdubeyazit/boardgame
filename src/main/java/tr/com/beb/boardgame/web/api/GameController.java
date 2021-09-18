package tr.com.beb.boardgame.web.api;

import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import tr.com.beb.boardgame.domain.application.GameService;
import tr.com.beb.boardgame.domain.common.security.CurrentUser;
import tr.com.beb.boardgame.domain.model.game.GameId;
import tr.com.beb.boardgame.domain.model.game.GameIsNotPlayableException;
import tr.com.beb.boardgame.domain.model.game.GameNotFinishedYetException;
import tr.com.beb.boardgame.domain.model.game.GameSession;
import tr.com.beb.boardgame.domain.model.game.IllegalGameAccessException;
import tr.com.beb.boardgame.domain.model.game.IncompatibleGameStateException;
import tr.com.beb.boardgame.domain.model.game.InvalidPitIndexException;
import tr.com.beb.boardgame.domain.model.game.PitEmptyException;
import tr.com.beb.boardgame.domain.model.user.DefaultUserDetails;
import tr.com.beb.boardgame.web.payload.CreateGamePayload;
import tr.com.beb.boardgame.web.payload.GamePlayPayload;
import tr.com.beb.boardgame.web.results.ApiResult;
import tr.com.beb.boardgame.web.results.GameLobbyResult;
import tr.com.beb.boardgame.web.results.GameSessionResult;
import tr.com.beb.boardgame.web.results.Result;
import tr.com.beb.boardgame.web.socket.updater.GameBoardUpdater;
import tr.com.beb.boardgame.web.socket.updater.GameLobbyUpdater;

@Controller
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private GameService gameService;
    private GameBoardUpdater gameBoardUpdater;
    private GameLobbyUpdater gameLobbyUpdater;

    public GameController(GameService gameService, GameBoardUpdater gameBoardUpdater,
            GameLobbyUpdater gameLobbyUpdater) {
        this.gameService = gameService;
        this.gameBoardUpdater = gameBoardUpdater;
        this.gameLobbyUpdater = gameLobbyUpdater;
    }

    @GetMapping(value = "/api/games")
    public ResponseEntity<ApiResult> getGames(@CurrentUser DefaultUserDetails currentUser) {

        List<GameSession> myGames = gameService.getMyGames(currentUser.getUserId());
        List<GameSession> availableGames = gameService.getAvailableGamesByUser(currentUser.getUserId());
        List<GameSession> activeGames = gameService.getActiveGamesByUser(currentUser.getUserId());

        return GameLobbyResult.build(myGames, availableGames, activeGames);
    }

    @PatchMapping(value = "/api/games/{gameId}/cancel")
    public ResponseEntity<ApiResult> cancelGame(@CurrentUser DefaultUserDetails currentUser,
            @PathVariable(name = "gameId", required = true) String id) {
        try {
            GameSession gameSession = gameService.cancel(new GameId(id), currentUser.getUserId());

            // Notify users
            gameLobbyUpdater.onGameCancelled(gameSession.getGameId(), gameSession);

            logger.info("Game(id: {}) is cancelled by user (id: {})", id, currentUser.getUserId().getValue());
            return Result.ok();
        } catch (NoSuchElementException e) {
            logger.error("Game(id: {}) does not exist. Details : {}", id, e.getMessage());
            return Result.notFound();
        } catch (IncompatibleGameStateException e) {
            logger.error("Game(id: {}) can not be cancelled. Details :{}", id, e.getMessage());
            return Result.failure("Game status is not compatible for canelleation");
        } catch (IllegalGameAccessException e) {
            logger.error("User(id:{}) can not cancel this Game(id: {}) . Details : {}",
                    currentUser.getUserId().getValue(), id, e.getMessage());
            return Result.forbidden();
        }
    }

    @PostMapping(value = "/api/games")
    public ResponseEntity<ApiResult> createGame(@Valid @RequestBody CreateGamePayload payload,
            @CurrentUser DefaultUserDetails currentUser) {
        try {
            GameSession gameSession = gameService.create(currentUser.getUserId(), payload.getPitCount(),
                    payload.getItemsPerPit());
            // Notify users
            gameLobbyUpdater.onGameCreated(gameSession.getGameId(), gameSession);
            return GameSessionResult.build(gameSession);
        } catch (Exception e) {
            logger.error("Game can not be created. Details : {}", e.getMessage());
            return Result.failure("Game can not be created");
        }

    }

    @PatchMapping(value = "/api/games/{gameId}")
    public ResponseEntity<ApiResult> joinGame(@PathVariable(name = "gameId", required = true) String gameId,
            @CurrentUser DefaultUserDetails currentUser) {

        try {
            GameSession gameSession = gameService.join(new GameId(gameId), currentUser.getUserId());

            // Notify users
            gameLobbyUpdater.onGameStarted(gameSession.getGameId(), gameSession);

            return Result.ok();
        } catch (NoSuchElementException e) {
            logger.error("Game(id: {}) does not exist. Details : {}", gameId, e.getMessage());
            return Result.notFound();
        } catch (IncompatibleGameStateException e) {
            logger.error("Unable to join Game(id: {}). Details :{}", gameId, e.getMessage());
            return Result.failure("Game status is not compatible to join");
        } catch (IllegalGameAccessException e) {
            logger.error("User(id:{}) can not join this Game(id: {}) . Details : {}",
                    currentUser.getUserId().getValue(), gameId, e.getMessage());
            return Result.forbidden();
        }
    }

    @GetMapping(value = "/api/games/{gameId}")
    public ResponseEntity<ApiResult> getGameData(@PathVariable(name = "gameId", required = true) String gameId,
            @CurrentUser DefaultUserDetails currentUser) {

        try {
            GameSession gameSession = gameService.loadGame(new GameId(gameId), currentUser.getUserId());
            return GameSessionResult.build(gameSession);
        } catch (NoSuchElementException e) {
            logger.error("Game(id: {}) does not exist. Details : {}", gameId, e.getMessage());
            return Result.notFound();
        } catch (IncompatibleGameStateException e) {
            logger.error("Unable to open Game(id: {}). Details :{}", gameId, e.getMessage());
            return Result.failure("Game status is not compatible to open");
        } catch (IllegalGameAccessException e) {
            logger.error("User(id:{}) can not join this Game(id: {}) . Details : {}",
                    currentUser.getUserId().getValue(), gameId, e.getMessage());
            return Result.forbidden();
        } catch (GameNotFinishedYetException e) {
            logger.error("Game(id: {}) has not finished yet. Details : {}", currentUser.getUserId().getValue(), gameId,
                    e.getMessage());
            return Result.failure("Game has not finished yet");
        }
    }

    @PostMapping(value = "/api/games/{gameId}/play")
    public ResponseEntity<ApiResult> play(@PathVariable(name = "gameId", required = true) String gameId,
            @Valid @RequestBody GamePlayPayload payload, @CurrentUser DefaultUserDetails currentUser) {

        try {
            GameSession gameSession = gameService.play(new GameId(gameId), currentUser.getUserId(),
                    payload.getPitIndex());

            // Notify users
            gameBoardUpdater.onPlayed(new GameId(gameId), gameSession);

            return GameSessionResult.build(gameSession);
        } catch (PitEmptyException e) {
            logger.error("Game(id: {}) can not be played. Because pit is empty. Details : {}", gameId, e.getMessage());
            return Result.failure("Pit is empty");
        } catch (GameIsNotPlayableException e) {
            logger.error("Game(id: {}) is not playable. Details : {}", gameId, e.getMessage());
            return Result.failure("Game is not playable");
        } catch (InvalidPitIndexException e) {
            logger.error("Game(id: {}) can not be played. Because pit index is invalid. Details : {}", gameId,
                    e.getMessage());
            return Result.failure("Pit index is invalid");
        } catch (GameNotFinishedYetException e) {
            logger.error("Game(id: {}) has not finished yet. Details : {}", currentUser.getUserId().getValue(), gameId,
                    e.getMessage());
            return Result.failure("Game has not finished yet");
        } catch (NoSuchElementException e) {
            logger.error("Game(id: {}) can not be found. Details : {}", gameId, e.getMessage());
            return Result.notFound();
        }
    }

    @PatchMapping(value = "/api/games/{gameId}/quit")
    public ResponseEntity<ApiResult> leaveGame(@PathVariable(name = "gameId", required = true) String gameId,
            @CurrentUser DefaultUserDetails currentUser) {
        try {
            gameService.leave(new GameId(gameId), currentUser.getUserId());

            gameBoardUpdater.onPlayerLeft(new GameId(gameId));
            gameLobbyUpdater.onPlayerLeft(new GameId(gameId));
            
            return Result.ok();
        } catch (NoSuchElementException e) {
            logger.error("Game(id: {}) can not be found. Details : {}", gameId, e.getMessage());
            return Result.notFound();
        } catch (IncompatibleGameStateException e) {
            logger.error("Game(id: {}) state is not valid. Details : {}", gameId, e.getMessage());
            return Result.failure("Game state is not valid.");
        } catch (IllegalGameAccessException e) {
            logger.error("User(id:{}) can not access this Game(id: {}) . Details : {}",
                    currentUser.getUserId().getValue(), gameId, e.getMessage());
            return Result.forbidden();
        }
    }

}
