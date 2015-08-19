/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

/**
 *
 * @author antony
 */
public final class StringTag extends Tag<String> {
    private final String value;
    public StringTag(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public TagType getType() {
        return TagType.STRING;
    }

    @Override
    public String toString() {
        return StringTag.toString(getValue());
    }

    public static String toString(String s) {
        StringBuilder ret = new StringBuilder(s.length() + 4);
        ret.append('"');
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\b':
                    ret.append("\\b");
                    break;
                case '\f':
                    ret.append("\\f");
                    break;
                case '\n':
                    ret.append("\\n");
                    break;
                case '\r':
                    ret.append("\\r");
                    break;
                case '\t':
                    ret.append("\\t");
                    break;
                case '\\':
                    ret.append("\\\\");
                    break;
                case '"':
                    ret.append("\\\"");
                    break;
                default:
                    ret.append(ch);
            }
        }
        ret.append('"');
        return ret.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.value != null ? this.value.hashCode() : 0);
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
        final StringTag other = (StringTag) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        return true;
    }
}
