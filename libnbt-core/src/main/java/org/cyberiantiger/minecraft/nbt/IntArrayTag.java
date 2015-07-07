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

    @Override
    public String toValueString() {
        StringBuilder ret = new StringBuilder();
        ret.append('[');
        int[] v = getValue();
        for (int i = 0; i < v.length; i++) {
            if (i != 0) {
                ret.append(", ");
            }
            ret.append(v[i]);
        }
        ret.append(']');
        return ret.toString();
    }
}
