package tr.com.beb.boardgame.domain.model.game;

public class InvalidPitIndexException extends Exception {

    public InvalidPitIndexException() {
    }

    public InvalidPitIndexException(String message) {
        super(message);
    }

    public InvalidPitIndexException(String message, Throwable cause) {
        super(message, cause);
    }

}
