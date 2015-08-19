/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.nbt;

import java.util.Map;

/**
 *
 * @author antony
 */
public final class TagTuple<T extends Tag> implements Map.Entry<String, T> {
    private final String name;
    private final T tag;
    
    public TagTuple(String name, T tag) {
        this.name = name;
        this.tag = tag;
    }

    @Override
    public String getKey() {
        return name;
    }

    @Override
    public T getValue() {
        return tag;
    }

    @Override
    public T setValue(T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return StringTag.toString(getKey()) + " : " + getValue();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 37 * hash + (this.tag != null ? this.tag.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TagTuple<T> other = (TagTuple<T>) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.tag != other.tag && (this.tag == null || !this.tag.equals(other.tag))) {
            return false;
        }
        return true;
    }
}
