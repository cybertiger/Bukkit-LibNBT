/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.unsafe.v1_12_R1_2;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Level;
import net.minecraft.server.v1_12_R1.ChunkRegionLoader;
import net.minecraft.server.v1_12_R1.DataConverterManager;
import net.minecraft.server.v1_12_R1.IChunkLoader;
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.ServerNBTManager;
import net.minecraft.server.v1_12_R1.WorldData;
import net.minecraft.server.v1_12_R1.WorldProvider;
import net.minecraft.server.v1_12_R1.WorldProviderHell;
import net.minecraft.server.v1_12_R1.WorldProviderTheEnd;
import org.bukkit.plugin.Plugin;

// Have to extend WorldNBTStorage - CB casts IDataManager to it in World.getWorldFolder()

// cannot just implement IDataManager, PlayerFileData
class InstanceDataManager extends ServerNBTManager {
    private static final String WORLD_DATA = "level.dat";
    private static final String WORLD_DATA_OLD = "level.dat_old";
    private final Plugin instances;
    private final File loadDataFolder;
    private final String world;

    public InstanceDataManager(Plugin instances, String instanceName, File loadDataFolder, File saveDataFolder, DataConverterManager converter) {
        // false flag - do not create players directory.
        super(saveDataFolder.getParentFile(), saveDataFolder.getName(), false, converter);
        this.instances = instances;
        this.loadDataFolder = loadDataFolder;
        this.world = instanceName;
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
        WorldData result = null;
        if (file1.exists()) {
            try {
                nbttagcompound = NBTCompressedStreamTools.a((InputStream) (new FileInputStream(file1)));
                nbttagcompound1 = nbttagcompound.getCompound("Data");
                result = new WorldData(nbttagcompound1);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            file1 = new File(loadDataFolder, WORLD_DATA_OLD);
            if (file1.exists()) {
                try {
                    nbttagcompound = NBTCompressedStreamTools.a((InputStream) (new FileInputStream(file1)));
                    nbttagcompound1 = nbttagcompound.getCompound("Data");
                    result = new WorldData(nbttagcompound1);
                } catch (Exception exception1) {
                    exception1.printStackTrace();
                }
            }
        }
        if (result != null) {
            result.a(world);
        }
        return result;
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
        ChunkRegionLoader loadLoader = new ChunkRegionLoader(loadChunkDir, this.a);
        ChunkRegionLoader saveLoader = new ChunkRegionLoader(saveChunkDir, this.a);
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

    @Override
    public UUID getUUID() {
        return UUID.nameUUIDFromBytes( ("libnbt:" + world).getBytes(StandardCharsets.UTF_8) );
    }
}
