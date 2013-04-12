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
    public DoubleTag(String name, double value) {
        super(name);
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
}
