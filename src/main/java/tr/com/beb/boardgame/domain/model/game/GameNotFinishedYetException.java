package tr.com.beb.boardgame.domain.model.game;

public class GameNotFinishedYetException extends Exception{

    public GameNotFinishedYetException() {
    }

    public GameNotFinishedYetException(String message) {
        super(message);
    }

    public GameNotFinishedYetException(String message, Throwable cause) {
        super(message, cause);
    }

    
    
}
