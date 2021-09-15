package tr.com.beb.boardgame.domain.model.game;

import org.apache.commons.lang3.StringUtils;

import tr.com.beb.boardgame.domain.common.model.BeanId;

public class GameId extends BeanId<String> {

    public GameId(String id) {
        super(id);
    }

    @Override
    public boolean isValid() {
        return StringUtils.isNotBlank(getValue());
    }
    
}
