/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 *
 * @author antony
 */
public final class ListTag extends Tag<Tag[]> {
    private TagType listType;
    private Tag[] value;

    public ListTag(TagType listType, Tag[] value) {
        if (listType == TagType.END) {
            if (value != null) {
                throw new IllegalArgumentException("Lists of TypeType.END must have null for their value");
            }
        } else if (value.length == 0) {
            listType = TagType.END;
            value = null;
        } else {
            Class c = value.getClass().getComponentType();
            if (listType.getTagClass() != value.getClass().getComponentType())
                throw new IllegalArgumentException("Tag class " + value.getClass().getComponentType().getName() + " is not " + listType.getTagClass());
        }
        this.listType = listType;
        this.value = value;
    }

    public int size() {
        return value == null ? 0 : value.length;
    }

    public boolean isEmpty() {
        return value == null || value.length == 0;
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
        if (listType == TagType.END) {
            listType = t.getType();
            value = listType.newArray(1);
            value[0] = t;
        } else {
            if (t.getType() != listType) {
                throw new IllegalArgumentException();
            }
            Tag[] newValue = listType.newArray(value.length + 1);
            System.arraycopy(value, 0, newValue, 0, value.length);
            newValue[newValue.length-1] = t;
            this.value = newValue;
        }
    }

    public void remove(int idx) {
        if (idx < 0 || idx >= value.length)
            throw new IllegalArgumentException();
        if (value.length == 1) {
            listType = TagType.END;
            value = null;
        } else {
            Tag[] newValue = listType.newArray(value.length - 1);
            System.arraycopy(value, 0, newValue, 0, idx);
            System.arraycopy(value, idx+1, newValue, idx, value.length-1-idx);
            this.value = newValue;
        }
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append('[');
        if (value != null) {
            for (int i = 0; i < value.length; i++) {
                if (i != 0) {
                    ret.append(", ");
                }
                ret.append(value[i].toString());
            }
        }
        ret.append(']');
        return ret.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.listType != null ? this.listType.hashCode() : 0);
        hash = 37 * hash + Arrays.deepHashCode(this.value);
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
        final ListTag other = (ListTag) obj;
        if (this.listType != other.listType) {
            return false;
        }
        if (!Arrays.deepEquals(this.value, other.value)) {
            return false;
        }
        return true;
    }
}
