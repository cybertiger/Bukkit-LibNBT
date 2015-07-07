/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

/**
 *
 * @author antony
 */
public final class FloatTag extends Tag {
    private final float value;

    public FloatTag(String name, float value) {
        super(name);
        this.value = value;
    }

    @Override
    public Object getValue() {
        return getRawValue();
    }

    public float getRawValue() {
        return value;
    }

    @Override
    public TagType getType() {
        return TagType.FLOAT;
    }

    @Override
    public String toValueString() {
        return "" + getRawValue() + 'f';
    }

    
}
