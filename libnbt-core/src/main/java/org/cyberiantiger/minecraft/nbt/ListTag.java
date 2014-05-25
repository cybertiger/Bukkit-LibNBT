/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

import java.lang.reflect.Array;

/**
 *
 * @author antony
 */
public final class ListTag extends Tag<Tag[]> {
    private final TagType listType;
    private Tag[] value;

    public ListTag(String name, TagType listType, Tag[] value) {
        super(name);
        if (listType == TagType.END) {
            if (value != null) {
                throw new IllegalArgumentException("Lists of TypeType.END must have null for their value");
            }
        } else {
            Class c = value.getClass().getComponentType();
            if (listType.getTagClass() != value.getClass().getComponentType())
                throw new IllegalArgumentException("Tag class " + value.getClass().getComponentType().getName() + " is not " + listType.getTagClass());
        }
        this.listType = listType;
        this.value = value;
    }

    public int size() {
        return value.length;
    }

    public boolean isEmpty() {
        return value.length == 0;
    }

    @Override
    public Tag[] getValue() {
        return value;
    }

    public TagType getListType() {
        return listType;
    }

    @Override
    public TagType getType() {
        return TagType.LIST;
    }

    public void add(Tag t) {
        if (t.getType() != listType) {
            throw new IllegalArgumentException();
        }
        Tag[] newValue = (Tag[]) Array.newInstance(listType.getTagClass(), value.length + 1);
        System.arraycopy(value, 0, newValue, 0, value.length);
        newValue[newValue.length-1] = t;
        this.value = newValue;
    }

    public void remove(int idx) {
        if (idx < 0 || idx >= value.length)
            throw new IllegalArgumentException();
        Tag[] newValue = (Tag[]) Array.newInstance(listType.getTagClass(), value.length - 1);
        System.arraycopy(value, 0, newValue, 0, idx);
        System.arraycopy(value, idx+1, newValue, idx, value.length-1-idx);
        this.value = newValue;
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
        StringBuilder ret = new StringBuilder('[');
        if (value != null) {
            for (int i = 0; i < value.length; i++) {
                ret.append(value[i].toValueString());
                if (i != value.length-1) {
                    ret.append(", ");
                }
            }
        }
        ret.append(']');
        return ret.toString();
    }
}
