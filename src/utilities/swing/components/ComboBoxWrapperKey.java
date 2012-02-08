/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.swing.components;

import java.io.Serializable;

/**
 * Clase diseñada para envolver entidades y hacerlas visibles en un {@code JComboBox}
 * sobreescribiendo el método {@link Object#toString()}.
 * 
 * @param <T> Entidad a la cual se wrappea.
 * @param <K> Id/Key (Primary Key or Unique) of the entity wrapped.
 * @author FiruzzZ
 */
public class ComboBoxWrapperKey<T, K extends Number> implements Serializable {

    private static final long serialVersionUID = 1L;
    private final T entity;
    private final K id;
    private final String text;

    /**
     * 
     * @param entity to be wrapped
     * @param id (Primary Key or Unique) of the entity wrapped.
     * @param text a front-end description of the object wrapped.
     */
    public ComboBoxWrapperKey(T entity, K id, String text) {
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
        final ComboBoxWrapperKey<T, K> other = (ComboBoxWrapperKey<T, K>) obj;
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
