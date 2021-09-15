package tr.com.beb.boardgame.domain.application;

import org.springframework.security.core.userdetails.UserDetailsService;

import tr.com.beb.boardgame.domain.model.user.RegistrationException;
import tr.com.beb.boardgame.domain.model.user.User;
import tr.com.beb.boardgame.domain.model.user.UserId;
import tr.com.beb.boardgame.domain.model.user.UserNotFoundException;

/**
 * User service extending UserDetailsService
 */
public interface UserService extends UserDetailsService {

    User findById(UserId userId) throws UserNotFoundException;

    void register(String username, String password, String name, String surname) throws RegistrationException;

}
