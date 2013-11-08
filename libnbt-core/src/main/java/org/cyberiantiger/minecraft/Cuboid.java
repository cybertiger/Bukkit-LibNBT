/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author antony
 */
public class Cuboid {

    private final String world;
    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;
    private final int minZ;
    private final int maxZ;

    public Cuboid(Location from, Location to) {
        if (!from.getWorld().equals(to.getWorld())) {
            throw new IllegalArgumentException("Locations must be in the same world");
        }
        world = from.getWorld().getName();
        if (from.getBlockX() < to.getBlockX()) {
            minX = from.getBlockX();
            maxX = to.getBlockX();
        } else {
            minX = to.getBlockX();
            maxX = from.getBlockX();
        }
        if (from.getBlockY() < to.getBlockY()) {
            minY = from.getBlockY();
            maxY = to.getBlockY();
        } else {
            minY = to.getBlockY();
            maxY = from.getBlockY();
        }
        if (from.getBlockZ() < to.getBlockZ()) {
            minZ = from.getBlockZ();
            maxZ = to.getBlockZ();
        } else {
            minZ = to.getBlockZ();
            maxZ = from.getBlockZ();
        }
    }

    public Cuboid(String world, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        this.world = world;
        if (minX <= maxX) {
            this.minX = minX;
            this.maxX = maxX;
        } else {
            this.minX = maxX;
            this.maxX = minX;
        }
        if (minY <= maxY) {
            this.minY = minY;
            this.maxY = maxY;
        } else {
            this.minY = maxY;
            this.maxY = minY;
        }
        if (minZ <= maxZ) {
            this.minZ = minZ;
            this.maxZ = maxZ;
        } else {
            this.minZ = maxZ;
            this.maxZ = minZ;
        }
    }

    public String getWorld() {
        return world;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    private boolean isEmpty(World world, double x, double y, double z) {
        Block b1 = world.getBlockAt((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
        Block b2 = world.getBlockAt((int) Math.floor(x), (int) Math.floor(y) + 1, (int) Math.floor(z));

        return (!b1.getType().isSolid()) && (!b2.getType().isSolid());
    }

    public Location getCenterFloor(World world) {
        double xCenter = (minX + 0.5 + maxX + 0.5) / 2.0;
        double yFloor = minY;
        double zCenter = (minZ + 0.5 + maxZ + 0.5) / 2.0;

        while (!isEmpty(world, xCenter, yFloor, zCenter)) {
            yFloor += 1.0;
        }

        return new Location(world, xCenter, yFloor, zCenter);
    }

    public boolean contains(Location location) {
        return contains(Coord.fromLocation(location)) && location.getWorld().getName().equals(world);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append(world);
        ret.append(" <");
        ret.append(minX);
        ret.append(" - ");
        ret.append(maxX);
        ret.append(", ");
        ret.append(minY);
        ret.append(" - ");
        ret.append(maxY);
        ret.append(", ");
        ret.append(minZ);
        ret.append(" - ");
        ret.append(maxZ);
        ret.append(">");
        return ret.toString();
    }

    public boolean contains(Coord coord) {
        return coord.getX() >= minX && coord.getX() <= maxX
                && coord.getY() >= minY && coord.getY() <= maxY
                && coord.getZ() >= minZ && coord.getZ() <= maxZ;
    }
}
