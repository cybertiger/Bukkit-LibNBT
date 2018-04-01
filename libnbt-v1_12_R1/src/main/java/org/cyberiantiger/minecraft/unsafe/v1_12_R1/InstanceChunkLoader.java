/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.unsafe.v1_12_R1;

import java.io.IOException;
import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.ChunkRegionLoader;
import net.minecraft.server.v1_12_R1.ExceptionWorldConflict;
import net.minecraft.server.v1_12_R1.IAsyncChunkSaver;
import net.minecraft.server.v1_12_R1.IChunkLoader;
import org.cyberiantiger.minecraft.unsafe.VersionedNMS;

// Safe not to extend ChunkRegionLoader - CB does not cast to ChunkRegionLoader anywhere.

public final class InstanceChunkLoader implements IChunkLoader, IAsyncChunkSaver, VersionedNMS {
    private final ChunkRegionLoader loadLoader;
    private final ChunkRegionLoader saveLoader;

    public InstanceChunkLoader(ChunkRegionLoader loadLoader, ChunkRegionLoader saveLoader) {
        this.loadLoader = loadLoader;
        this.saveLoader = saveLoader;
    }

    /**
     * Load chunk.
     * @param world World
     * @param i Chunk x coord.
     * @param j Chunk y coord.
     * @return The loaded chunk, or null if it does not exist.
     * @throws IOException When an IOException occurs.
     */
    @Override
    public Chunk a(net.minecraft.server.v1_12_R1.World world, int i, int j) throws IOException {
        if (saveLoader.chunkExists(i, j)) {
            return saveLoader.a(world, i, j);
        }
        return loadLoader.a(world, i, j);
    }

    @Override
    public String getTargetVersion() throws ReflectiveOperationException {
        if (IChunkLoader.class.getMethod("a",
                net.minecraft.server.v1_12_R1.World.class,
                net.minecraft.server.v1_12_R1.Chunk.class) != null) {
            // Versions 1.12, 1.12.1
            return "v1_12_R1";
        } else if (IChunkLoader.class.getMethod("saveChunk",
                net.minecraft.server.v1_12_R1.World.class,
                net.minecraft.server.v1_12_R1.Chunk.class,
                boolean.class) != null) {
            // Spigot 1.12.2
            return "v1_12_R1_1";
        } else if (IChunkLoader.class.getMethod("saveChunk",
                net.minecraft.server.v1_12_R1.World.class,
                net.minecraft.server.v1_12_R1.Chunk.class) != null) {
            // Craftbukkit 1.12.2
            return "v1_12_R1_2";
        } else {
            throw new IllegalStateException("Incompatible NMS version");
        }
    }

    /**
     * Save chunk.
     * @param world World
     * @param chunk Chunk to save.
     * @throws IOException If an error occurs saving the chunk.
     * @throws ExceptionWorldConflict If more than one Minecraft has opened the world.
     */
    @Override
    public void a(net.minecraft.server.v1_12_R1.World world, Chunk chunk) throws IOException, ExceptionWorldConflict {
        saveLoader.a(world, chunk);
    }

    /**
     * Some sort of save operation.
     * @param world World to perform the save for.
     * @param chunk Chunk to save.
     * @throws IOException
     */
    @Override
    public void b(net.minecraft.server.v1_12_R1.World world, Chunk chunk) throws IOException {
        // Assume this is supposed to be some sort of save operation.
        // Can't tell from NMS - empty method.
        saveLoader.b(world, chunk);
    }

    /**
     * This is a no-op in the server version of minecraft, it is assumed to do something useful in the client
     * internal server version.
     */
    @Override
    public void b() {
    }

    /**
     * Sync all in memory data to disk.
     * @return true if all chunks saved successfully, false otherwise.
     */
    @Override
    public boolean a() {
        return saveLoader.a();
    }

    /**
     * Force sync to disk (keeps trying until all chunks are saved or an error occurs).
     */
    @Override
    public void c() {
        saveLoader.c();
    }

    /**
     * Test if a chunk exists in the save file.
     * @param i Chunk x coordinate.
     * @param j Chunk y coordinate.
     * @return true if the chunk exists, false otherwise.
     */
    @Override
    public boolean chunkExists(int i, int j) {
        return loadLoader.chunkExists(i, j) || saveLoader.chunkExists(i, j);
    }
}
