/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.unsafe;

import java.util.UUID;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.cyberiantiger.minecraft.nbt.CompoundTag;

/**
 *
 * @author antony
 */
public interface NBTTools {

    public boolean isEntityByIdSupported();

    public boolean isEntityByUuidSupported();

    public Entity getEntityById(World world, int id);

    public Entity getEntityByUUID(World world, UUID id);

    public CompoundTag readEntity(Entity entity);

    public void updateEntity(Entity entity, CompoundTag tag);

    public CompoundTag readTileEntity(Block b);

    public void writeTileEntity(Block b, CompoundTag tag);

    public CompoundTag readItemStack(ItemStack stack);

    public ItemStack createItemStack(CompoundTag tag);
    
}