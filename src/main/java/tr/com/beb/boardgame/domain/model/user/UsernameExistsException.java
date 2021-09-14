package tr.com.beb.boardgame.domain.model.user;

public class UsernameExistsException extends RegistrationException {

    public UsernameExistsException() {
    }

    public UsernameExistsException(String message) {
        super(message);
    }

    public UsernameExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
