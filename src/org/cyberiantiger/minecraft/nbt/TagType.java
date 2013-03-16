package org.cyberiantiger.minecraft.nbt;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum TagType {

    END(null, null) {

        public Tag read(String name, TagInputStream in) throws IOException {
            return null;
        }

        public void write(Tag tag, TagOutputStream out) throws IOException {
            // NOOP
        }

        @Override
        public String readName(TagInputStream in) throws IOException {
            return null;
        }

        @Override
        public void writeName(String name, TagOutputStream out) throws IOException {
            // NOOP
        }
    },
    BYTE(ByteTag.class, Byte.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            out.writeByte(((ByteTag) tag).getRawValue());
        }

        public ByteTag read(String name, TagInputStream in) throws IOException {
            return new ByteTag(name, in.readByte());
        }
    },
    SHORT(ShortTag.class, Short.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            out.writeShort(((ShortTag) tag).getRawValue());
        }

        public ShortTag read(String name, TagInputStream in) throws IOException {
            return new ShortTag(name, in.readShort());
        }
    },
    INT(IntTag.class, Integer.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            out.writeInt(((IntTag) tag).getRawValue());
        }

        public IntTag read(String name, TagInputStream in) throws IOException {
            return new IntTag(name, in.readInt());
        }
    },
    LONG(LongTag.class, Long.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            out.writeLong(((LongTag) tag).getRawValue());
        }

        public LongTag read(String name, TagInputStream in) throws IOException {
            return new LongTag(name, in.readLong());
        }
    },
    FLOAT(FloatTag.class, Float.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            out.writeFloat(((FloatTag) tag).getRawValue());
        }

        public FloatTag read(String name, TagInputStream in) throws IOException {
            return new FloatTag(name, in.readFloat());
        }
    },
    DOUBLE(DoubleTag.class, Double.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            out.writeDouble(((DoubleTag) tag).getRawValue());
        }

        public DoubleTag read(String name, TagInputStream in) throws IOException {
            return new DoubleTag(name, in.readDouble());
        }
    },
    BYTE_ARRAY(ByteArrayTag.class, byte[].class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            byte[] data = ((ByteArrayTag) tag).getValue();
            out.writeInt(data.length);
            out.write(data);
        }

        public ByteArrayTag read(String name, TagInputStream in) throws IOException {
            int length = in.readInt();
            byte[] value = new byte[length];
            in.readFully(value);
            return new ByteArrayTag(name, value);
        }
    },
    STRING(StringTag.class, String.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            out.writeMCString(((StringTag) tag).getValue());
        }

        public StringTag read(String name, TagInputStream in) throws IOException {
            return new StringTag(name, in.readMCString());
        }
    },
    LIST(ListTag.class, Tag[].class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            TagType type = ((ListTag) tag).getListType();
            Tag[] values = ((ListTag) tag).getValue();
            out.writeByte(type.ordinal());
            out.writeInt(values.length);
            for (int i = 0; i < values.length; i++) {
                type.write(values[i], out);
            }
        }

        public ListTag read(String name, TagInputStream in) throws IOException {
            TagType type = TagType.values()[in.readByte()];
            int length = in.readInt();
            Tag[] value = (Tag[]) Array.newInstance(type.getTagClass(), length);
            for (int i = 0; i < length; i++) {
                value[i] = type.read(null, in);
            }
            return new ListTag(name, type, value);
        }
    },
    COMPOUND(CompoundTag.class, Map.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            Collection<Tag> tags = ((CompoundTag) tag).getValue().values();
            for (Tag t : tags) {
                out.writeTag(t);
            }
            out.writeByte(TagType.END.ordinal());
        }

        public CompoundTag read(String name, TagInputStream in) throws IOException {
            Map<String, Tag> values = new HashMap<String, Tag>();
            Tag tag;
            while ((tag = in.readTag()) != null) {
                values.put(tag.getName(), tag);
            }
            return new CompoundTag(name, values);
        }
    },
    INT_ARRAY(IntArrayTag.class, int[].class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            int[] value = ((IntArrayTag)tag).getValue();
            out.writeInt(value.length);
            for (int i = 0; i < value.length; i++) {
                out.writeInt(value[i]);
            }
        }

        public IntArrayTag read(String name, TagInputStream in) throws IOException {
            int length = in.readInt();
            int[] value = new int[length];
            for (int i = 0; i < length; i++) {
                value[i] = in.readInt();
            }
            return new IntArrayTag(name, value);
        }
    };
    
    private final Class<? extends Tag> tagClass;
    private final Class<?> valueClass;

    private TagType(Class<? extends Tag> tagClass, Class<?> valueClass) {
        this.tagClass = tagClass;
        this.valueClass = valueClass;
    }

    public Class<? extends Tag> getTagClass() {
        return tagClass;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public abstract <T extends Tag> T read(String name, TagInputStream in) throws IOException;

    public String readName(TagInputStream in) throws IOException {
        return in.readMCString();
    }

    public abstract <T extends Tag> void write(T tag, TagOutputStream out) throws IOException;

    public void writeName(String name, TagOutputStream out) throws IOException {
        out.writeMCString(name);
    }
}
