package org.cyberiantiger.minecraft.unsafe;

public interface VersionedNMS {

    /**
     * Get the target version for this version.
     * @return An NMS version string, possibly with a suffix.
     */
    String getTargetVersion();
}
