package tr.com.beb.boardgame.domain.model.game;

public class Test {

    public static void main(String[] args) {
        Game game = new Game( 6, 9);
        try {
            game.play(5);
        } catch (PitEmptyException | GameAlreadyFinishedException | InvalidPitIndexException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
