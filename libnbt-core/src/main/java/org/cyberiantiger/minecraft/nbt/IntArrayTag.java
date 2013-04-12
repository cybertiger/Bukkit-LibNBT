/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

/**
 *
 * @author antony
 */
public final class IntArrayTag extends Tag<int[]> {
    private final int[] value;
    public IntArrayTag(String name, int[] value) {
        super (name);
        this.value = value;
    }

    @Override
    public int[] getValue() {
        return value;
    }

    @Override
    public TagType getType() {
        return TagType.INT_ARRAY;
    }



}
