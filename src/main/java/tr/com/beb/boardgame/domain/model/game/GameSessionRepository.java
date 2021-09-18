package tr.com.beb.boardgame.domain.model.game;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * GameSessionEntity repository. Mongo Repository
 */
public interface GameSessionRepository extends MongoRepository<GameSessionEntity, String> {

    /**
     * Find game session by id
     * 
     * @return Optional GameSessionEntity
     */
    Optional<GameSessionEntity> findById(String id);

    /**
     * Filter GameSessions created by user and by game status
     * 
     * @param gameStatus
     * @return List of game sessions
     */
    @Query(value = "{$and : [{gameStatus: ?0}, {playerA: ?1}]}", sort = "{createdDate : -1}")
    List<GameSessionEntity> getGamesCreatedByUserAndByStatusOrderByCreatedDateDesc(GameStatus gameStatus,
            String userId);

    /**
     * Filter GameSessions not created by user and by game status
     * 
     * @param gameStatus
     * @return List of game sessions
     */
    @Query(value = "{$and : [{gameStatus: ?0}, {playerA: {$ne : ?1}}]}", sort = "{createdDate : -1}")
    List<GameSessionEntity> getGamesNotCreatedByUserAndByStatusOrderByCreatedDateDesc(GameStatus gameStatus,
            String userId);

    /**
     * Gets games by state and user who has started the game or joined the game
     * 
     * @param gameStatus
     * @param userId
     * @return list of games ordered by startTime DESC
     */
    @Query(value = "{$and : [{gameStatus: ?0}, {$or: [{playerA: ?1},{playerB: ?1}]}]}", sort = "{startTime : -1}")
    List<GameSessionEntity> getGamesRelatedByUserByStateOrderedByStartTimeDesc(GameStatus gameStatus, String userId);

}
