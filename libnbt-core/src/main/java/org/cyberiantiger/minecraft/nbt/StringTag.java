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
            if (ch == '"') {
                ret.append("\\\"");
            } else {
                ret.append(ch);
            }
        }
        ret.append('"');
        return ret.toString();
    }
}
