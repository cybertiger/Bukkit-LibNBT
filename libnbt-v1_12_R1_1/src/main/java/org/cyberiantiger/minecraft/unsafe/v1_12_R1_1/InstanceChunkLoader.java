/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.unsafe.v1_12_R1_1;

import java.io.IOException;

import net.minecraft.server.v1_12_R1.*;

/**
 * Version for Spigot 1.12.2
 */
public final class InstanceChunkLoader implements IChunkLoader, IAsyncChunkSaver {
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
    public Chunk a(World world, int i, int j) throws IOException {
        if (saveLoader.chunkExists(i, j)) {
            return saveLoader.a(world, i, j);
        }
        return loadLoader.a(world, i, j);
    }

    /**
     * Save chunk.
     * @param world World
     * @param chunk Chunk to save.
     * @throws IOException If an error occurs saving the chunk.
     * @throws ExceptionWorldConflict If more than one Minecraft has opened the world.
     */
    @Override
    public void saveChunk(World world, Chunk chunk, boolean unload) throws IOException, ExceptionWorldConflict {
        saveLoader.saveChunk(world, chunk, unload);
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
