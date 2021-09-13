package tr.com.beb.boardgame.domain.common.model;

public abstract class BeanId<T> {

    private T id;

    public BeanId(T id) {
        this.id = id;
    }

    public T getValue() {
        return id;
    }

    public abstract boolean isValid();

}
