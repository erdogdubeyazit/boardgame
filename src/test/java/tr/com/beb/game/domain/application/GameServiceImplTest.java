package tr.com.beb.game.domain.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import tr.com.beb.boardgame.domain.application.GameService;
import tr.com.beb.boardgame.domain.application.impl.GameServiceImpl;
import tr.com.beb.boardgame.domain.model.Player;
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
import tr.com.beb.boardgame.domain.model.user.UserId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class GameServiceImplTest {

    private GameService gameService;

    @Mock
    private GameSessionRepository gameSessionRepositoryMock;

    @Before
    public void setUp() {
        gameService = new GameServiceImpl(gameSessionRepositoryMock);
    }

    ///////////////////////////
    /// METHOD:   create    ///
    ///////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void create_nullUserId_shouldFail() {
        gameService.create(null, 1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_pitCountLessThenOrEqualToZero_shouldFail() {
        gameService.create(new UserId("id"), 0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_itemsPerPitThenOrEqualToZero_shouldFail() {
        gameService.create(new UserId("id"), 1, 0);
    }

    /**
     * create function is expected to return a GameSessionEntity instance with exact information
     * Sample : 
     * GameSession(
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
     */
    @Test
    public void create_validCreateCall_shouldSucceedAndReturnCorrectInformation() {

        GameSessionEntity gameSessionEntityMock = mock(GameSessionEntity.class);

        UserId userId = new UserId("playerA");
        int pitCount = 1;
        int itemsPerPit = 2;
        String playerA = "playerA";
        GameStatus gameStatus = GameStatus.CREATED;
        Date date = new Date();

        when(gameSessionEntityMock.getPitCount()).thenReturn(pitCount);
        when(gameSessionEntityMock.getItemsPerPit()).thenReturn(itemsPerPit);
        when(gameSessionEntityMock.getPlayerA()).thenReturn(playerA);
        when(gameSessionEntityMock.getGameStatus()).thenReturn(gameStatus);
        when(gameSessionEntityMock.getCreatedDate()).thenReturn(date);

        when(gameSessionRepositoryMock.save(any(GameSessionEntity.class))).thenReturn(gameSessionEntityMock);

        Exception exception = null;
        GameSession gameSession = null;
        try {
            gameSession = gameService.create(userId, pitCount, itemsPerPit);
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
        assertNotNull(gameSession);

        assertNull(gameSession.getPlayerB());
        assertNull(gameSession.getCurrentPlayer());
        assertEquals(gameSession.getGameStatus(), gameStatus);
        assertEquals(gameSession.getPitCount(), pitCount);
        assertEquals(gameSession.getItemsPerPit(), itemsPerPit);
        assertNull(gameSession.getPlayerAPits());
        assertNull(gameSession.getPlayerBPits());
        assertNull(gameSession.getTankA());
        assertNull(gameSession.getTankB());
        assertEquals(gameSession.getCreatedDate(), date);
        assertNull(gameSession.getStartTime());

        assertNull(gameSession.getEndTime());
        assertNull(gameSession.getWinner());
        assertEquals(gameSession.getTurn(), 0);

    }

    ///////////////////////////
    /// METHOD:   join      ///
    ///////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void join_NullGameId_shouldFail()
            throws NoSuchElementException, IncompatibleGameStateException, IllegalGameAccessException {
        gameService.join(null, new UserId("id"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void join_NullUserId_shouldFail() throws NoSuchElementException, IncompatibleGameStateException, IllegalGameAccessException {
        gameService.join(new GameId("id"), null);
    }

    @Test
    public void join_NotExistingGame_shoulfFail(){

        when(gameSessionRepositoryMock.findById(anyString())).thenReturn(Optional.empty());

        Exception exception = null;
        GameSession gameSession = null;
        try {
            gameSession = gameService.join(new GameId("id"), new UserId("id"));
        } catch (Exception e) {
            exception = e;
        }

        assertNull(gameSession);
        assertNotNull(exception);
        assertTrue(exception instanceof NoSuchElementException);

        verify(gameSessionRepositoryMock).findById("id");
    }

    @Test
    public void join_notCreatedState_shouldFail() {
        GameSessionEntity gameSessionEntityMock = mock(GameSessionEntity.class);

        // Only CREATED game state is accepted. Others should throw an error
        when(gameSessionEntityMock.getGameStatus()).thenReturn(GameStatus.STARTED);
        when(gameSessionRepositoryMock.findById(anyString())).thenReturn(Optional.ofNullable(gameSessionEntityMock));

        Exception exception = null;
        GameSession gameSession = null;

        try {
            gameSession = gameService.join(new GameId("id"), new UserId("id"));
        } catch (Exception e) {
            exception = e;
        }

        assertNull(gameSession);
        assertNotNull(exception);

        verify(gameSessionRepositoryMock, never()).save(gameSessionEntityMock);

        assertTrue(exception instanceof IncompatibleGameStateException);
    }

    @Test
    public void join_emptyGameCreator_sholdFail(){
        GameSessionEntity gameSessionEntityMock = mock(GameSessionEntity.class);

        when(gameSessionEntityMock.getPlayerA()).thenReturn("");
        when(gameSessionEntityMock.getGameStatus()).thenReturn(GameStatus.CREATED);
        when(gameSessionRepositoryMock.findById(anyString())).thenReturn(Optional.ofNullable(gameSessionEntityMock));

        Exception exception = null;
        GameSession gameSession = null;

        try {
            gameSession = gameService.join(new GameId("id"), new UserId("id"));
        } catch (Exception e) {
            exception = e;
        }

        assertNull(gameSession);
        assertNotNull(exception);

        verify(gameSessionRepositoryMock, never()).save(gameSessionEntityMock);

        assertTrue(exception instanceof IncompatibleGameStateException);
    }

    @Test
    public void join_creatorOfTheGame_shouldFail(){
        GameSessionEntity gameSessionEntityMock = mock(GameSessionEntity.class);

        when(gameSessionEntityMock.getGameStatus()).thenReturn(GameStatus.CREATED);
        when(gameSessionEntityMock.getPlayerA()).thenReturn("playerA");
        when(gameSessionRepositoryMock.findById(anyString())).thenReturn(Optional.ofNullable(gameSessionEntityMock));

        Exception exception = null;
        GameSession gameSession = null;

        try {
            gameSession = gameService.join(new GameId("id"), new UserId("playerA"));
        } catch (Exception e) {
            exception = e;
        }

        assertNull(gameSession);
        assertNotNull(exception);

        verify(gameSessionRepositoryMock, never()).save(gameSessionEntityMock);

        assertTrue(exception instanceof IllegalGameAccessException);

    }

    /**
     * create function is expected to return a GameSessionEntity instance with exact information
     * Sample : 
     * GameSession(
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
     */
    @Test
    public void join_validJoinCall_shouldSucceedAndReturnCorrectInformation(){

        String playerA = "playerA";
        String playerB = "playerB";
        String gameId = "gameId";
        
        GameSessionEntity gameSessionEntity = new GameSessionEntity();
        gameSessionEntity.setPlayerA(playerA);
        gameSessionEntity.setPlayerB(playerB);
        gameSessionEntity.setId(gameId);
        gameSessionEntity.setGameStatus(GameStatus.CREATED);

        when(gameSessionRepositoryMock.findById(anyString())).thenReturn(Optional.ofNullable(gameSessionEntity));


        Exception exception = null;
        GameSession gameSession = null;

        try {
            gameSession = gameService.join(new GameId(gameId), new UserId(playerB));
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
        assertNotNull(gameSession);

        verify(gameSessionRepositoryMock).save(gameSessionEntity);

        assertEquals(gameId, new GameId(gameId).getValue());
        assertEquals(playerA, new UserId(playerA).getValue());
        assertEquals(playerB, new UserId(playerB).getValue());
        assertEquals(GameStatus.STARTED, gameSession.getGameStatus());
        assertNull(gameSession.getWinner());
        assertEquals(0, gameSession.getTurn());

    }

    ///////////////////////////
    /// METHOD:   play      ///
    ///////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void play_nullGameId_shouldFail() throws PitEmptyException, GameIsNotPlayableException, InvalidPitIndexException, GameNotFinishedYetException {
        gameService.play(null, new UserId("id"), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void play_nullUserId_shouldFail() throws PitEmptyException, GameIsNotPlayableException, InvalidPitIndexException, GameNotFinishedYetException {
        gameService.play(new GameId("id"), null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void play_negativePitIndex_shouldFail() throws PitEmptyException, GameIsNotPlayableException, InvalidPitIndexException, GameNotFinishedYetException {
        gameService.play(new GameId("id"), new UserId("id"), -1);
    }

    @Test
    public void play_notExistingGame_shouldFail(){

        GameSessionEntity gameSessionEntityMock = mock(GameSessionEntity.class);

        when(gameSessionRepositoryMock.findById(anyString())).thenReturn(Optional.empty());

        Exception exception = null;
        GameSession gameSession = null;

        try {
            gameSession = gameService.play(new GameId("id"), new UserId("id"),0);
        } catch (Exception e) {
            exception = e;
        }

        assertNull(gameSession);
        assertNotNull(exception);

        verify(gameSessionRepositoryMock, never()).save(gameSessionEntityMock);

        assertTrue(exception instanceof NoSuchElementException);

    }

    /**
     * Expected to return a GameSessionEntity instance with exact information
     * Sample :
     * GameSession(
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
     */
    @Test
    public void play_validPlayCall_shouldSucceedAndReturnCorrectInformation(){
        GameSessionEntity gameSessionEntity = new GameSessionEntity();

        gameSessionEntity.setGameStatus(GameStatus.STARTED);
        gameSessionEntity.setPitCount(6);
        gameSessionEntity.setItemsPerPit(6);
        gameSessionEntity.setPlayerA("playerA");
        gameSessionEntity.setPlayerA("playerB");
        gameSessionEntity.setNextPlayer(Player.A);
        gameSessionEntity.setTurns(new ArrayList<>());
        gameSessionEntity.setCreatedDate(new Date());
        gameSessionEntity.setStartTime(new Date());


        when(gameSessionRepositoryMock.findById(anyString())).thenReturn(Optional.ofNullable(gameSessionEntity));
        when(gameSessionRepositoryMock.save(any())).thenReturn(gameSessionEntity);

        Exception exception = null;
        GameSession gameSession = null;

        try {
            gameSession = gameService.play(new GameId("id"), new UserId("playerA"), 0);
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
        assertNotNull(gameSession);

        assertNotNull(gameSession.getPlayerB());
        assertNotNull(gameSession.getCurrentPlayer());
        assertNotNull(gameSession.getGameStatus());
        assertEquals(gameSession.getPitCount(), 6);
        assertEquals(gameSession.getItemsPerPit(), 6);
        assertNotNull(gameSession.getPlayerAPits());
        assertNotNull(gameSession.getPlayerBPits());
        assertNotNull(gameSession.getTankA());
        assertNotNull(gameSession.getTankB());
        assertNotNull(gameSession.getCreatedDate());
        assertNotNull(gameSession.getStartTime());

    }


    ///////////////////////////
    /// METHOD:   cancel    ///
    ///////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void cancel_nullGameId_shouldFail() throws NoSuchElementException, IncompatibleGameStateException, IllegalGameAccessException {
        gameService.cancel(null, new  UserId("id"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancel_UserId_shouldFail() throws NoSuchElementException, IncompatibleGameStateException, IllegalGameAccessException {
        gameService.cancel(new GameId("id"), null);
    }

    @Test
    public void cancel_NotExistingGame_shouldFail(){
        when(gameSessionRepositoryMock.findById(anyString())).thenReturn(Optional.empty());

        Exception exception = null;
        GameSession gameSession = null;
        try {
            gameSession = gameService.cancel(new GameId("id"), new UserId("id"));
        } catch (Exception e) {
            exception = e;
        }

        assertNull(gameSession);
        assertNotNull(exception);
        assertTrue(exception instanceof NoSuchElementException);

        verify(gameSessionRepositoryMock).findById("id");
    }

    @Test
    public void cancel_notCreatedStatus_shouldFail(){
        GameSessionEntity gameSessionEntityMock = mock(GameSessionEntity.class);

        // Only CREATED game state is accepted. Others should throw an error
        when(gameSessionEntityMock.getGameStatus()).thenReturn(GameStatus.STARTED);
        when(gameSessionRepositoryMock.findById(anyString())).thenReturn(Optional.ofNullable(gameSessionEntityMock));

        Exception exception = null;
        GameSession gameSession = null;

        try {
            gameSession = gameService.cancel(new GameId("id"), new UserId("id"));
        } catch (Exception e) {
            exception = e;
        }

        assertNull(gameSession);
        assertNotNull(exception);

        verify(gameSessionRepositoryMock, never()).save(gameSessionEntityMock);

        assertTrue(exception instanceof IncompatibleGameStateException);
    }

    @Test
    public void cancel_notCreatorOfTheGame_shouldFail(){

        GameSessionEntity gameSessionEntity = mock(GameSessionEntity.class);

        when(gameSessionEntity.getPlayerA()).thenReturn("NOT CREATOR OF THIS GAME");
        when(gameSessionEntity.getGameStatus()).thenReturn(GameStatus.CREATED);

        when(gameSessionRepositoryMock.findById(anyString())).thenReturn(Optional.ofNullable(gameSessionEntity));

        Exception exception = null;
        GameSession gameSession = null;

        try {
            gameSession = gameService.cancel(new GameId("id"), new UserId("id"));
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);
        assertTrue(exception instanceof IllegalGameAccessException);

        assertNull(gameSession);

        verify(gameSessionRepositoryMock, never()).save(gameSessionEntity);

    }

    ///////////////////////////
    /// METHOD:   leave     ///
    ///////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void leave_nullGameId_shouldFail() throws NoSuchElementException, IncompatibleGameStateException, IllegalGameAccessException {
        gameService.leave(null, new  UserId("id"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void leave_nullUserId_shouldFail() throws NoSuchElementException, IncompatibleGameStateException, IllegalGameAccessException {
        gameService.leave(new GameId("id"), null);
    }

    @Test
    public void leave_NotExistingGame_shouldFail(){
        when(gameSessionRepositoryMock.findById(anyString())).thenReturn(Optional.empty());

        Exception exception = null;
        try {
            gameService.leave(new GameId("id"), new UserId("id"));
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);
        assertTrue(exception instanceof NoSuchElementException);

        verify(gameSessionRepositoryMock).findById("id");
    }

    @Test
    public void leave_notPlayerOfTheGame_shouldFail(){

        GameSessionEntity gameSessionEntity = mock(GameSessionEntity.class);

        when(gameSessionEntity.getPlayerA()).thenReturn("playerA");
        when(gameSessionEntity.getPlayerA()).thenReturn("playerB");
        when(gameSessionEntity.getGameStatus()).thenReturn(GameStatus.STARTED);

        when(gameSessionRepositoryMock.findById(anyString())).thenReturn(Optional.ofNullable(gameSessionEntity));

        Exception exception = null;

        try {
            gameService.leave(new GameId("id"), new UserId("NOT PLAYER OF THE GAME"));
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);

        verify(gameSessionRepositoryMock, never()).save(gameSessionEntity);

    }

    @Test
    public void leave_notStartedStatus_shouldFail(){
        GameSessionEntity gameSessionEntityMock = mock(GameSessionEntity.class);

        // Only STARTED game state is accepted. Others should throw an error
        when(gameSessionEntityMock.getGameStatus()).thenReturn(GameStatus.COMPLETED);
        when(gameSessionRepositoryMock.findById(anyString())).thenReturn(Optional.ofNullable(gameSessionEntityMock));

        Exception exception = null;

        try {
            gameService.leave(new GameId("id"), new UserId("id"));
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);

        verify(gameSessionRepositoryMock, never()).save(gameSessionEntityMock);

        assertTrue(exception instanceof IncompatibleGameStateException);
    }

    /////////////////////////////
    /// METHOD:   getMyGames  ///
    /////////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void getMyGames_nullGameId_shouldFail() throws NoSuchElementException, IncompatibleGameStateException, IllegalGameAccessException {
        gameService.getMyGames(null);
    }

    /**
     * DataBase interaction already tested in GameSessionRepositoryTests
     * 
     * Expected to return a GameSessionEntity instance with exact information
     * List of sample :
     * GameSession(
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
     */
    @Test
    public void  getMyGames_validCall_shouldSucceedAndReturnCorrectInformation(){

        GameSessionEntity gameSessionEntity = mock(GameSessionEntity.class);

        String gameId = "gameId";
        String playerA = "playerA";
        GameStatus gameStatus = GameStatus.CREATED;
        int pitCount = 6;
        int itemsPerPit = 6;
        Date date = new Date();

        when(gameSessionEntity.getId()).thenReturn(gameId);
        when(gameSessionEntity.getPlayerA()).thenReturn(playerA);
        when(gameSessionEntity.getGameStatus()).thenReturn(gameStatus);
        when(gameSessionEntity.getPitCount()).thenReturn(pitCount);
        when(gameSessionEntity.getItemsPerPit()).thenReturn(itemsPerPit);
        when(gameSessionEntity.getCreatedDate()).thenReturn(date);
        when(gameSessionEntity.getStartTime()).thenReturn(date);
        when(gameSessionEntity.getEndTime()).thenReturn(date);

        List<GameSessionEntity> gameSessionEntities = Collections.singletonList(gameSessionEntity);

        when(gameSessionRepositoryMock.getGamesCreatedByUserAndByStatusOrderByCreatedDateDesc(GameStatus.CREATED, playerA)).thenReturn(gameSessionEntities);

        Exception exception = null;
        List<GameSession> gameSessionList = null;

        try {
            gameSessionList = gameService.getMyGames(new UserId(playerA));
        } catch (Exception e) {
            exception = e;
        }

        verify(gameSessionRepositoryMock).getGamesCreatedByUserAndByStatusOrderByCreatedDateDesc(gameStatus, playerA);

        assertNull(exception);
        assertNotNull(gameSessionList);

        GameSession gameSessionUnderTest = gameSessionList.get(0);

        assertEquals(gameId, gameSessionUnderTest.getGameId().getValue());
        assertEquals(playerA, gameSessionUnderTest.getPlayerA().getValue());
        assertEquals(GameStatus.CREATED, gameSessionUnderTest.getGameStatus());
        assertEquals(pitCount, gameSessionUnderTest.getPitCount());
        assertEquals(itemsPerPit, gameSessionUnderTest.getItemsPerPit());
        assertEquals(date, gameSessionUnderTest.getCreatedDate());
        assertEquals(date, gameSessionUnderTest.getStartTime());
        assertEquals(date, gameSessionUnderTest.getEndTime());

    }

    ////////////////////////////////////
    /// METHOD:   getAvailableGames  ///
    ////////////////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void getAvailableGames_nullGameId_shouldFail() throws NoSuchElementException, IncompatibleGameStateException, IllegalGameAccessException {
        gameService.getMyGames(null);
    }

    /**
     * DataBase interaction already tested in GameSessionRepositoryTests
     * 
     * Expected to return a GameSessionEntity instance with exact information
     * List of sample :
     * GameSession(
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
     */
    @Test
    public void  getAvailableGames_validCall_shouldSucceedAndReturnCorrectInformation(){

        GameSessionEntity gameSessionEntity = mock(GameSessionEntity.class);

        String gameId = "gameId";
        String playerA = "playerA";
        GameStatus gameStatus = GameStatus.CREATED;
        int pitCount = 6;
        int itemsPerPit = 6;
        Date date = new Date();

        when(gameSessionEntity.getId()).thenReturn(gameId);
        when(gameSessionEntity.getPlayerA()).thenReturn(playerA);
        when(gameSessionEntity.getGameStatus()).thenReturn(gameStatus);
        when(gameSessionEntity.getPitCount()).thenReturn(pitCount);
        when(gameSessionEntity.getItemsPerPit()).thenReturn(itemsPerPit);
        when(gameSessionEntity.getCreatedDate()).thenReturn(date);
        when(gameSessionEntity.getStartTime()).thenReturn(date);
        when(gameSessionEntity.getEndTime()).thenReturn(date);

        List<GameSessionEntity> gameSessionEntities = Collections.singletonList(gameSessionEntity);

        when(gameSessionRepositoryMock.getGamesNotCreatedByUserAndByStatusOrderByCreatedDateDesc(GameStatus.CREATED, playerA)).thenReturn(gameSessionEntities);

        Exception exception = null;
        List<GameSession> gameSessionList = null;

        try {
            gameSessionList = gameService.getAvailableGamesByUser(new UserId(playerA));
        } catch (Exception e) {
            exception = e;
        }

        verify(gameSessionRepositoryMock).getGamesNotCreatedByUserAndByStatusOrderByCreatedDateDesc(gameStatus, playerA);

        assertNull(exception);
        assertNotNull(gameSessionList);

        GameSession gameSessionUnderTest = gameSessionList.get(0);

        assertEquals(gameId, gameSessionUnderTest.getGameId().getValue());
        assertEquals(playerA, gameSessionUnderTest.getPlayerA().getValue());
        assertEquals(GameStatus.CREATED, gameSessionUnderTest.getGameStatus());
        assertEquals(pitCount, gameSessionUnderTest.getPitCount());
        assertEquals(itemsPerPit, gameSessionUnderTest.getItemsPerPit());
        assertEquals(date, gameSessionUnderTest.getCreatedDate());
        assertEquals(date, gameSessionUnderTest.getStartTime());
        assertEquals(date, gameSessionUnderTest.getEndTime());

    }

    ////////////////////////////////////
    /// METHOD:   getActiveGames  ///
    ////////////////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void getActiveGames_nullUserId_shouldFail() {
        gameService.getActiveGamesByUser(null);
    }

    /**
     * DataBase interaction already tested in GameSessionRepositoryTests
     * 
     * Expected to return a GameSessionEntity instance with exact information
     * List of sample :
     * GameSession(
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
     */
    @Test
    public void  getActiveGames_validCall_shouldSucceedAndReturnCorrectInformation(){

        GameSessionEntity gameSessionEntity = mock(GameSessionEntity.class);

        String gameId = "gameId";
        String playerA = "playerA";
        int pitCount = 6;
        int itemsPerPit = 6;
        Date date = new Date();

        when(gameSessionEntity.getId()).thenReturn(gameId);
        when(gameSessionEntity.getPitCount()).thenReturn(pitCount);
        when(gameSessionEntity.getItemsPerPit()).thenReturn(itemsPerPit);
        when(gameSessionEntity.getStartTime()).thenReturn(date);

        List<GameSessionEntity> gameSessionEntities = Collections.singletonList(gameSessionEntity);

        when(gameSessionRepositoryMock.getGamesRelatedByUserByStateOrderedByStartTimeDesc(GameStatus.STARTED, playerA)).thenReturn(gameSessionEntities);

        Exception exception = null;
        List<GameSession> gameSessionList = null;

        try {
            gameSessionList = gameService.getActiveGamesByUser(new UserId(playerA));
        } catch (Exception e) {
            exception = e;
        }

        verify(gameSessionRepositoryMock).getGamesRelatedByUserByStateOrderedByStartTimeDesc(GameStatus.STARTED, playerA);

        assertNull(exception);
        assertNotNull(gameSessionList);

        GameSession gameSessionUnderTest = gameSessionList.get(0);

        assertEquals(gameId, gameSessionUnderTest.getGameId().getValue());
        assertEquals(pitCount, gameSessionUnderTest.getPitCount());
        assertEquals(itemsPerPit, gameSessionUnderTest.getItemsPerPit());
        assertEquals(date, gameSessionUnderTest.getStartTime());

    }

    ///////////////////////////
    /// METHOD:   loadGame  ///
    ///////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void loadGame_nullGame_shouldFail() throws NoSuchElementException, IncompatibleGameStateException, IllegalGameAccessException, GameNotFinishedYetException {
        gameService.loadGame(null, new UserId("id"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void loadGame_nullUserId_shouldFail() throws NoSuchElementException, IncompatibleGameStateException, IllegalGameAccessException, GameNotFinishedYetException {
        gameService.loadGame(new GameId("id") ,null);
    }

    @Test
    public void loadGame_NotExistingGame_shoulfFail(){

        when(gameSessionRepositoryMock.findById(anyString())).thenReturn(Optional.empty());

        Exception exception = null;
        GameSession gameSession = null;
        try {
            gameSession = gameService.loadGame(new GameId("id"), new UserId("id"));
        } catch (Exception e) {
            exception = e;
        }

        assertNull(gameSession);
        assertNotNull(exception);
        assertTrue(exception instanceof NoSuchElementException);

        verify(gameSessionRepositoryMock).findById("id");
    }

    @Test
    public void loadGame_notPlayerOfTheGame_shouldFail(){

        GameSessionEntity gameSessionEntity = mock(GameSessionEntity.class);

        when(gameSessionEntity.getPlayerA()).thenReturn("playerA");
        when(gameSessionEntity.getPlayerA()).thenReturn("playerB");

        when(gameSessionRepositoryMock.findById(anyString())).thenReturn(Optional.ofNullable(gameSessionEntity));

        Exception exception = null;

        try {
            gameService.leave(new GameId("id"), new UserId("NOT PLAYER OF THE GAME"));
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);

        verify(gameSessionRepositoryMock, never()).save(gameSessionEntity);

    }

}
