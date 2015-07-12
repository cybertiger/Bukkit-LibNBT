/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import org.junit.Test;

/**
 *
 * @author antony
 */
public class TagInputStreamTest {

    @Test
    public void testParse() throws IOException {
        TagInputStream tin;
        InputStream in;

        in = TagInputStreamTest.class.getResourceAsStream("level.dat");
        tin = new TagInputStream(new GZIPInputStream(in));
        System.out.println(tin.readTag());

        in = TagInputStreamTest.class.getResourceAsStream("hello_world.nbt");
        tin = new TagInputStream(in);
        System.err.println(tin.readTag());

        in = TagInputStreamTest.class.getResourceAsStream("bigtest.nbt");
        tin = new TagInputStream(new GZIPInputStream(in));
        System.err.println(tin.readTag());
    }
}
