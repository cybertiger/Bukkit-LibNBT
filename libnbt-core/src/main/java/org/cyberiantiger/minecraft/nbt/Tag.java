/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

import java.nio.charset.Charset;

/**
 *
 * @author antony
 */
public abstract class Tag<T> {
    public static final Charset CHARSET = Charset.forName("UTF-8");

    private final String name;

    protected Tag(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public abstract T getValue();

    public abstract TagType getType();

    @Override
    public String toString() {
        return getName() + " : " + toValueString();
    }

    public String toValueString() {
        return String.valueOf(getValue());
    }
}
