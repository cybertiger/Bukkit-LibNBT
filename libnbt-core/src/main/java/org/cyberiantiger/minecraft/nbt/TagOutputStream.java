/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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

    public <T extends Tag> void writeTag(T tag) throws IOException {
        if (tag == null) {
            writeByte(TagType.END.ordinal());
        } else {
            TagType type = tag.getType();
            writeByte(type.ordinal());
            type.writeName(tag.getName(), this);
            type.write(tag, this);
        }
    }
}
