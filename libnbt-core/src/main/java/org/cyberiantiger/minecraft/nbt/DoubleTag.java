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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
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
        final DoubleTag other = (DoubleTag) obj;
        if (Double.doubleToLongBits(this.value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        return true;
    }
}
