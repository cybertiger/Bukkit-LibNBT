/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.unsafe.v1_4_R1;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import net.minecraft.server.v1_4_R1.Chunk;
import net.minecraft.server.v1_4_R1.ChunkRegionLoader;
import net.minecraft.server.v1_4_R1.EntityTracker;
import net.minecraft.server.v1_4_R1.IAsyncChunkSaver;
import net.minecraft.server.v1_4_R1.IChunkLoader;
import net.minecraft.server.v1_4_R1.IDataManager;
import net.minecraft.server.v1_4_R1.IWorldAccess;
import net.minecraft.server.v1_4_R1.MethodProfiler;
import net.minecraft.server.v1_4_R1.MinecraftServer;
import net.minecraft.server.v1_4_R1.NBTTagCompound;
import net.minecraft.server.v1_4_R1.WorldData;
import net.minecraft.server.v1_4_R1.WorldManager;
import net.minecraft.server.v1_4_R1.WorldProvider;
import net.minecraft.server.v1_4_R1.WorldServer;
import net.minecraft.server.v1_4_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_4_R1.RegionFile;
import net.minecraft.server.v1_4_R1.RegionFileCache;
import net.minecraft.server.v1_4_R1.ServerNBTManager;
import net.minecraft.server.v1_4_R1.WorldProviderHell;
import net.minecraft.server.v1_4_R1.WorldProviderTheEnd;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_4_R1.CraftServer;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.cyberiantiger.minecraft.Coord;
import org.cyberiantiger.minecraft.generator.VoidGenerator;
import org.cyberiantiger.minecraft.unsafe.AbstractInstanceTools;

/**
 *
 * @author antony
 */
public final class InstanceTools extends AbstractInstanceTools {

    public void unloadWorld(Plugin plugin, World world) {
        try {
            plugin.getServer().unloadWorld(world, false);
            File folder = world.getWorldFolder();
            Field field = RegionFileCache.class.getDeclaredField("a");
            field.setAccessible(true);
            Map<File,RegionFile> fileCache = (Map<File,RegionFile>) field.get(null);
            Iterator<Map.Entry<File,RegionFile>> i = fileCache.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<File,RegionFile> e = i.next();
                if (isParent(folder, e.getKey())) {
                    i.remove();
                    try {
                        e.getValue().c();
                    } catch (IOException ex) {
                        plugin.getLogger().log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (NoSuchFieldException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public org.bukkit.World createInstance(final Plugin plugin, String instanceName, World.Environment env, Difficulty difficulty, File source, File destination) {
        checkDirectories(source, destination);

        MinecraftServer console = ((CraftServer) plugin.getServer()).getServer();
        if (console == null) {
            throw new IllegalStateException("Minecraft console was null");
        }

        IDataManager dataManager =
                new InstanceDataManager(plugin, instanceName, source, destination);

        // XXX: Copy paste from craftbukkit.
        int dimension = 10 + console.worlds.size();

        boolean used = false;
        do {
            for (WorldServer server : console.worlds) {
                used = server.dimension == dimension;
                if (used) {
                    dimension++;
                    break;
                }
            }
        } while (used);

        MethodProfiler profiler = console.methodProfiler;

        WorldData wd = dataManager.getWorldData();

        ChunkGenerator generator = new VoidGenerator(Biome.PLAINS, new Coord(wd.c(),wd.d(),wd.e()));

        WorldServer instanceWorld = new WorldServer(console, dataManager, instanceName, dimension, null, profiler, env, generator);

        instanceWorld.worldMaps = console.worlds.get(0).worldMaps;
        instanceWorld.tracker = new EntityTracker(instanceWorld);
        instanceWorld.addIWorldAccess((IWorldAccess) new WorldManager(console, instanceWorld));
        instanceWorld.difficulty = difficulty.getValue();
        instanceWorld.keepSpawnInMemory = false;
        console.worlds.add(instanceWorld);

        instanceWorld.getWorld().getPopulators().addAll(generator.getDefaultPopulators(instanceWorld.getWorld()));

        plugin.getServer().getPluginManager().callEvent(new WorldInitEvent(instanceWorld.getWorld()));
        plugin.getServer().getPluginManager().callEvent(new WorldLoadEvent(instanceWorld.getWorld()));

        return instanceWorld.getWorld();
    }

    // Have to extend WorldNBTStorage - CB casts IDataManager to it in World.getWorldFolder()
    // cannot just implement IDataManager, PlayerFileData
    private static class InstanceDataManager extends ServerNBTManager {
        private static final String WORLD_DATA = "level.dat";
        private static final String WORLD_DATA_OLD = "level.dat_old";

        private final Plugin instances;
        private final File loadDataFolder;
        private final String world;

        public InstanceDataManager(Plugin instances, String worldName, File loadDataFolder, File saveDataFolder) {
            // false flag - do not create players directory.
            super(saveDataFolder.getParentFile(), saveDataFolder.getName(), false);
            this.instances = instances;
            this.loadDataFolder = loadDataFolder;
            this.world = worldName;
        }

        @Override
        public WorldData getWorldData() {
            File levelData = new File(getDirectory(), WORLD_DATA);
            if (levelData.isFile()) {
                return super.getWorldData();
            }
            levelData = new File(getDirectory(), WORLD_DATA_OLD);
            if (levelData.isFile()) {
                return super.getWorldData();
            }
            
            File file1 = new File(loadDataFolder, WORLD_DATA);
            NBTTagCompound nbttagcompound;
            NBTTagCompound nbttagcompound1;
            
            if (file1.exists()) {
                try {
                    nbttagcompound = NBTCompressedStreamTools.a((InputStream) (new FileInputStream(file1)));
                    nbttagcompound1 = nbttagcompound.getCompound("Data");
                    return new WorldData(nbttagcompound1);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            
            file1 = new File(loadDataFolder, WORLD_DATA_OLD);
            if (file1.exists()) {
                try {
                    nbttagcompound = NBTCompressedStreamTools.a((InputStream) (new FileInputStream(file1)));
                    nbttagcompound1 = nbttagcompound.getCompound("Data");
                    return new WorldData(nbttagcompound1);
                } catch (Exception exception1) {
                    exception1.printStackTrace();
                }
            }
            
            return null;
        }

        @Override
        public IChunkLoader createChunkLoader(WorldProvider wp) {
            File loadChunkDir;
            File saveChunkDir;
            if (wp instanceof WorldProviderHell) {
                loadChunkDir = new File(loadDataFolder, "DIM-1");
                saveChunkDir = new File(getDirectory(), "DIM-1");
            } else if (wp instanceof WorldProviderTheEnd) {
                loadChunkDir = new File(loadDataFolder, "DIM1");
                saveChunkDir = new File(getDirectory(), "DIM1");
            } else {
                loadChunkDir = loadDataFolder;
                saveChunkDir = getDirectory();
            }
            ChunkRegionLoader loadLoader = new ChunkRegionLoader(loadChunkDir);
            ChunkRegionLoader saveLoader = new ChunkRegionLoader(saveChunkDir);
            return new InstanceChunkLoader(loadLoader, saveLoader);
        }

        @Override
        public File getDataFile(String string) {
            File result = new File(this.loadDataFolder, string + ".dat");
            if (result.isFile()) {
                return result;
            }
            File source = new File(getDirectory(), string + ".dat");
            if (!source.isFile()) {
                return result;
            }
            try {
                Files.copy(source, result);
            } catch (IOException ex) {
                instances.getLogger().log(Level.SEVERE, "Error copying " + source.getPath() + " to " + result.getPath() + " for Instance world: " + world, ex);
            }
            return result;
        }
    }

    // Safe not to extend ChunkRegionLoader - CB does not cast to ChunkRegionLoader anywhere.
    public static final class InstanceChunkLoader implements IChunkLoader, IAsyncChunkSaver {

        private final ChunkRegionLoader loadLoader;
        private final ChunkRegionLoader saveLoader;

        public InstanceChunkLoader(ChunkRegionLoader loadLoader, ChunkRegionLoader saveLoader) {
            this.loadLoader = loadLoader;
            this.saveLoader = saveLoader;
        }

        public Chunk a(net.minecraft.server.v1_4_R1.World world, int i, int j) {
            if (saveLoader.chunkExists(world, i, j)) {
                return saveLoader.a(world, i, j);
            }
            return loadLoader.a(world, i, j);
        }

        public void a(net.minecraft.server.v1_4_R1.World world, Chunk chunk) {
            saveLoader.a(world, chunk);
        }

        public void b(net.minecraft.server.v1_4_R1.World world, Chunk chunk) {
            // Assume this is supposed to be some sort of save operation.
            // Can't tell from NMS - empty method.
            saveLoader.b(world, chunk);
        }

        public void a() {
            // XXX: Can't guess if this is a save or load operation.
        }

        public void b() {
            // XXX: Can't guess if this is a save or load operation.
        }

        public boolean c() {
            // Looks like a flush() / sync() method.
            return saveLoader.c();
        }
    }
}