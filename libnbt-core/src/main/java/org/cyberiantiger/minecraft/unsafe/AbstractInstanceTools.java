/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.unsafe;

import java.io.File;

/**
 *
 * @author antony
 */
public abstract class AbstractInstanceTools implements InstanceTools {

    protected boolean isParent(File parent, File child) {
        if (child == null) {
            return false;
        } else if (child == parent) {
            return true;
        } else {
            return isParent(parent, child.getParentFile());
        }
    }

    protected void checkDirectories(File source, File destination) {
        if (!source.exists()) {
            throw new IllegalArgumentException("Source directory " + source + " does not exist.");
        }
        if (!source.isDirectory()) {
            throw new IllegalArgumentException("Source directory " + source + " is not a directory.");
        }
        if (!source.canRead()) {
            throw new IllegalArgumentException("Source directory " + source + " cannot be read.");
        }
        if (!destination.exists()) {
            throw new IllegalArgumentException("Destination directory " + destination + " does not exist.");
        }
        if (!destination.isDirectory()) {
            throw new IllegalArgumentException("Destination directory " + destination + " is not a directory.");
        }
        if (!destination.canRead()) {
            throw new IllegalArgumentException("Destination directory " + destination + " cannot be read.");
        }
        if (!destination.canWrite()) {
            throw new IllegalArgumentException("Destination directory " + destination + " cannot be written.");
        }
    }
    
}
