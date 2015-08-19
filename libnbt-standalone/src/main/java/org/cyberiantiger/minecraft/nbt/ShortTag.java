/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

/**
 *
 * @author antony
 */
public final class ShortTag extends Tag<Short> {
    private final short value;

    public ShortTag(short value) {
        this.value = value;
    }

    @Override
    public Short getValue() {
        return getRawValue();
    }

    public short getRawValue() {
        return value;
    }

    @Override
    public TagType getType() {
        return TagType.SHORT;
    }

    @Override
    public String toString() {
        return "" + getRawValue() + 's';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.value;
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
        final ShortTag other = (ShortTag) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }
}
