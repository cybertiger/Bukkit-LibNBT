/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

/**
 *
 * @author antony
 */
public final class FloatTag extends Tag {
    private final float value;

    public FloatTag(float value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return getRawValue();
    }

    public float getRawValue() {
        return value;
    }

    @Override
    public TagType getType() {
        return TagType.FLOAT;
    }

    @Override
    public String toString() {
        return "" + getRawValue() + 'f';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Float.floatToIntBits(this.value);
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
        final FloatTag other = (FloatTag) obj;
        if (Float.floatToIntBits(this.value) != Float.floatToIntBits(other.value)) {
            return false;
        }
        return true;
    }

}
