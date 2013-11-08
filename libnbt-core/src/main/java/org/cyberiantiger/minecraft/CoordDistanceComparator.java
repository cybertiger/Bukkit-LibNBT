/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft;

import java.util.Comparator;

/**
 *
 * @author antony
 */
public class CoordDistanceComparator implements Comparator<Coord> {
    private final Coord base;

    public CoordDistanceComparator(Coord base) {
        this.base = base;
    }

    public int compare(Coord o1, Coord o2) {
        int distance1 = base.distanceSquared(o1);
        int distance2 = base.distanceSquared(o2);
        if (distance1 == distance2) {
            return 0;
        } else if (distance1 < distance2) {
            return -1;
        } else {
            return 1;
        }
    }

}
