/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.unsafe.v1_6_R3;

import org.cyberiantiger.minecraft.util.FileUtils;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import net.minecraft.server.v1_6_R3.Chunk;
import net.minecraft.server.v1_6_R3.ChunkRegionLoader;
import net.minecraft.server.v1_6_R3.EntityTracker;
import net.minecraft.server.v1_6_R3.IAsyncChunkSaver;
import net.minecraft.server.v1_6_R3.IChunkLoader;
import net.minecraft.server.v1_6_R3.IDataManager;
import net.minecraft.server.v1_6_R3.IWorldAccess;
import net.minecraft.server.v1_6_R3.MethodProfiler;
import net.minecraft.server.v1_6_R3.MinecraftServer;
import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.WorldData;
import net.minecraft.server.v1_6_R3.WorldManager;
import net.minecraft.server.v1_6_R3.WorldProvider;
import net.minecraft.server.v1_6_R3.WorldServer;
import net.minecraft.server.v1_6_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_6_R3.RegionFile;
import net.minecraft.server.v1_6_R3.RegionFileCache;
import net.minecraft.server.v1_6_R3.ServerNBTManager;
import net.minecraft.server.v1_6_R3.WorldProviderHell;
import net.minecraft.server.v1_6_R3.WorldProviderTheEnd;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.cyberiantiger.minecraft.Coord;
import org.cyberiantiger.minecraft.generator.VoidGenerator;

/**
 *
 * @author antony
 */
public final class InstanceTools implements org.cyberiantiger.minecraft.unsafe.InstanceTools {
    public static final String FOLDER_NAME = "worlds";

    private boolean isParent(File parent, File child) {
        if (child == null) {
            return false;
        } else if (child == parent) {
            return true;
        } else {
            return isParent(parent, child.getParentFile());
        }
    }

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
    public org.bukkit.World createInstance(final Plugin instances, Difficulty difficulty, String sourceWorld, String instanceName) {
        World source = instances.getServer().getWorld(sourceWorld);
        File dataFolder;
        if (source == null) {
            dataFolder = new File(instances.getServer().getWorldContainer(), sourceWorld);
            if (!dataFolder.isDirectory()) {
                instances.getLogger().info("Failed to create instance, could not find data folder " + dataFolder.getAbsolutePath() + " for world " + sourceWorld);
                return null;
            }
        } else {
            instances.getLogger().warning("Creating instance from loaded world " + sourceWorld + 
                    "; This will increase loading time for instanced worlds and may cause " +
                    "chunk corruption in the instance world, unload the source world in " +
                    "a production environment e.g. via /mvunload <world>.");
            dataFolder = source.getWorldFolder();
        }

        MinecraftServer console = ((CraftServer) instances.getServer()).getServer();
        if (console == null) {
            instances.getLogger().info("Failed to create instance, could not locate console object.");
            return null;
        }

        File worldFolder = new File(instances.getDataFolder(), FOLDER_NAME);
        File saveDataFolder = new File(worldFolder, instanceName);

        if (saveDataFolder.isDirectory()) {
            try {
                final File tempFile = File.createTempFile("deleted_",".world", worldFolder);
                tempFile.delete();
                if(saveDataFolder.renameTo(tempFile)) {
                    instances.getLogger().info("Renamed old world data");
                    instances.getServer().getScheduler().runTaskAsynchronously(instances, new Runnable() {

                        public void run() {
                            try {
                                if (!FileUtils.deleteRecursively(tempFile)) {
                                    instances.getLogger().warning("Failed to delete archived instances world: " + tempFile);
                                }
                            } catch (IOException e) {
                                instances.getLogger().warning("Failed to delete archived instances world: " + tempFile);
                            }
                        }
                        
                    });
                } else {
                    if (!FileUtils.deleteRecursively(saveDataFolder)) {
                        instances.getLogger().log(Level.SEVERE, "Failed to delete world folder: " + saveDataFolder);
                        return null;
                    }
                }
            } catch (IOException ex) {
                instances.getLogger().log(Level.SEVERE, null, ex);
                return null;
            }
        }

        saveDataFolder.mkdirs();

        IDataManager dataManager =
                new InstanceDataManager(instances, source, dataFolder, saveDataFolder);

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

        World.Environment env;

        switch (wd.j()) {
            case 0:
                env = World.Environment.NORMAL;
            case -1:
                env = World.Environment.NETHER;
            case 1:
                env = World.Environment.THE_END;
            default:
                env = World.Environment.NORMAL;
        }

        ChunkGenerator generator = new VoidGenerator(Biome.PLAINS, new Coord(wd.c(),wd.d(),wd.e()));

        WorldServer instanceWorld = new WorldServer(console, dataManager, instanceName, dimension, null, console.methodProfiler, console.getLogger(), env, generator);

        instanceWorld.worldMaps = console.worlds.get(0).worldMaps;
        instanceWorld.tracker = new EntityTracker(instanceWorld);
        instanceWorld.addIWorldAccess((IWorldAccess) new WorldManager(console, instanceWorld));
        instanceWorld.difficulty = difficulty.getValue();
        instanceWorld.keepSpawnInMemory = false;
        console.worlds.add(instanceWorld);

        instanceWorld.getWorld().getPopulators().addAll(generator.getDefaultPopulators(instanceWorld.getWorld()));

        instances.getServer().getPluginManager().callEvent(new WorldInitEvent(instanceWorld.getWorld()));
        instances.getServer().getPluginManager().callEvent(new WorldLoadEvent(instanceWorld.getWorld()));

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
        private final World source;

        public InstanceDataManager(Plugin instances, World source, File loadDataFolder, File saveDataFolder) {
            // false flag - do not create players directory.
            super(saveDataFolder.getParentFile(), saveDataFolder.getName(), false);
            this.instances = instances;
            this.source = source;
            this.loadDataFolder = loadDataFolder;
            this.world = saveDataFolder.getName();
        }

        @Override
        public WorldData getWorldData() {
            if (source != null) {
                // Force the source world to save, this could be costly.
                source.save();
            }
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
            return new InstanceChunkLoader(source, loadLoader, saveLoader);
        }

        @Override
        public File getDataFile(String string) {
            if (source != null) {
                source.save();
            }
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

        private final World source;
        private final ChunkRegionLoader loadLoader;
        private final ChunkRegionLoader saveLoader;

        public InstanceChunkLoader(World source, ChunkRegionLoader loadLoader, ChunkRegionLoader saveLoader) {
            this.source = source;
            this.loadLoader = loadLoader;
            this.saveLoader = saveLoader;
        }

        public Chunk a(net.minecraft.server.v1_6_R3.World world, int i, int j) {
            if (saveLoader.chunkExists(world, i, j)) {
                return saveLoader.a(world, i, j);
            }
            if (source != null) {
                source.save();
            }
            return loadLoader.a(world, i, j);
        }

        public void a(net.minecraft.server.v1_6_R3.World world, Chunk chunk) {
            saveLoader.a(world, chunk);
        }

        public void b(net.minecraft.server.v1_6_R3.World world, Chunk chunk) {
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
