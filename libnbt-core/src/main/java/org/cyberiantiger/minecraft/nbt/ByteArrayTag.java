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
public final class ByteArrayTag extends Tag<byte[]> {
    private final byte[] value;
    public ByteArrayTag(byte[] value) {
        this.value = value;
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    public TagType getType() {
        return TagType.BYTE_ARRAY;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append('<');
        byte[] v = getValue();
        for (int i = 0; i < v.length; i++) {
            if (i != 0) {
                ret.append(", ");
            }
            ret.append(v[i]&0xff);
        }
        ret.append('>');
        return ret.toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Arrays.hashCode(this.value);
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
        final ByteArrayTag other = (ByteArrayTag) obj;
        if (!Arrays.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }
}