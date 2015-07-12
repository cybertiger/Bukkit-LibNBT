/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

/**
 *
 * @author antony
 */
public final class ByteTag extends Tag<Byte> {
    private final byte value;

    public ByteTag(byte value) {
        this.value = value;
    }

    @Override
    public Byte getValue() {
        return value;
    }

    public byte getRawValue() {
        return value;
    }

    @Override
    public TagType getType() {
        return TagType.BYTE;
    }

    @Override
    public String toString() {
        return "" + getRawValue() + 'b';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.value;
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
        final ByteTag other = (ByteTag) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }
}