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

    protected Tag() {
    }
    
    public abstract T getValue();

    public abstract TagType getType();

    public String toString() {
        return String.valueOf(getValue());
    }
}
