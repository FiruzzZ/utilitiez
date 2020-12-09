package utilities.general;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @param <T> Entity to be wrapped
 * @author FiruzzZ
 * @version 1.5
 * @since loooooong time ago
 */
public class EntityWrapper<T> {

    private static final long serialVersionUID = 1111111111111111119L;
    private final T entity;
    private final Serializable id;
    private final String text;

    /**
     *
     * @param entity to be wrapped, can be null.
     * @param id (Primary Key or Unique) of the entity wrapped.
     * @param text a front-end description of the object wrapped.
     */
    public EntityWrapper(T entity, Serializable id, String text) {
        this.entity = entity;
        this.id = Objects.requireNonNull(id, "parameter id can not be null");
        this.text = Objects.requireNonNull(text, "parameter text can not be null");
    }

    public T getEntity() {
        return entity;
    }

    public Serializable getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * Ojo piojo con este, no es un equal standar!
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final EntityWrapper<T> other = (EntityWrapper<T>) obj;
        if (this.getEntity() != null && other.getEntity() != null) {
            return this.getEntity().equals(other.getEntity());
        } else if (!this.id.equals(other.id)) {
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
