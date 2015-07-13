/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.nbt;

import java.io.IOException;

/**
 *
 * @author antony
 */
public class MojangsonParseException extends IOException {
    public MojangsonParseException(String reason) {
        super(reason);
    }
}
