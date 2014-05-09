/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.nbt;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author antony
 */
public final class CompoundTag extends Tag<Map<String, Tag>> {

    private final Map<String, Tag> value;

    public CompoundTag() {
        this(null);
    }

    public CompoundTag(String name) {
        this(name, new HashMap<String, Tag>());
    }

    public CompoundTag(String name, Map<String, Tag> value) {
        super(name);
        this.value = value;
    }

    public boolean containsKey(String key) {
        return value.containsKey(key);
    }

    public void remove(String string) {
        value.remove(string);
    }

    public byte getByte(String name) {
        ByteTag tag = (ByteTag) value.get(name);
        return tag == null ? 0 : tag.getRawValue();
    }

    public void setByte(String name, byte b) {
        value.put(name, new ByteTag(name, b));
    }

    public byte[] getByteArray(String name) {
        ByteArrayTag tag = (ByteArrayTag) value.get(name);
        return tag == null ? null : tag.getValue();
    }

    public void setByteArray(String name, byte[] b) {
        value.put(name, new ByteArrayTag(name, b));
    }

    public CompoundTag getCompound(String name) {
        return (CompoundTag) value.get(name);
    }

    public void setCompound(String name) {
        setCompound(name, new CompoundTag(name));
    }

    public void setCompound(String name, CompoundTag c) {
        value.put(name, c);
    }

    public double getDouble(String name) {
        DoubleTag tag = (DoubleTag) value.get(name);
        return tag == null ? 0 : tag.getRawValue();
    }

    public void setDouble(String name, double d) {
        value.put(name, new DoubleTag(name, d));
    }

    public float getFloat(String name) {
        FloatTag tag = (FloatTag) value.get(name);
        return tag == null ? 0 : tag.getRawValue();
    }

    public void setFloat(String name, float f) {
        value.put(name, new FloatTag(name, f));
    }

    public int[] getIntArray(String name) {
        IntArrayTag tag = (IntArrayTag) value.get(name);
        return tag == null ? null : tag.getValue();
    }

    public void setIntArray(String name, int[] v) {
        value.put(name, new IntArrayTag(name, v));
    }

    public int getInt(String name) {
        IntTag tag = (IntTag) value.get(name);
        return tag == null ? 0 : tag.getRawValue();
    }

    public void setInt(String name, int v) {
        value.put(name, new IntTag(name, v));
    }

    public ListTag getList(String name) {
        return (ListTag) value.get(name);
    }

    public void setList(String name, ListTag l) {
        value.put(name, l);
    }

    public long getLong(String name) {
        LongTag tag = (LongTag) value.get(name);
        return tag == null ? 0 : tag.getRawValue();
    }

    public void setLong(String name, long l) {
        value.put(name, new LongTag(name, l));
    }

    public short getShort(String name) {
        ShortTag tag = (ShortTag) value.get(name);
        return tag == null ? 0 : tag.getRawValue();
    }

    public void setShort(String name, short s) {
        value.put(name, new ShortTag(name, s));
    }

    public String getString(String name) {
        StringTag tag = (StringTag) value.get(name);
        return tag == null ? null : tag.getValue();
    }

    public void setString(String name, String v) {
        value.put(name, new StringTag(name, v));
    }

    @Override
    public Map<String, Tag> getValue() {
        return value;
    }

    @Override
    public TagType getType() {
        return TagType.COMPOUND;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append(getName());
        ret.append(" : ");
        ret.append(toValueString());
        return ret.toString();
    }

    @Override
    public String toValueString() {
        StringBuilder ret = new StringBuilder('{');
        for (Map.Entry<String, Tag> t : value.entrySet()) {
            ret.append(t.getValue());
            ret.append(", ");
        }
        ret.append('}');
        return ret.toString();
    }
}
