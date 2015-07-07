/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.unsafe.v1_8_R3;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagByte;
import net.minecraft.server.v1_8_R3.NBTTagByteArray;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagDouble;
import net.minecraft.server.v1_8_R3.NBTTagFloat;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagIntArray;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagLong;
import net.minecraft.server.v1_8_R3.NBTTagShort;
import net.minecraft.server.v1_8_R3.NBTTagString;
import net.minecraft.server.v1_8_R3.TileEntity;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
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
        NBTTagCompound result = new NBTTagCompound();
        for (Map.Entry<String,Tag> e : tag.getValue().entrySet()) {
            NBTBase base;
            String name = e.getKey();
            Tag t = e.getValue();
            switch (t.getType()) {
                case BYTE:
                    base = new NBTTagByte(((ByteTag)t).getRawValue());
                    break;
                case BYTE_ARRAY:
                    base = new NBTTagByteArray(((ByteArrayTag)t).getValue());
                    break;
                case COMPOUND:
                    base = toNativeCompound((CompoundTag)t);
                    break;
                case DOUBLE:
                    base = new NBTTagDouble(((DoubleTag)t).getRawValue());
                    break;
                case FLOAT:
                    base = new NBTTagFloat(((FloatTag)t).getRawValue());
                    break;
                case INT:
                    base = new NBTTagInt(((IntTag)t).getRawValue());
                    break;
                case INT_ARRAY:
                    base = new NBTTagIntArray(((IntArrayTag)t).getValue());
                    break;
                case LIST:
                    base = toNativeList((ListTag)t);
                    break;
                case LONG:
                    base = new NBTTagLong(((LongTag)t).getRawValue());
                    break;
                case SHORT:
                    base = new NBTTagShort(((ShortTag)t).getRawValue());
                    break;
                case STRING:
                    base = new NBTTagString(((StringTag)t).getValue());
                    break;
                default:
                    // Can't be reached.
                    throw new IllegalArgumentException();
            }
            result.set(name, base);
        }
        return result;
    }

    public NBTTagList toNativeList(ListTag tag) {
        NBTTagList result = new NBTTagList();

        switch (tag.getListType()) {
            case BYTE:
                for (ByteTag t : (ByteTag[]) tag.getValue()) {
                    result.add(new NBTTagByte(t.getRawValue()));
                }
                break;
            case BYTE_ARRAY:
                for (ByteArrayTag t : (ByteArrayTag[]) tag.getValue()) {
                    result.add(new NBTTagByteArray(((ByteArrayTag) t).getValue()));
                }
                break;
            case COMPOUND:
                for (CompoundTag t : (CompoundTag[]) tag.getValue()) {
                    result.add(toNativeCompound(t));
                }
                break;
            case DOUBLE:
                for (DoubleTag t : (DoubleTag[]) tag.getValue()) {
                    result.add(new NBTTagDouble(t.getRawValue()));
                }
                break;
            case FLOAT:
                for (FloatTag t : (FloatTag[]) tag.getValue()) {
                    result.add(new NBTTagFloat(t.getRawValue()));
                }
                break;
            case INT:
                for (IntTag t : (IntTag[]) tag.getValue()) {
                    result.add(new NBTTagInt(t.getRawValue()));
                }
                break;
            case INT_ARRAY:
                for (IntArrayTag t : (IntArrayTag[]) tag.getValue()) {
                    result.add(new NBTTagIntArray(t.getValue()));
                }
                break;
            case LIST:
                for (ListTag t : (ListTag[]) tag.getValue()) {
                    result.add(toNativeList((ListTag) t));
                }
            case LONG:
                for (LongTag t : (LongTag[]) tag.getValue()) {
                    result.add(new NBTTagLong(t.getRawValue()));
                }
                break;
            case SHORT:
                for (ShortTag t : (ShortTag[]) tag.getValue()) {
                    result.add(new NBTTagShort(t.getRawValue()));
                }
                break;
            case STRING:
                for (StringTag t : (StringTag[]) tag.getValue()) {
                    result.add(new NBTTagString(t.getValue()));
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
        return fromNativeCompound(null, tag);
    }

    public CompoundTag fromNativeCompound(String parentName, NBTTagCompound tag) {
        if (COMPOUND_MAP_FIELD != null) {
            try {
                Map<String, Tag> result = new HashMap<String, Tag>();
                Map<String, NBTBase> map = (Map<String, NBTBase>) COMPOUND_MAP_FIELD.get(tag);
                for (Map.Entry<String,NBTBase> e : map.entrySet()) {
                    String name = e.getKey();
                    NBTBase b = e.getValue();
                    switch (TagType.values()[b.getTypeId()]) {
                        case BYTE:
                            result.put(name, new ByteTag(name, ((NBTTagByte) b).f()));
                            break;
                        case BYTE_ARRAY:
                            result.put(name, new ByteArrayTag(name, ((NBTTagByteArray) b).c()));
                            break;
                        case COMPOUND:
                            result.put(name, fromNativeCompound(name, (NBTTagCompound) b));
                            break;
                        case DOUBLE:
                            result.put(name, new DoubleTag(name, ((NBTTagDouble) b).g()));
                            break;
                        case FLOAT:
                            result.put(name, new FloatTag(name, ((NBTTagFloat) b).h()));
                            break;
                        case INT:
                            result.put(name, new IntTag(name, ((NBTTagInt) b).d()));
                            break;
                        case INT_ARRAY:
                            result.put(name, new IntArrayTag(name, ((NBTTagIntArray) b).c()));
                            break;
                        case LIST:
                            result.put(name, fromNativeList(name, (NBTTagList) b));
                            break;
                        case LONG:
                            result.put(name, new LongTag(name, ((NBTTagLong) b).c()));
                            break;
                        case SHORT:
                            result.put(name, new ShortTag(name, ((NBTTagShort) b).e()));
                            break;
                        case STRING:
                            result.put(name, new StringTag(name, ((NBTTagString) b).a_()));
                            break;
                    }
                }
                return new CompoundTag(parentName, result);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static final Field LIST_LIST_FIELD;

    static {
        Field f = null;
        try {
            f = NBTTagList.class.getDeclaredField("list");
            f.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        LIST_LIST_FIELD = f;
    }

    public ListTag fromNativeList(NBTTagList tag) {
        return fromNativeList(null, tag);
    }

    public ListTag fromNativeList(String parentName, NBTTagList tag) {
        try {
            TagType type = TagType.values()[tag.f()];
            if (type == TagType.END) {
                type = TagType.BYTE;
            }
            List<? extends NBTBase> list = (List<? extends NBTBase>) LIST_LIST_FIELD.get(tag);
            Tag[] t = (Tag[]) Array.newInstance(type.getTagClass(), tag.size());
            switch (type) {
                case BYTE:
                    for (int i = 0; i < list.size(); i++) {
                        t[i] = new ByteTag(null, ((NBTTagByte)list.get(i)).f());
                    }
                    break;
                case BYTE_ARRAY:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new ByteArrayTag(null, ((NBTTagByteArray)list.get(i)).c());
                    }
                    break;
                case COMPOUND:
                    for (int i = 0; i < tag.size(); i++) {
                        if (tag.get(i) == null) {
                            t[i] = new CompoundTag();
                        } else {
                            t[i] = fromNativeCompound((NBTTagCompound) list.get(i));
                        }
                    }
                    break;
                case DOUBLE:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new DoubleTag(null, ((NBTTagDouble) list.get(i)).g());
                    }
                    break;
                case FLOAT:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new FloatTag(null, ((NBTTagFloat) list.get(i)).h());
                    }
                    break;
                case INT:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new IntTag(null, ((NBTTagInt) list.get(i)).d());
                    }
                    break;
                case INT_ARRAY:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new IntArrayTag(null, ((NBTTagIntArray) list.get(i)).c());
                    }
                    break;
                case LIST:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = fromNativeList((NBTTagList) list.get(i));
                    }
                    break;
                case LONG:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new LongTag(null, ((NBTTagLong) list.get(i)).c());
                    }
                    break;
                case SHORT:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new ShortTag(null, ((NBTTagShort) list.get(i)).e());
                    }
                    break;
                case STRING:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new StringTag(null, ((NBTTagString) list.get(i)).a_());
                    }
                    break;
            }
            return new ListTag(parentName, type, t);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void writeTileEntity(Block block, CompoundTag tag) {
        CraftWorld craftWorld = (CraftWorld) block.getWorld();
        TileEntity tileEntity = craftWorld.getTileEntityAt(block.getX(), block.getY(), block.getZ());
        if (tileEntity == null) {
            return;
        }
        tileEntity.a(toNativeCompound(tag));
        tileEntity.update();
        craftWorld.getHandle().notify(new BlockPosition(block.getX(), block.getY(), block.getZ()));
    }

    @Override
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

    @Override
    public CompoundTag readItemStack(ItemStack stack) {
        net.minecraft.server.v1_8_R3.ItemStack nativeStack = CraftItemStack.asNMSCopy(stack);
        if (nativeStack == null) 
            return null;
        NBTTagCompound compound = new NBTTagCompound();
        nativeStack.save(compound);
        return fromNativeCompound(compound);
    }

    @Override
    public ItemStack createItemStack(CompoundTag tag) {
        net.minecraft.server.v1_8_R3.ItemStack nativeStack = new net.minecraft.server.v1_8_R3.ItemStack((net.minecraft.server.v1_8_R3.Item)null);
        nativeStack.c(toNativeCompound(tag));
        return CraftItemStack.asCraftMirror(nativeStack);
    }

    @Override
    public CompoundTag readEntity(Entity e) {
        net.minecraft.server.v1_8_R3.Entity handle = ((CraftEntity) e).getHandle();
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
    public Entity getEntityByUUID(World world, UUID id) {
        CraftWorld craftWorld = (CraftWorld) world;
        net.minecraft.server.v1_8_R3.Entity entity = craftWorld.getHandle().getEntity(id);
        if (entity == null)
            return null;
        return entity.getBukkitEntity();
    }

    @Override
    public boolean isEntityByIdSupported() {
        return false;
    }

    @Override
    public boolean isEntityByUuidSupported() {
        return true;
    }

    @Override
    public Entity getEntityById(World world, int id) {
        throw new UnsupportedOperationException("Not supported");
    }
}
