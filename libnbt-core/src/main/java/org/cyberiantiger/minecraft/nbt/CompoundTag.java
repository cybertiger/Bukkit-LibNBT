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
        this(new HashMap<String, Tag>());
    }

    public CompoundTag(Map<String, Tag> value) {
        this.value = value;
    }

    public boolean containsKey(String key) {
        return value.containsKey(key);
    }

    public boolean containsKey(String key, TagType type) {
        Tag t = value.get(key);
        return t == null ? false : t.getType() == type;
    }

    public void remove(String string) {
        value.remove(string);
    }

    public byte getByte(String name) {
        ByteTag tag = (ByteTag) value.get(name);
        return tag == null ? 0 : tag.getRawValue();
    }

    public void setByte(String name, byte b) {
        value.put(name, new ByteTag(b));
    }

    public byte[] getByteArray(String name) {
        ByteArrayTag tag = (ByteArrayTag) value.get(name);
        return tag == null ? null : tag.getValue();
    }

    public void setByteArray(String name, byte[] b) {
        value.put(name, new ByteArrayTag(b));
    }

    public CompoundTag getCompound(String name) {
        return (CompoundTag) value.get(name);
    }

    public void setCompound(String name) {
        setCompound(name, new CompoundTag());
    }

    public void setCompound(String name, CompoundTag c) {
        value.put(name, c);
    }

    public double getDouble(String name) {
        DoubleTag tag = (DoubleTag) value.get(name);
        return tag == null ? 0 : tag.getRawValue();
    }

    public void setDouble(String name, double d) {
        value.put(name, new DoubleTag(d));
    }

    public float getFloat(String name) {
        FloatTag tag = (FloatTag) value.get(name);
        return tag == null ? 0 : tag.getRawValue();
    }

    public void setFloat(String name, float f) {
        value.put(name, new FloatTag(f));
    }

    public int[] getIntArray(String name) {
        IntArrayTag tag = (IntArrayTag) value.get(name);
        return tag == null ? null : tag.getValue();
    }

    public void setIntArray(String name, int[] v) {
        value.put(name, new IntArrayTag(v));
    }

    public int getInt(String name) {
        IntTag tag = (IntTag) value.get(name);
        return tag == null ? 0 : tag.getRawValue();
    }

    public void setInt(String name, int v) {
        value.put(name, new IntTag(v));
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
        value.put(name, new LongTag(l));
    }

    public short getShort(String name) {
        ShortTag tag = (ShortTag) value.get(name);
        return tag == null ? 0 : tag.getRawValue();
    }

    public void setShort(String name, short s) {
        value.put(name, new ShortTag(s));
    }

    public String getString(String name) {
        StringTag tag = (StringTag) value.get(name);
        return tag == null ? null : tag.getValue();
    }

    public void setString(String name, String v) {
        value.put(name, new StringTag(v));
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
        ret.append('{');
        boolean first = true;
        for (Map.Entry<String, Tag> t : value.entrySet()) {
            if (first) {
                first = false;
            } else {
                ret.append(", ");
            }
            ret.append(StringTag.toString(t.getKey()));
            ret.append(" : ");
            ret.append(t.getValue());
        }
        ret.append('}');
        return ret.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (this.value != null ? this.value.hashCode() : 0);
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
        final CompoundTag other = (CompoundTag) obj;
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        return true;
    }
}
