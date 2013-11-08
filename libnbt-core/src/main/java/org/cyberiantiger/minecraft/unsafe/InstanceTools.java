/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.unsafe;

import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author antony
 */
public interface InstanceTools {

    public World createInstance(Plugin plugin, Difficulty difficulty, String sourceWorldName, String instanceWorldName);

    public void unloadWorld(Plugin plugin, World world);
    
}
