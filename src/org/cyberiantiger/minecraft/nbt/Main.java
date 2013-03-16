/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.nbt;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author antony
 */
public class Main {
    public static void main(String... args) throws IOException {
        TagInputStream tin;
        InputStream in;

        in = Main.class.getResourceAsStream("level.dat");
        tin = new TagInputStream(new GZIPInputStream(in));
        System.out.println(tin.readTag());

        in = Main.class.getResourceAsStream("hello_world.nbt");
        tin = new TagInputStream(in);
        System.err.println(tin.readTag());

        in = Main.class.getResourceAsStream("bigtest.nbt");
        tin = new TagInputStream(new GZIPInputStream(in));
        System.err.println(tin.readTag());
    }
}
