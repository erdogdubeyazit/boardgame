package tr.com.beb.boardgame.domain.model.game;

public class GameAlreadyFinishedException extends Exception {

    public GameAlreadyFinishedException() {
    }

    public GameAlreadyFinishedException(String message) {
        super(message);
    }

    public GameAlreadyFinishedException(String message, Throwable cause) {
        super(message, cause);
    }

}
