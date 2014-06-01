/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.unsafe.v1_5_R2;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_5_R2.NBTBase;
import net.minecraft.server.v1_5_R2.NBTTagByte;
import net.minecraft.server.v1_5_R2.NBTTagByteArray;
import net.minecraft.server.v1_5_R2.NBTTagCompound;
import net.minecraft.server.v1_5_R2.NBTTagDouble;
import net.minecraft.server.v1_5_R2.NBTTagFloat;
import net.minecraft.server.v1_5_R2.NBTTagInt;
import net.minecraft.server.v1_5_R2.NBTTagIntArray;
import net.minecraft.server.v1_5_R2.NBTTagList;
import net.minecraft.server.v1_5_R2.NBTTagLong;
import net.minecraft.server.v1_5_R2.NBTTagShort;
import net.minecraft.server.v1_5_R2.NBTTagString;
import net.minecraft.server.v1_5_R2.TileEntity;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_5_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.cyberiantiger.minecraft.nbt.ByteArrayTag;
import org.cyberiantiger.minecraft.nbt.ByteTag;
import org.cyberiantiger.minecraft.nbt.CompoundTag;
import org.cyberiantiger.minecraft.nbt.DoubleTag;
import org.cyberiantiger.minecraft.nbt.FloatTag;
import org.cyberiantiger.minecraft.nbt.IntArrayTag;
import org.cyberiantiger.minecraft.nbt.IntTag;
import org.cyberiantiger.minecraft.nbt.ListTag;
import org.cyberiantiger.minecraft.nbt.LongTag;
import org.cyberiantiger.minecraft.nbt.ShortTag;
import org.cyberiantiger.minecraft.nbt.StringTag;
import org.cyberiantiger.minecraft.nbt.Tag;
import org.cyberiantiger.minecraft.nbt.TagType;

/**
 *
 * @author antony
 */
public final class NBTTools implements org.cyberiantiger.minecraft.unsafe.NBTTools {

    public NBTTagCompound toNativeCompound(CompoundTag tag) {
        NBTTagCompound result = new NBTTagCompound(tag.getName());
        for (Tag t : tag.getValue().values()) {
            switch (t.getType()) {
                case BYTE:
                    result.setByte(t.getName(), ((ByteTag) t).getRawValue());
                    break;
                case BYTE_ARRAY:
                    result.setByteArray(t.getName(), ((ByteArrayTag) t).getValue());
                    break;
                case COMPOUND:
                    result.setCompound(t.getName(), toNativeCompound(((CompoundTag) t)));
                    break;
                case DOUBLE:
                    result.setDouble(t.getName(), ((DoubleTag) t).getRawValue());
                    break;
                case FLOAT:
                    result.setFloat(t.getName(), ((FloatTag) t).getRawValue());
                    break;
                case INT:
                    result.setInt(t.getName(), ((IntTag) t).getRawValue());
                    break;
                case INT_ARRAY:
                    result.setIntArray(t.getName(), ((IntArrayTag) t).getValue());
                    break;
                case LIST:
                    result.set(t.getName(), toNativeList((ListTag) t));
                    break;
                case LONG:
                    result.setLong(t.getName(), ((LongTag) t).getValue());
                    break;
                case SHORT:
                    result.setShort(t.getName(), ((ShortTag) t).getValue());
                    break;
                case STRING:
                    result.setString(t.getName(), ((StringTag) t).getValue());
                    break;
            }
        }
        return result;
    }

    public NBTTagList toNativeList(ListTag tag) {
        NBTTagList result = new NBTTagList(tag.getName());

        switch (tag.getListType()) {
            case BYTE:
                for (ByteTag t : (ByteTag[]) tag.getValue()) {
                    result.add(new NBTTagByte(null, t.getRawValue()));
                }
                break;
            case BYTE_ARRAY:
                for (ByteArrayTag t : (ByteArrayTag[]) tag.getValue()) {
                    result.add(new NBTTagByteArray(null, ((ByteArrayTag) t).getValue()));
                }
                break;
            case COMPOUND:
                for (CompoundTag t : (CompoundTag[]) tag.getValue()) {
                    result.add(toNativeCompound(t));
                }
                break;
            case DOUBLE:
                for (DoubleTag t : (DoubleTag[]) tag.getValue()) {
                    result.add(new NBTTagDouble(null, t.getRawValue()));
                }
                break;
            case FLOAT:
                for (FloatTag t : (FloatTag[]) tag.getValue()) {
                    result.add(new NBTTagFloat(null, t.getRawValue()));
                }
                break;
            case INT:
                for (IntTag t : (IntTag[]) tag.getValue()) {
                    result.add(new NBTTagInt(null, t.getRawValue()));
                }
                break;
            case INT_ARRAY:
                for (IntArrayTag t : (IntArrayTag[]) tag.getValue()) {
                    result.add(new NBTTagIntArray(null, t.getValue()));
                }
                break;
            case LIST:
                for (ListTag t : (ListTag[]) tag.getValue()) {
                    result.add(toNativeList((ListTag) t));
                }
            case LONG:
                for (LongTag t : (LongTag[]) tag.getValue()) {
                    result.add(new NBTTagLong(null, t.getRawValue()));
                }
                break;
            case SHORT:
                for (ShortTag t : (ShortTag[]) tag.getValue()) {
                    result.add(new NBTTagShort(null, t.getRawValue()));
                }
                break;
            case STRING:
                for (StringTag t : (StringTag[]) tag.getValue()) {
                    result.add(new NBTTagString(null, t.getValue()));
                }
        }

        return result;
    }

    public static final Field COMPOUND_MAP_FIELD;

    static {
        Field f = null;
        try {
            f = NBTTagCompound.class.getDeclaredField("map");
            f.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        COMPOUND_MAP_FIELD = f;
    }

    public CompoundTag fromNativeCompound(NBTTagCompound tag) {
        if (COMPOUND_MAP_FIELD != null) {
            try {
                Map<String, Tag> result = new HashMap<String, Tag>();
                Map<String, NBTBase> map = (Map<String, NBTBase>) COMPOUND_MAP_FIELD.get(tag);
                for (NBTBase b : map.values()) {
                    switch (TagType.values()[b.getTypeId()]) {
                        case BYTE:
                            result.put(b.getName(), new ByteTag(b.getName(), ((NBTTagByte) b).data));
                            break;
                        case BYTE_ARRAY:
                            result.put(b.getName(), new ByteArrayTag(b.getName(), ((NBTTagByteArray) b).data));
                            break;
                        case COMPOUND:
                            result.put(b.getName(), fromNativeCompound((NBTTagCompound) b));
                            break;
                        case DOUBLE:
                            result.put(b.getName(), new DoubleTag(b.getName(), ((NBTTagDouble) b).data));
                            break;
                        case FLOAT:
                            result.put(b.getName(), new FloatTag(b.getName(), ((NBTTagFloat) b).data));
                            break;
                        case INT:
                            result.put(b.getName(), new IntTag(b.getName(), ((NBTTagInt) b).data));
                            break;
                        case INT_ARRAY:
                            result.put(b.getName(), new IntArrayTag(b.getName(), ((NBTTagIntArray) b).data));
                            break;
                        case LIST:
                            result.put(b.getName(), fromNativeList((NBTTagList) b));
                            break;
                        case LONG:
                            result.put(b.getName(), new LongTag(b.getName(), ((NBTTagLong) b).data));
                            break;
                        case SHORT:
                            result.put(b.getName(), new ShortTag(b.getName(), ((NBTTagShort) b).data));
                            break;
                        case STRING:
                            result.put(b.getName(), new StringTag(b.getName(), ((NBTTagString) b).data));
                            break;
                    }
                }
                return new CompoundTag(tag.getName(), result);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static final Field LIST_TYPE_FIELD;

    static {
        Field f = null;
        try {
            f = NBTTagList.class.getDeclaredField("type");
            f.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        LIST_TYPE_FIELD = f;
    }

    public ListTag fromNativeList(NBTTagList tag) {
        try {
            TagType type = TagType.values()[(Byte) LIST_TYPE_FIELD.get(tag)];
            if (type == TagType.END) {
                type = TagType.BYTE;
            }
            Tag[] t = (Tag[]) Array.newInstance(type.getTagClass(), tag.size());
            switch (type) {
                case BYTE:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new ByteTag(null, ((NBTTagByte) tag.get(i)).data);
                    }
                    break;
                case BYTE_ARRAY:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new ByteArrayTag(null, ((NBTTagByteArray) tag.get(i)).data);
                    }
                    break;
                case COMPOUND:
                    for (int i = 0; i < tag.size(); i++) {
                        if (tag.get(i) == null) {
                            t[i] = new CompoundTag();
                        } else {
                            t[i] = fromNativeCompound((NBTTagCompound) tag.get(i));
                        }
                    }
                    break;
                case DOUBLE:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new DoubleTag(null, ((NBTTagDouble) tag.get(i)).data);
                    }
                    break;
                case FLOAT:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new FloatTag(null, ((NBTTagFloat) tag.get(i)).data);
                    }
                    break;
                case INT:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new IntTag(null, ((NBTTagInt) tag.get(i)).data);
                    }
                    break;
                case INT_ARRAY:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new IntArrayTag(null, ((NBTTagIntArray) tag.get(i)).data);
                    }
                    break;
                case LIST:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = fromNativeList((NBTTagList) tag.get(i));
                    }
                    break;
                case LONG:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new LongTag(null, ((NBTTagLong) tag.get(i)).data);
                    }
                    break;
                case SHORT:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new ShortTag(null, ((NBTTagShort) tag.get(i)).data);
                    }
                    break;
                case STRING:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new StringTag(null, ((NBTTagString) tag.get(i)).data);
                    }
                    break;
            }
            return new ListTag(tag.getName(), type, t);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void writeTileEntity(Block block, CompoundTag tag) {
        CraftWorld craftWorld = (CraftWorld) block.getWorld();
        TileEntity tileEntity = craftWorld.getTileEntityAt(block.getX(), block.getY(), block.getZ());
        if (tileEntity == null) {
            return;
        }
        tileEntity.a(toNativeCompound(tag));
        tileEntity.update();
        craftWorld.getHandle().notify(block.getX(), block.getY(), block.getZ());
    }

    public CompoundTag readTileEntity(Block block) {
        CraftWorld craftWorld = (CraftWorld) block.getWorld();
        TileEntity tileEntity = craftWorld.getTileEntityAt(block.getX(), block.getY(), block.getZ());
        if (tileEntity == null) {
            return null;
        }
        NBTTagCompound tag = new NBTTagCompound();
        tileEntity.b(tag);
        return fromNativeCompound(tag);
    }

    public CompoundTag readItemStack(ItemStack stack) {
        try {
            Field f = CraftItemStack.class.getDeclaredField("handle");
            f.setAccessible(true);
            net.minecraft.server.v1_5_R2.ItemStack nativeStack = (net.minecraft.server.v1_5_R2.ItemStack) f.get(stack);
            NBTTagCompound compound = new NBTTagCompound();
            nativeStack.save(compound);
            return fromNativeCompound(compound);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public CompoundTag readEntity(Entity e) {
        net.minecraft.server.v1_5_R2.Entity handle = ((CraftEntity) e).getHandle();
        NBTTagCompound compound = new NBTTagCompound();
        handle.e(compound);
        return fromNativeCompound(compound);
    }

    @Override
    public void updateEntity(Entity entity, CompoundTag tag) {
        CraftEntity craftEntity = (CraftEntity)entity;
        craftEntity.getHandle().f(toNativeCompound(tag));
    }

    @Override
    public Entity getEntityById(World world, int id) {
        CraftWorld craftWorld = (CraftWorld) world;
        net.minecraft.server.v1_5_R2.Entity entity = craftWorld.getHandle().getEntity(id);
        if (entity == null)
            return null;
        return entity.getBukkitEntity();
    }
}
