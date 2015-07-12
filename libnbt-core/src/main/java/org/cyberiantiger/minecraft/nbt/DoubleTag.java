/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

/**
 *
 * @author antony
 */
public final class DoubleTag extends Tag {
    private final double value;
    public DoubleTag(double value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return getRawValue();
    }

    public double getRawValue() {
        return value;
    }

    @Override
    public TagType getType() {
        return TagType.DOUBLE;
    }

    @Override
    public String toString() {
        return "" + getRawValue() + 'd';
    }

    
}
