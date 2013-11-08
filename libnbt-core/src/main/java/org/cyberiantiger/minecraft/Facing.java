/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft;

import java.text.DecimalFormat;
import org.bukkit.Location;

/**
 *
 * @author antony
 */
public final class Facing {
    public static final Facing SOUTH = new Facing(0, 0);
    public static final Facing WEST = new Facing(90, 0);
    public static final Facing NORTH = new Facing(180, 0);
    public static final Facing EAST = new Facing (270, 0);

    private final float yaw;
    private final float pitch;

    public Facing(double yaw, double pitch) {
        this((float)yaw, (float)pitch);
    }

    public Facing(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Facing(Location location) {
        this(location.getYaw(), location.getPitch());
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public static Facing fromLocation(Location location) {
        return new Facing(location.getYaw(), location.getPitch());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Float.floatToIntBits(this.yaw);
        hash = 37 * hash + Float.floatToIntBits(this.pitch);
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
        final Facing other = (Facing) obj;
        if (Float.floatToIntBits(this.yaw) != Float.floatToIntBits(other.yaw)) {
            return false;
        }
        if (Float.floatToIntBits(this.pitch) != Float.floatToIntBits(other.pitch)) {
            return false;
        }
        return true;
    }

    private static final DecimalFormat DEGREE_FORMAT = new DecimalFormat("###.0\u00B0");

    @Override
    public String toString() {
        if (NORTH.equals(this)) {
            return "north";
        } else if (EAST.equals(this)) {
            return "east";
        } else if (SOUTH.equals(this)) {
            return "south";
        } else if (WEST.equals(this)) {
            return "west";
        } else {
            return DEGREE_FORMAT.format(yaw) + (pitch < 0 ? " up " + DEGREE_FORMAT.format(-pitch) : " down " + DEGREE_FORMAT.format(pitch));
        }
    }
}
