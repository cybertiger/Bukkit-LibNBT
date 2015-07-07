/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

/**
 *
 * @author antony
 */
public final class ByteArrayTag extends Tag<byte[]> {
    private final byte[] value;
    public ByteArrayTag(String name, byte[] value) {
        super(name);
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
    public String toValueString() {
        StringBuilder ret = new StringBuilder();
        ret.append('<');
        byte[] v = getValue();
        for (int i = 0; i < v.length; i++) {
            if (i != 0) {
                ret.append(", ");
            }
            ret.append(v[i]);
        }
        ret.append('>');
        return ret.toString();
    }
}