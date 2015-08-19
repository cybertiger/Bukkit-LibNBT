/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 *
 * @author antony
 */
public class TagOutputStream extends DataOutputStream {

    public TagOutputStream(OutputStream out) {
        super(out);
    }

    public void writeMCString(String string) throws IOException {
        byte[] data = string.getBytes(Tag.CHARSET);
        if (data.length > 0xffff) throw new IOException("String too long");
        writeShort(data.length);
        write(data);
    }

    public <T extends Tag> void writeTag(Map.Entry<String,T> e) throws IOException {
        String name = e.getKey();
        Tag<T> value = e.getValue();
        TagType type = value.getType();
        writeByte(type.ordinal());
        type.writeName(name, this);
        type.write(value, this);
    }
}
