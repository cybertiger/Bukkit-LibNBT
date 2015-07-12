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

    public IntTag(int value) {
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.value;
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
        final IntTag other = (IntTag) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }
}
