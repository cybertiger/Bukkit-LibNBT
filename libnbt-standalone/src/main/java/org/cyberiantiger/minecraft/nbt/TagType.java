package org.cyberiantiger.minecraft.nbt;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum TagType {

    END(null, null) {

        public Tag read(TagInputStream in) throws IOException {
            return EndTag.VALUE;
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

        @Override
        public EndTag[] newArray(int size) {
            throw new IllegalStateException("Cannot construct arrays of end tags");
        }
    },
    BYTE(ByteTag.class, Byte.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            out.writeByte(((ByteTag) tag).getRawValue());
        }

        public ByteTag read(TagInputStream in) throws IOException {
            return new ByteTag(in.readByte());
        }

        @Override
        public ByteTag[] newArray(int size) {
            return new ByteTag[size];
        }
    },
    SHORT(ShortTag.class, Short.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            out.writeShort(((ShortTag) tag).getRawValue());
        }

        public ShortTag read(TagInputStream in) throws IOException {
            return new ShortTag(in.readShort());
        }

        @Override
        public ShortTag[] newArray(int size) {
            return new ShortTag[size];
        }
    },
    INT(IntTag.class, Integer.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            out.writeInt(((IntTag) tag).getRawValue());
        }

        public IntTag read(TagInputStream in) throws IOException {
            return new IntTag(in.readInt());
        }

        @Override
        public IntTag[] newArray(int size) {
            return new IntTag[size];
        }
    },
    LONG(LongTag.class, Long.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            out.writeLong(((LongTag) tag).getRawValue());
        }

        public LongTag read(TagInputStream in) throws IOException {
            return new LongTag(in.readLong());
        }

        @Override
        public LongTag[] newArray(int size) {
            return new LongTag[size];
        }
    },
    FLOAT(FloatTag.class, Float.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            out.writeFloat(((FloatTag) tag).getRawValue());
        }

        public FloatTag read(TagInputStream in) throws IOException {
            return new FloatTag(in.readFloat());
        }

        @Override
        public FloatTag[] newArray(int size) {
            return new FloatTag[size];
        }
    },
    DOUBLE(DoubleTag.class, Double.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            out.writeDouble(((DoubleTag) tag).getRawValue());
        }

        public DoubleTag read(TagInputStream in) throws IOException {
            return new DoubleTag(in.readDouble());
        }

        @Override
        public DoubleTag[] newArray(int size) {
            return new DoubleTag[size];
        }
    },
    BYTE_ARRAY(ByteArrayTag.class, byte[].class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            byte[] data = ((ByteArrayTag) tag).getValue();
            out.writeInt(data.length);
            out.write(data);
        }

        public ByteArrayTag read(TagInputStream in) throws IOException {
            int length = in.readInt();
            byte[] value = new byte[length];
            in.readFully(value);
            return new ByteArrayTag(value);
        }

        @Override
        public ByteArrayTag[] newArray(int size) {
            return new ByteArrayTag[size];
        }
    },
    STRING(StringTag.class, String.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            out.writeMCString(((StringTag) tag).getValue());
        }

        public StringTag read(TagInputStream in) throws IOException {
            return new StringTag(in.readMCString());
        }

        @Override
        public StringTag[] newArray(int size) {
            return new StringTag[size];
        }
    },
    LIST(ListTag.class, Tag[].class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            TagType type = ((ListTag) tag).getListType();
            Tag[] values = ((ListTag) tag).getValue();
            out.writeByte(type.ordinal());
            if (values == null) {
                out.writeInt(0);
            } else {
                out.writeInt(values.length);
                for (int i = 0; i < values.length; i++) {
                    type.write(values[i], out);
                }
            }
        }

        public ListTag read(TagInputStream in) throws IOException {
            TagType type = TagType.values()[in.readByte()];
            int length = in.readInt();
            if (type == TagType.END) {
                if (length == 0 ) {
                    return new ListTag(type, null);
                } else {
                    throw new IOException("Illegal list tag, had type of 0 and non-zero length");
                }
            } else {
                Tag[] value = (Tag[]) Array.newInstance(type.getTagClass(), length);
                for (int i = 0; i < length; i++) {
                    value[i] = type.read(in);
                }
                return new ListTag(type, value);
            }
        }

        @Override
        public ListTag[] newArray(int size) {
            return new ListTag[size];
        }
    },
    COMPOUND(CompoundTag.class, Map.class) {

        public void write(Tag tag, TagOutputStream out) throws IOException {
            for (Map.Entry<String, Tag> e : ((CompoundTag)tag).getValue().entrySet()) {
                out.writeTag(e);
            }
            out.writeTag(EndTag.TUPLE);
        }

        public CompoundTag read(TagInputStream in) throws IOException {
            Map<String, Tag> values = new HashMap<String, Tag>();
            while (true) {
                TagTuple<? extends Tag> tag = in.readTag();
                if (tag.getValue().getType() == TagType.END) {
                    break;
                }
                values.put(tag.getKey(), tag.getValue());
            }
            return new CompoundTag(values);
        }

        @Override
        public CompoundTag[] newArray(int size) {
            return new CompoundTag[size];
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

        public IntArrayTag read(TagInputStream in) throws IOException {
            int length = in.readInt();
            int[] value = new int[length];
            for (int i = 0; i < length; i++) {
                value[i] = in.readInt();
            }
            return new IntArrayTag(value);
        }

        @Override
        public IntArrayTag[] newArray(int size) {
            return new IntArrayTag[size];
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

    public abstract <T extends Tag> T read(TagInputStream in) throws IOException;

    public String readName(TagInputStream in) throws IOException {
        return in.readMCString();
    }

    public abstract <T extends Tag> void write(T tag, TagOutputStream out) throws IOException;

    public void writeName(String name, TagOutputStream out) throws IOException {
        out.writeMCString(name);
    }

    public abstract <T extends Tag> T[] newArray(int size);
}
