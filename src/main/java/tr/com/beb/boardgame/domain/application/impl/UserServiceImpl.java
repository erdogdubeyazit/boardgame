package tr.com.beb.boardgame.domain.application.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.lang.Assert;
import tr.com.beb.boardgame.domain.application.UserService;
import tr.com.beb.boardgame.domain.model.user.DefaultUserDetails;
import tr.com.beb.boardgame.domain.model.user.RegistrationException;
import tr.com.beb.boardgame.domain.model.user.User;
import tr.com.beb.boardgame.domain.model.user.UserEntity;
import tr.com.beb.boardgame.domain.model.user.UserId;
import tr.com.beb.boardgame.domain.model.user.UserNotFoundException;
import tr.com.beb.boardgame.domain.model.user.UserRepository;
import tr.com.beb.boardgame.domain.model.user.UsernameExistsException;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsernameNotFoundException exception = new UsernameNotFoundException(
                String.format("User with name `%s` not found", username));
        if (StringUtils.isEmpty(username))
            throw exception;
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> exception);

        User user = createUserFromUserEntity(userEntity);

        return new DefaultUserDetails(user);
    }

    @Override
    public User findById(UserId userId) throws UserNotFoundException {
        Assert.notNull(userId, "Parameter `userId` must not be null");

        UserEntity userEntity = userRepository.findById(userId.getValue())
                .orElseThrow(() -> new UserNotFoundException("User not found by id:" + userId.getValue()));
        return createUserFromUserEntity(userEntity);
    }

    @Override
    public void register(String username, String password, String name, String surname) throws RegistrationException {
        Assert.hasText(username, "Parameter `username` must not be empty");
        Assert.hasText(password, "Parameter `password` must not be empty");
        Assert.hasText(name, "Parameter `name` must not be empty");
        Assert.hasText(surname, "Parameter `surname` must not be empty");

        Optional<UserEntity> existingUserEntity = userRepository.findByUsername(username);
        if (existingUserEntity.isPresent())
            throw new UsernameExistsException(String.format("User with name `%s` already exists.", username));

        try {
            UserEntity newUserEntity = new UserEntity(username, passwordEncoder.encode(password), name, surname);
            userRepository.save(newUserEntity);
        } catch (Exception e) {
            logger.error(
                    "Registration failed at UserServiceImpl. Details: username:{}, password:{}, name:{}, surname:{}. Error Message : "
                            + username,
                    password, name, surname, e.getMessage());
            throw new RegistrationException("Registration failed", e);
        }

    }

    private User createUserFromUserEntity(UserEntity userEntity) {
        return new User(new UserId(userEntity.getId()), userEntity.getRole(), userEntity.getUsername(),
                userEntity.getPassword(), userEntity.getName(), userEntity.getSurname());
    }

}
