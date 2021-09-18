package tr.com.beb.game.domain.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import io.jsonwebtoken.lang.Assert;
import tr.com.beb.boardgame.domain.application.UserService;
import tr.com.beb.boardgame.domain.application.impl.UserServiceImpl;
import tr.com.beb.boardgame.domain.model.user.DefaultUserDetails;
import tr.com.beb.boardgame.domain.model.user.RegistrationException;
import tr.com.beb.boardgame.domain.model.user.User;
import tr.com.beb.boardgame.domain.model.user.UserEntity;
import tr.com.beb.boardgame.domain.model.user.UserId;
import tr.com.beb.boardgame.domain.model.user.UserNotFoundException;
import tr.com.beb.boardgame.domain.model.user.UserRepository;
import tr.com.beb.boardgame.domain.model.user.UsernameExistsException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class UserServiceImplTest {

    private UserService userService;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private PasswordEncoder passwordEncoderMock;

    @Before
    public void setUp() {
        userService = new UserServiceImpl(userRepositoryMock, passwordEncoderMock);
    }

    @Test
    public void loadUserByUsername_emptyUserName_shouldFail() {
        Exception exception = null;
        try {
            userService.loadUserByUsername("");
        } catch (Exception e) {
            exception = e;
        }

        Assert.notNull(exception);
        assertTrue(exception instanceof UsernameNotFoundException);
        verify(userRepositoryMock, never()).findByUsername("");
    }

    @Test
    public void loadUserByUsername_notExistingUserName_shouldFail() {
        String unRegisteredUserName = "Unknown User";

        when(userRepositoryMock.findByUsername(unRegisteredUserName)).thenReturn(Optional.empty());
        Exception exception = null;
        try {
            userService.loadUserByUsername(unRegisteredUserName);
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);
        assertTrue(exception instanceof UsernameNotFoundException);
        verify(userRepositoryMock).findByUsername(unRegisteredUserName);
    }

    @Test
    public void loadUserByUsername_registeredUser_shouldSucceed() {
        String registeredUserName = "Registered User";
        String mockPassword = "mockPassword";
        String mockUserId = "mockUserId";

        UserEntity mockUserEntity = Mockito.mock(UserEntity.class);
        when(mockUserEntity.getId()).thenReturn(mockUserId);
        when(mockUserEntity.getUsername()).thenReturn(registeredUserName);
        when(mockUserEntity.getPassword()).thenReturn(mockPassword);

        when(userRepositoryMock.findByUsername(registeredUserName)).thenReturn(Optional.ofNullable(mockUserEntity));

        Exception exception = null;
        UserDetails userDetails = null;
        try {
            userDetails = userService.loadUserByUsername(registeredUserName);
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
        verify(userRepositoryMock).findByUsername(registeredUserName);
        assertNotNull(userDetails);
        assertEquals(registeredUserName, userDetails.getUsername());
        assertTrue(userDetails instanceof DefaultUserDetails);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findById_nullUserId_shouldFail() throws UserNotFoundException {
        userService.findById(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findById_blankUserId_shouldFail() throws UserNotFoundException {
        userService.findById(new UserId(""));
    }

    @Test
    public void findById_notExistingId_shouldFail() {
        UserId userId = new UserId("mockUserId");

        when(userRepositoryMock.findById(anyString())).thenReturn(Optional.empty());

        Exception exception = null;

        try {
            userService.findById(userId);
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);
        assertTrue(exception instanceof UserNotFoundException);
        verify(userRepositoryMock).findById("mockUserId");
    }

    @Test
    public void findById_exitingUserId_shouldSucceed() {
        UserId userId = new UserId("mockUserId");

        UserEntity mockUserEntity = Mockito.mock(UserEntity.class);
        when(mockUserEntity.getId()).thenReturn("mockUserId");

        when(userRepositoryMock.findById("mockUserId")).thenReturn(Optional.ofNullable(mockUserEntity));
        User user = null;
        Exception exception = null;
        try {
            user = userService.findById(userId);
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
        verify(userRepositoryMock).findById("mockUserId");
        assertNotNull(user);
        assertEquals(user.getUserId().getValue(), userId.getValue());
    }

    @Test
    public void register_ExistingUser_shouldFail(){
        String existingUserName = "Registered User";
        UserEntity userEntityMock = Mockito.mock(UserEntity.class);
        when(userEntityMock.getUsername()).thenReturn(existingUserName);

        when(userRepositoryMock.findByUsername(existingUserName)).thenReturn(Optional.ofNullable(userEntityMock));

        Exception exception = null;
        try {
            userService.register(existingUserName, "password", "name", "surname");
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);
        verify(userRepositoryMock).findByUsername(existingUserName);
        assertTrue(exception instanceof UsernameExistsException);
        verify(userRepositoryMock, never()).save(userEntityMock);
    }

    @Test
    public void register_notExistingUsername_shouldSucceed(){
       
        when(userRepositoryMock.findByUsername(anyString())).thenReturn(Optional.empty());

        UserEntity mockUserEntity = new UserEntity("username", "password", "name", "surname");
        Exception exception = null;
        try {
            userService.register("username", "password", "name", "surname");
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
        verify(userRepositoryMock).save(mockUserEntity);

    }

    @Test(expected = IllegalArgumentException.class)
    public void register_blankUsername_shouldFail() throws RegistrationException{
        userService.register("", "password", "name", "surname");
    }

    @Test(expected = IllegalArgumentException.class)
    public void register_blankPassword_shouldFail() throws RegistrationException{
        userService.register("username", "", "name", "surname");
    }

    @Test(expected = IllegalArgumentException.class)
    public void register_blankName_shouldFail() throws RegistrationException{
        userService.register("username", "password", "", "surname");
    }

    @Test(expected = IllegalArgumentException.class)
    public void register_blankSurname_shouldFail() throws RegistrationException{
        userService.register("username", "password", "name", "");
    }

}
