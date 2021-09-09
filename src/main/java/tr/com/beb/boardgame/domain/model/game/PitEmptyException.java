package tr.com.beb.boardgame.domain.model.game;

public class PitEmptyException extends Exception {

    public PitEmptyException() {
    }

    public PitEmptyException(String message) {
        super(message);
    }

    public PitEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

}
