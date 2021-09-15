package tr.com.beb.boardgame.domain.model.game;

public class GameIsNotPlayableException extends Exception {

    public GameIsNotPlayableException() {
    }

    public GameIsNotPlayableException(String message) {
        super(message);
    }

    public GameIsNotPlayableException(String message, Throwable cause) {
        super(message, cause);
    }

}
