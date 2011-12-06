package utilities.swing.components;

/**
 * Clase diseñada para envolver entidades y hacerlas visibles en un {@code JComboBox}
 * sobreescribiendo el método {@link Object#toString()}.
 * @param <T> Entidad a la cual se wrappea.
 * @author FiruzzZ
 */
public final class ComboBoxWrapper<T> {

    private final T entity;
    private final Integer id;
    private final String text;

    /**
     * 
     * @param id
     * @param text
     * @param entity 
     */
    public ComboBoxWrapper(T entity, Integer id, String text) {
        if(id == null) {
            throw new IllegalArgumentException("parameter id can not be null");
        }
        if(text == null) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ComboBoxWrapper<T> other = (ComboBoxWrapper<T>) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
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
        return text ;
    }
    

}
