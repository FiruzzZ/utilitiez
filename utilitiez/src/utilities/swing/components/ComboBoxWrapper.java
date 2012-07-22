package utilities.swing.components;

/**
 * Clase diseñada para envolver entidades y hacerlas visibles en un {@code JComboBox}
 * sobreescribiendo el método {@link Object#toString()}.
 *
 * @param <T> Class to wrap.
 * @author FiruzzZ
 */
public final class ComboBoxWrapper<T> implements Comparable<ComboBoxWrapper<T>> {

    private final T entity;
    private final Integer id;
    private final String text;

    /**
     *
     * @param entity instance of the object to wrap, can be
     * <code>null</code>
     * @param id unique instance identifier of the class (Primary Key or Unique
     * constraint)
     * @param text String used as
     * <code>return</code> in the overrided method {@link #toString()}
     */
    public ComboBoxWrapper(T entity, Integer id, String text) {
        if (id == null) {
            throw new IllegalArgumentException("parameter id can not be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("parameter text can not be null");
        }
        this.entity = entity;
        this.id = id;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public T getEntity() {
        return entity;
    }

    public Integer getId() {
        return id;
    }

    /**
     * Ojo piojo con este, no es un equal standar!
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
        final ComboBoxWrapper<T> other = (ComboBoxWrapper<T>) obj;
        if (this.getEntity() != null && other.getEntity() != null) {
            return this.getEntity().equals(other.getEntity());
        } else if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 777;
        hash = 777 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public int compareTo(ComboBoxWrapper<T> o) {
        return this.getText().compareTo(o.getText());
    }
}
