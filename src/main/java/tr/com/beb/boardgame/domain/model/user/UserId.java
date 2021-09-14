package tr.com.beb.boardgame.domain.model.user;

import org.apache.commons.lang3.StringUtils;

import tr.com.beb.boardgame.domain.common.model.BeanId;

public class UserId extends BeanId<String> {

    public UserId(String id) {
        super(id);
    }

    @Override
    public boolean isValid() {
        return StringUtils.isNotBlank(getValue());
    }

}
