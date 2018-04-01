package org.cyberiantiger.minecraft.unsafe.v1_12_R1;

import net.minecraft.server.v1_12_R1.IChunkLoader;
import org.cyberiantiger.minecraft.unsafe.VersionedNMS;

public class InstanceToolsVersionedNMS implements VersionedNMS {
    @Override
    public String getTargetVersion() {
        try {
            IChunkLoader.class.getMethod("a",
                    net.minecraft.server.v1_12_R1.World.class,
                    net.minecraft.server.v1_12_R1.Chunk.class);
            return "v1_12_R1";
        } catch (ReflectiveOperationException ex) {
            // ignored;
        }
        try {
            IChunkLoader.class.getMethod("saveChunk",
                    net.minecraft.server.v1_12_R1.World.class,
                    net.minecraft.server.v1_12_R1.Chunk.class,
                    boolean.class);
            return "v1_12_R1_1";
        } catch (ReflectiveOperationException ex) {
            // ignored;
        }
        try {
            IChunkLoader.class.getMethod("saveChunk",
                    net.minecraft.server.v1_12_R1.World.class,
                    net.minecraft.server.v1_12_R1.Chunk.class);
            return "v1_12_R1_2";
        } catch (ReflectiveOperationException ex) {
            // ignored;
        }
        throw new IllegalStateException("Incompatible NMS version");
    }

}
