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
        return getKey() + " : " + getValue();
    }
}
