package tr.com.beb.boardgame.domain.model.game;

public class IllegalGameAccessException extends Exception {

    public IllegalGameAccessException() {
    }

    public IllegalGameAccessException(String message) {
        super(message);
    }

    public IllegalGameAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
