/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

/**
 *
 * @author antony
 */
public final class LongTag extends Tag<Long> {
    private final long value;

    public LongTag(long value) {
        this.value = value;
    }

    @Override
    public Long getValue() {
        return getRawValue();
    }

    public long getRawValue() {
        return value;
    }

    @Override
    public TagType getType() {
        return TagType.LONG;
    }

    @Override
    public String toString() {
        return "" + getRawValue() + 'l';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (this.value ^ (this.value >>> 32));
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
        final LongTag other = (LongTag) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }
}
