package tr.com.beb.boardgame.domain.model.user;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * User Repository Interface. Mongo Repository
 */
public interface UserRepository extends MongoRepository<UserEntity, String> {

    /**
     * Find user by username
     * 
     * @param username
     * @return Optional User
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Find user by id
     * 
     * @return Optional user
     */
    Optional<UserEntity> findById(String id);

}
