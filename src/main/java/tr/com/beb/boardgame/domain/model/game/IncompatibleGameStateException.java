package tr.com.beb.boardgame.domain.model.game;

public class IncompatibleGameStateException extends Exception {

    public IncompatibleGameStateException() {
    }

    public IncompatibleGameStateException(String message) {
        super(message);
    }

    public IncompatibleGameStateException(String message, Throwable cause) {
        super(message, cause);
    }

}
