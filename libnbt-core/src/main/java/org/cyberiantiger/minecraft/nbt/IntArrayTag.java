/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

import java.util.Arrays;

/**
 *
 * @author antony
 */
public final class IntArrayTag extends Tag<int[]> {
    private final int[] value;
    public IntArrayTag(int[] value) {
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
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append('«');
        int[] v = getValue();
        for (int i = 0; i < v.length; i++) {
            if (i != 0) {
                ret.append(", ");
            }
            ret.append(v[i]);
        }
        ret.append('»');
        return ret.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Arrays.hashCode(this.value);
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
        final IntArrayTag other = (IntArrayTag) obj;
        if (!Arrays.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

}
