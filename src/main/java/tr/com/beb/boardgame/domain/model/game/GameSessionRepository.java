package tr.com.beb.boardgame.domain.model.game;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

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
     * Filter GameSessions by game status
     * 
     * @param gameStatus
     * @return List of game sessions
     */
    List<GameSessionEntity> findByGameStatusOrderByCreatedDateDesc(GameStatus gameStatus);


}
