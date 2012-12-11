package utilities.general;

import java.io.Serializable;

/**
 *
 * @param <T> Entity to be wrapped
 * @param <K> Id/Key (Primary Key or Unique) of the entity wrapped.
 * @author FiruzzZ
 * @version 1
 * @since loooooong time ago
 */
public class EntityWrapper<T, K> implements Serializable {

    private static final long serialVersionUID = 1111111111111111119L;
    private final T entity;
    private final K id;
    private final String text;

    /**
     *
     * @param entity to be wrapped
     * @param id (Primary Key or Unique) of the entity wrapped.
     * @param text a front-end description of the object wrapped.
     */
    public EntityWrapper(T entity, K id, String text) {
        if (id == null) {
            throw new IllegalArgumentException("parameter id can not be null");
        }
        if (text == null) {
            throw new IllegalArgumentException(text);
        }
        this.entity = entity;
        this.id = id;
        this.text = text;
    }

    public T getEntity() {
        return entity;
    }

    public K getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final EntityWrapper<T, K> other = (EntityWrapper<T, K>) obj;
        if (this.entity != other.entity && (this.entity == null || !this.entity.equals(other.entity))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.entity != null ? this.entity.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return text;
    }
}
