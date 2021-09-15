package tr.com.beb.boardgame.web.payload;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CreateGamePayload {

    @NotNull
    @Min(1)
    private Integer pitCount;

    @NotNull
    @Min(1)
    private Integer itemsPerPit;

    public Integer getPitCount() {
        return pitCount;
    }

    public void setPitCount(Integer pitCount) {
        this.pitCount = pitCount;
    }

    public Integer getItemsPerPit() {
        return itemsPerPit;
    }

    public void setItemsPerPit(Integer itemsPerPit) {
        this.itemsPerPit = itemsPerPit;
    }

}
