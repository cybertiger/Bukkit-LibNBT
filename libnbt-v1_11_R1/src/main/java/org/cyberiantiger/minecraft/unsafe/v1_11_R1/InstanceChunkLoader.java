/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.unsafe.v1_11_R1;

import java.io.IOException;
import net.minecraft.server.v1_11_R1.Chunk;
import net.minecraft.server.v1_11_R1.ChunkRegionLoader;
import net.minecraft.server.v1_11_R1.ExceptionWorldConflict;
import net.minecraft.server.v1_11_R1.IAsyncChunkSaver;
import net.minecraft.server.v1_11_R1.IChunkLoader;

// Safe not to extend ChunkRegionLoader - CB does not cast to ChunkRegionLoader anywhere.

public final class InstanceChunkLoader implements IChunkLoader, IAsyncChunkSaver {
    private final ChunkRegionLoader loadLoader;
    private final ChunkRegionLoader saveLoader;

    public InstanceChunkLoader(ChunkRegionLoader loadLoader, ChunkRegionLoader saveLoader) {
        this.loadLoader = loadLoader;
        this.saveLoader = saveLoader;
    }

    @Override
    public Chunk a(net.minecraft.server.v1_11_R1.World world, int i, int j) throws IOException {
        if (saveLoader.chunkExists(world, i, j)) {
            return saveLoader.a(world, i, j);
        }
        return loadLoader.a(world, i, j);
    }

    @Override
    public void a(net.minecraft.server.v1_11_R1.World world, Chunk chunk) throws IOException, ExceptionWorldConflict {
        saveLoader.a(world, chunk);
    }

    @Override
    public void b(net.minecraft.server.v1_11_R1.World world, Chunk chunk) throws IOException {
        // Assume this is supposed to be some sort of save operation.
        // Can't tell from NMS - empty method.
        saveLoader.b(world, chunk);
    }

    @Override
    public void a() {
        // XXX: Can't guess if this is a save or load operation.
    }

    @Override
    public void b() {
        // XXX: Can't guess if this is a save or load operation.
    }

    @Override
    public boolean c() {
        // Looks like a flush() / sync() method.
        return saveLoader.c();
    }

    @Override
    public boolean a(int x, int z) {
        if (saveLoader.a(x, z)) {
            return true;
        }
        return loadLoader.a(x, z);
    }
    
}
