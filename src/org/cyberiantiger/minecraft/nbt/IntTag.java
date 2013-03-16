/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

/**
 *
 * @author antony
 */
public final class IntTag extends Tag<Integer> {

    private final int value;

    public IntTag(String name, int value) {
        super (name);
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return getRawValue();
    }

    public int getRawValue() {
        return value;
    }

    @Override
    public TagType getType() {
        return TagType.INT;
    }

}
