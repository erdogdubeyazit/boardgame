package tr.com.beb.boardgame.domain.common.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

public abstract class AbstractEntity implements Serializable {

    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract boolean equals(Object obj);

    public abstract int hashCode();

}
