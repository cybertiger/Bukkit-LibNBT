/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.unsafe.v1_9_R2;

import java.io.File;
import net.minecraft.server.v1_9_R2.EntityTracker;
import net.minecraft.server.v1_9_R2.EnumDifficulty;
import net.minecraft.server.v1_9_R2.IDataManager;
import net.minecraft.server.v1_9_R2.IWorldAccess;
import net.minecraft.server.v1_9_R2.MethodProfiler;
import net.minecraft.server.v1_9_R2.MinecraftServer;
import net.minecraft.server.v1_9_R2.WorldData;
import net.minecraft.server.v1_9_R2.WorldManager;
import net.minecraft.server.v1_9_R2.WorldServer;
import org.bukkit.craftbukkit.v1_9_R2.scoreboard.CraftScoreboard;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_9_R2.CraftServer;
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
        plugin.getServer().unloadWorld(world, false);
    }

    @Override
    public org.bukkit.World createInstance(final Plugin plugin, String instanceName, World.Environment env, Difficulty difficulty, File source, File destination) {
        checkDirectories(source, destination);

        MinecraftServer console = ((CraftServer) plugin.getServer()).getServer();
        if (console == null) {
            throw new IllegalStateException("Minecraft console was null");
        }

        CraftServer craftServer = (CraftServer) plugin.getServer();

        IDataManager dataManager =
                new InstanceDataManager(plugin, instanceName, source, destination, craftServer.getServer().getDataConverterManager());

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

        ChunkGenerator generator = new VoidGenerator(Biome.PLAINS, new Coord(wd.b(),wd.c(),wd.d()));

        WorldServer instanceWorld = (WorldServer) new WorldServer(console, dataManager, wd, dimension, console.methodProfiler, env, generator).b();

        instanceWorld.scoreboard = ((CraftScoreboard)plugin.getServer().getScoreboardManager().getMainScoreboard()).getHandle();

        instanceWorld.tracker = new EntityTracker(instanceWorld);
        instanceWorld.addIWorldAccess((IWorldAccess) new WorldManager(console, instanceWorld));
        // EnumDifficulty and Difficulty have same order of enum values.
        instanceWorld.getWorldData().setDifficulty(EnumDifficulty.values()[difficulty.ordinal()]);
        instanceWorld.setSpawnFlags(true, true);
        instanceWorld.keepSpawnInMemory = false;
        console.worlds.add(instanceWorld);

        instanceWorld.getWorld().getPopulators().addAll(generator.getDefaultPopulators(instanceWorld.getWorld()));

        plugin.getServer().getPluginManager().callEvent(new WorldInitEvent(instanceWorld.getWorld()));
        plugin.getServer().getPluginManager().callEvent(new WorldLoadEvent(instanceWorld.getWorld()));

        return instanceWorld.getWorld();
    }
}
