package tr.com.beb.boardgame.domain.model;

public enum Player {

    A(0), B(1);

    private final Integer code;

    Player(int code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
