package tr.com.beb.boardgame.web.payload;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class GamePlayPayload {

    @NotNull
    @Min(0)
    private Integer pitIndex;

    public Integer getPitIndex() {
        return pitIndex;
    }

    public void setPitIndex(Integer pitIndex) {
        this.pitIndex = pitIndex;
    }

}
