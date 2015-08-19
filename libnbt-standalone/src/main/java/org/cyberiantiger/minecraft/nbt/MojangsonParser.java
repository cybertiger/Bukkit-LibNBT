/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.nbt;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author antony
 */
public class MojangsonParser {
    private final PushbackReader in;
    StringBuilder element = new StringBuilder();
    StringBuilder unicodeEscape = new StringBuilder();

    public MojangsonParser(Reader in) {
        this.in = (in instanceof PushbackReader) ? (PushbackReader)in : new PushbackReader(in);
    }

    public TagTuple parse() throws IOException {
        return parseRoot();
    }

    private enum RootState {
        KEY_OR_COMPOUND,
        PAIR_IDENTIFIER,
        COMPOUND,
        EOF;
    }

    private TagTuple parseRoot() throws IOException {
        RootState state = RootState.KEY_OR_COMPOUND;
        String name = null;
        CompoundTag value = null;
        int i;
        while ((i = in.read()) != -1) {
            char ch = (char) i;
            switch (state) {
                case KEY_OR_COMPOUND:
                    switch (ch) {
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            break;
                        case '"':
                            parseQuotedString();
                            name = element.toString();
                            element.setLength(0);
                            state = RootState.PAIR_IDENTIFIER;
                            break;
                        case '{':
                            name = "";
                            value = parseCompound();
                            state = RootState.EOF;
                            break;
                        default:
                            if (('a' <= ch && 'z' >= ch) || ('A' <= ch && 'Z' >= ch)) {
                                element.append(ch);
                                parseString();
                                name = element.toString();
                                element.setLength(0);
                                state = RootState.PAIR_IDENTIFIER;
                            } else {
                                throw new MojangsonParseException("Expected [a-zA-Z{] but got: " + ch);
                            }
                            break;
                    }
                    break;
                case PAIR_IDENTIFIER:
                    switch (ch) {
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            break;
                        case ':':
                            state = RootState.COMPOUND;
                            break;
                        default:
                            throw new MojangsonParseException("Expected [:] but got: " + ch);
                    }
                    break;
                case COMPOUND:
                    switch (ch) {
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            break;
                        case '{':
                            value = parseCompound();
                            state = RootState.EOF;
                            break;
                        default:
                            throw new MojangsonParseException("Expected { but got " + ch);
                    }
                    break;
                case EOF:
                    switch (ch) {
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            break;
                        default:
                            throw new MojangsonParseException("Expected EOF but got " + ch);
                    }
                    break;
            }
        }
        if (state != RootState.EOF) {
            throw new MojangsonParseException("Unexpected EOF");
        }
        return new TagTuple(name, value);
    }

    private enum QuotedStringState {
        LITERAL,
        ESCAPED,
        UNICODE;
    }

    private void parseQuotedString() throws IOException {
        QuotedStringState state = QuotedStringState.LITERAL;
        int i;
        while ((i = in.read()) != -1) {
            char ch = (char) i;
            switch (state) {
                case LITERAL:
                    switch (ch) {
                        case '"':
                            return;
                        case '\\':
                            state = QuotedStringState.ESCAPED;
                            break;
                        default:
                            element.append(ch);
                    }
                    break;
                case ESCAPED:
                    switch (ch) {
                        case '"':
                            element.append('"');
                            state = QuotedStringState.LITERAL;
                            break;
                        case '\\':
                            element.append('\\');
                            state = QuotedStringState.LITERAL;
                            break;
                        case '/':
                            element.append('/');
                            state = QuotedStringState.LITERAL;
                            break;
                        case 'b':
                            element.append('\b');
                            state = QuotedStringState.LITERAL;
                            break;
                        case 'f':
                            element.append('\f');
                            state = QuotedStringState.LITERAL;
                            break;
                        case 'n':
                            element.append('\n');
                            state = QuotedStringState.LITERAL;
                            break;
                        case 'r':
                            element.append('\r');
                            state = QuotedStringState.LITERAL;
                            break;
                        case 't':
                            element.append('\t');
                            state = QuotedStringState.LITERAL;
                            break;
                        case 'u':
                            state = QuotedStringState.UNICODE;
                            break;
                    }
                    break;
                case UNICODE:
                    if (('0' <= ch && '9' >= ch) || ('a' <= ch && 'f' >= ch) || ('A' <= ch && 'F' >= ch)) {
                        unicodeEscape.append(ch);
                        if (unicodeEscape.length() >= 4) {
                            element.append((char)Integer.parseInt(unicodeEscape.toString(), 16));
                            unicodeEscape.setLength(0);
                            state = QuotedStringState.LITERAL;
                        }
                    } else {
                        throw new MojangsonParseException("Unicode escape in string, expected [0-9a-fA-F] got: " + ch);
                    }
                    break;
            }
        }
        throw new MojangsonParseException("Unexpected EOF in quoted string");
    }

    private void parseString() throws IOException {
        int i;
        while ((i = in.read()) != -1) {
            char ch = (char) i;
            if ( ('a' <= ch && 'z' >= ch) || ('A' <= ch && 'Z' >= ch) ) {
                element.append(ch);
            } else {
                in.unread(ch);
                return;
            }
        }
    }

    private enum CompoundState {
        KEY_OR_END,
        PAIR_IDENTIFIER,
        VALUE,
        PAIR_SEPARATOR_OR_END;
    }

    private CompoundTag parseCompound() throws IOException {
        CompoundTag result = new CompoundTag();
        String name = null;
        CompoundState state = CompoundState.KEY_OR_END;
        int i;
        while ((i = in.read()) != -1) {
            char ch = (char) i;
            switch (state) {
                case KEY_OR_END:
                    switch (ch) {
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            break;
                        case '}':
                            return result;
                        case '"':
                            parseQuotedString();
                            name = element.toString();
                            element.setLength(0);
                            state = CompoundState.PAIR_IDENTIFIER;
                            break;
                        default:
                            if (('a' <= ch && 'z' >= ch) || ('A' <= ch && 'Z' >= ch)) {
                                element.append(ch);
                                parseString();
                                name = element.toString();
                                element.setLength(0);
                                state = CompoundState.PAIR_IDENTIFIER;
                            } else {
                                throw new MojangsonParseException("Expected [a-zA-Z{] but got: " + ch);
                            }
                            break;
                    }
                    break;
                case PAIR_IDENTIFIER:
                    switch (ch) {
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            break;
                        case ':':
                            state = CompoundState.VALUE;
                            break;
                        default:
                            throw new MojangsonParseException("Expected [:] but got: " + ch);
                    }
                    break;
                case VALUE:
                    switch (ch) {
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            break;
                        case '"':
                            parseQuotedString();
                            result.setString(name, element.toString());
                            element.setLength(0);
                            state = CompoundState.PAIR_SEPARATOR_OR_END;
                            break;
                        case '<':
                            result.setByteArray(name, parseByteArray());
                            state = CompoundState.PAIR_SEPARATOR_OR_END;
                            break;
                        case '«':
                            result.setIntArray(name, parseIntList());
                            state = CompoundState.PAIR_SEPARATOR_OR_END;
                            break;
                        case '[':
                            result.setList(name, parseList());
                            state = CompoundState.PAIR_SEPARATOR_OR_END;
                            break;
                        case '{':
                            result.setCompound(name, parseCompound());
                            state = CompoundState.PAIR_SEPARATOR_OR_END;
                            break;
                        case '-':
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            in.unread(ch);
                            // XXX
                            parseNumericValue(new CompoundNumericResultHandler(name, result));
                            state = CompoundState.PAIR_SEPARATOR_OR_END;
                            break;
                        default:
                            if (('a' <= ch && 'z' >= ch) || ('A' <= ch && 'Z' >= ch)) {
                                element.append(ch);
                                parseString();
                                result.setString(name, element.toString());
                                element.setLength(0);
                                state = CompoundState.PAIR_SEPARATOR_OR_END;
                            } else {
                                throw new MojangsonParseException("Expected value got: "+ch);
                            }
                            break;
                    }
                    break;
                case PAIR_SEPARATOR_OR_END:
                    switch (ch) {
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            break;
                        case ',':
                            state = CompoundState.KEY_OR_END;
                            break;
                        case '}':
                            return result;
                        default:
                            throw new MojangsonParseException("Expected [,}] but got: " + ch);
                    }
                    break;
            }
        }
        throw new MojangsonParseException("Unexpected EOF in compound");
    }

    private enum ListState {
        VALUE_OR_END,
        SEPARATOR_OR_END;
    }

    private ListTag constructList(TagType tag, List<? extends Tag> data) {
        if (tag == null) {
            return new ListTag(TagType.END, null);
        } else {
            return new ListTag(tag, data.toArray(tag.newArray(data.size())));
        }
    }

    private ListTag parseList() throws IOException {
        List<Tag> listData = new ArrayList<Tag>();
        TagType type = null;
        ListState state = ListState.VALUE_OR_END;
        int i;
        while ((i = in.read()) != -1) {
            char ch = (char) i;
            switch (state) {
                case VALUE_OR_END:
                    switch (ch) {
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            break;
                        case ']':
                            return constructList(type, listData);
                        case '"':
                            if (type == null) {
                                type = TagType.STRING;
                            } else if (type != TagType.STRING) {
                                throw new MojangsonParseException("Expected list value of type: " + type + " got: " + TagType.STRING);
                            }
                            parseQuotedString();
                            listData.add(new StringTag(element.toString()));
                            element.setLength(0);
                            state = ListState.SEPARATOR_OR_END;
                            break;
                        case '<':
                            if (type == null) {
                                type = TagType.BYTE_ARRAY;
                            } else if(type != TagType.BYTE_ARRAY) {
                                throw new MojangsonParseException("Expected list value of type: " + type + " got: " + TagType.BYTE_ARRAY);
                            }
                            listData.add(new ByteArrayTag(parseByteArray()));
                            state = ListState.SEPARATOR_OR_END;
                            break;
                        case '«':
                            if (type == null) {
                                type = TagType.INT_ARRAY;
                            } else if(type != TagType.INT_ARRAY) {
                                throw new MojangsonParseException("Expected list value of type: " + type + " got: " + TagType.INT_ARRAY);
                            }
                            listData.add(new IntArrayTag(parseIntList()));
                            state = ListState.SEPARATOR_OR_END;
                            break;
                        case '[':
                            if (type == null) {
                                type = TagType.LIST;
                            } else if(type != TagType.LIST) {
                                throw new MojangsonParseException("Expected list value of type: " + type + " got: " + TagType.LIST);
                            }
                            listData.add(parseList());
                            state = ListState.SEPARATOR_OR_END;
                            break;
                        case '{':
                            if (type == null) {
                                type = TagType.COMPOUND;
                            } else if(type != TagType.COMPOUND) {
                                throw new MojangsonParseException("Expected list value of type: " + type + " got: " + TagType.COMPOUND);
                            }
                            listData.add(parseCompound());
                            state = ListState.SEPARATOR_OR_END;
                            break;
                        case '-':
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            in.unread(ch);
                            // XXX: Arse.
                            type = parseNumericValue(type, new ListNumericResultHandler(listData));
                            state = ListState.SEPARATOR_OR_END;
                            break;
                        default:
                            if (('a' <= ch && 'z' >= ch) || ('A' <= ch && 'Z' >= ch)) {
                                if (type == null) {
                                    type = TagType.STRING;
                                } else if (type != TagType.STRING) {
                                    throw new MojangsonParseException("Expected list value of type: " + type + " got: " + TagType.STRING);
                                }
                                parseString();
                                listData.add(new StringTag(element.toString()));
                                element.setLength(0);
                                state = ListState.SEPARATOR_OR_END;
                            } else {
                                throw new MojangsonParseException("Expected value got: "+ch);
                            }
                            break;
                    }
                    break;
                case SEPARATOR_OR_END:
                    switch (ch) {
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            break;
                        case ',':
                            state = ListState.VALUE_OR_END;
                            break;
                        case ']':
                            return constructList(type, listData);
                        default:
                            throw new MojangsonParseException("Expected [,\\]] but got: " + ch);
                    }
                    break;
            }
        }
        throw new MojangsonParseException("Unexpected EOF in list");
    }

    private enum ByteArrayState {
        WS_NUMBER_END,
        NUMBER,
        SEPARATOR_OR_END;
    }

    private byte[] toByteArray(List<Byte> data) {
        byte[] result = new byte[data.size()];
        for (int i = 0; i < data.size(); i++) {
            result[i] = data.get(i);
        }
        return result;
    }
    
    private byte[] parseByteArray() throws IOException {
        List<Byte> data = new ArrayList<Byte>(); // TODO Use a native ByteList impl
        ByteArrayState state = ByteArrayState.WS_NUMBER_END;
        int i;
        while ((i = in.read()) != -1) {
            char ch = (char) i;
            switch (state) {
                case WS_NUMBER_END:
                    switch (ch) {
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            break;
                        case '>':
                            return toByteArray(data);
                        case '0':
                            data.add((byte)0);
                            state = ByteArrayState.SEPARATOR_OR_END;
                            break;
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            element.append(ch);
                            state = ByteArrayState.NUMBER;
                            break;
                        default:
                            throw new MojangsonParseException("Expected [1-9] got: " + ch);
                    }
                    break;
                case NUMBER:
                    if ('0' <= ch && '9' >= ch) {
                        element.append(ch);
                    } else {
                        in.unread(ch);
                        try {
                            int num = Integer.parseInt(element.toString());
                            if (num >= 255) {
                                throw new MojangsonParseException("Byte expected, got: " + element);
                            }
                            data.add((byte)num);
                        } catch (NumberFormatException e) {
                            throw new MojangsonParseException("Byte expected, got: " + element);
                        }
                        element.setLength(0);
                        state = ByteArrayState.SEPARATOR_OR_END;
                    }
                    break;
                case SEPARATOR_OR_END:
                    switch (ch) {
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            break;
                        case ',':
                            state = ByteArrayState.WS_NUMBER_END;
                            break;
                        case '>':
                            return toByteArray(data);
                        default:
                            throw new IOException("Expected [,>] got: " + ch);
                    }
                    break;
            }
        }
        throw new MojangsonParseException("Unexpected EOF in byte array");
    }

    private enum IntArrayState {
        WS_MINUS_NUMBER_END,
        FIRST_NUMBER,
        NUMBER,
        SEPARATOR_OR_END;
    }

    private int[] toIntArray(List<Integer> data) {
        int[] result = new int[data.size()];
        for (int i = 0; i < data.size(); i++) {
            result[i] = data.get(i);
        }
        return result;
    }

    private int[] parseIntList() throws IOException {
        List<Integer> data = new ArrayList<Integer>(); // TODO: Use a native int list implementation.
        IntArrayState state = IntArrayState.WS_MINUS_NUMBER_END;
        int i;
        while ((i = in.read()) != -1) {
            char ch = (char) i;
            switch (state) {
                case WS_MINUS_NUMBER_END:
                    switch (ch) {
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            break;
                        case '»':
                            return toIntArray(data);
                        case '-':
                            element.append(ch);
                            state = IntArrayState.FIRST_NUMBER;
                            break;
                        case '0':
                            data.add(0);
                            state = IntArrayState.SEPARATOR_OR_END;
                            break;
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            element.append(ch);
                            state = IntArrayState.NUMBER;
                            break;
                        default:
                            throw new MojangsonParseException("Expected [1-9] got: " + ch);
                    }
                    break;
                case FIRST_NUMBER:
                    switch (ch) {
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            element.append(ch);
                            state = IntArrayState.NUMBER;
                            break;
                        default:
                            throw new MojangsonParseException("Expected [1-9] got: " + ch);
                    }
                case NUMBER:
                    if ('0' <= ch && '9' >= ch) {
                        element.append(ch);
                    } else {
                        in.unread(ch);
                        try {
                            data.add(Integer.parseInt(element.toString()));
                        } catch (NumberFormatException e) {
                            throw new MojangsonParseException("Byte expected, got: " + element);
                        }
                        element.setLength(0);
                        state = IntArrayState.SEPARATOR_OR_END;
                    }
                    break;
                case SEPARATOR_OR_END:
                    switch (ch) {
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            break;
                        case ',':
                            state = IntArrayState.WS_MINUS_NUMBER_END;
                            break;
                        case '»':
                            return toIntArray(data);
                        default:
                            throw new IOException("Expected [,»] got: " + ch);
                    }
                    break;
            }
        }
        throw new MojangsonParseException("Unexpected EOF in int array");
    }

    private enum NumericValueState {
        MINUS_NUMBER,
        NUMBER,
        NUMBER_DECIMAL_IDENTIFIER,
        NUMBER_IDENTIFIER,
    }

    // Return the tag type found, lists can only contain a single
    // tag type.
    private TagType parseNumericValue(NumericResultHandler handler) throws IOException {
        return parseNumericValue(null, handler);
    }

    private TagType parseNumericValue(TagType expected, NumericResultHandler handler) throws IOException {
        // TODO: +-Infinity, NaN, Exponents.
        NumericValueState state = NumericValueState.NUMBER_DECIMAL_IDENTIFIER;
        int i;
        while ((i = in.read()) != -1) {
            char ch = (char) i;
            switch (state) {
                case MINUS_NUMBER:
                    switch (ch) {
                        case '-':
                            element.append(ch);
                            state = NumericValueState.NUMBER;
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            element.append(ch);
                            state = NumericValueState.NUMBER_DECIMAL_IDENTIFIER;
                            break;
                    }
                    break;
                case NUMBER:
                    switch (ch) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            element.append(ch);
                            state = NumericValueState.NUMBER_DECIMAL_IDENTIFIER;
                    }
                case NUMBER_DECIMAL_IDENTIFIER:
                    switch (ch) {
                        case '.':
                            element.append(ch);
                            state = NumericValueState.NUMBER_IDENTIFIER;
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            element.append(ch);
                            break;
                        case 'b':
                        case 'B':
                            if (expected != null && expected != TagType.BYTE) {
                                throw new MojangsonParseException("Expected " + expected + " got " + TagType.BYTE);
                            }
                            try {
                                handler.value(Byte.parseByte(element.toString()));
                                element.setLength(0);
                                return TagType.BYTE;
                            } catch (NumberFormatException ex) {
                                throw new MojangsonParseException("Expected byte, got: " + element.toString() + ch);
                            }
                        case 's':
                        case 'S':
                            if (expected != null && expected != TagType.SHORT) {
                                throw new MojangsonParseException("Expected " + expected + " got " + TagType.SHORT);
                            }
                            try {
                                handler.value(Short.parseShort(element.toString()));
                                element.setLength(0);
                                return TagType.SHORT;
                            } catch (NumberFormatException ex) {
                                throw new MojangsonParseException("Expected short, got: " + element.toString() + ch);
                            }
                        case 'l':
                        case 'L':
                            if (expected != null && expected != TagType.LONG) {
                                throw new MojangsonParseException("Expected " + expected + " got " + TagType.LONG);
                            }
                            try {
                                handler.value(Long.parseLong(element.toString()));
                                element.setLength(0);
                                return TagType.LONG;
                            } catch (NumberFormatException ex) {
                                throw new MojangsonParseException("Expected long, got: " + element.toString() + ch);
                            }
                        case 'f':
                        case 'F':
                            if (expected != null && expected != TagType.FLOAT) {
                                throw new MojangsonParseException("Expected " + expected + " got " + TagType.FLOAT);
                            }
                            try {
                                handler.value(Float.parseFloat(element.toString()));
                                element.setLength(0);
                                return TagType.FLOAT;
                            } catch (NumberFormatException ex) {
                                throw new MojangsonParseException("Expected float, got: " + element.toString() + ch);
                            }
                        case 'd':
                        case 'D':
                            if (expected != null && expected != TagType.DOUBLE) {
                                throw new MojangsonParseException("Expected " + expected + " got " + TagType.DOUBLE);
                            }
                            try {
                                handler.value(Double.parseDouble(element.toString()));
                                element.setLength(0);
                                return TagType.DOUBLE;
                            } catch (NumberFormatException ex) {
                                throw new MojangsonParseException("Expected double, got: " + element.toString() + ch);
                            }
                        case '}':
                        case ',':
                        case ']':
                            in.unread(ch);
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            if (expected != null && expected != TagType.INT) {
                                throw new MojangsonParseException("Expected " + expected + " got " + TagType.INT);
                            }
                            try {
                                handler.value(Integer.parseInt(element.toString()));
                                element.setLength(0);
                                return TagType.INT;
                            } catch (NumberFormatException ex) {
                                throw new MojangsonParseException("Expected double, got: " + element.toString() + ch);
                            }
                    }
                    break;
                case NUMBER_IDENTIFIER:
                    switch (ch) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            element.append(ch);
                            break;
                        case 'f':
                        case 'F':
                            if (expected != null && expected != TagType.FLOAT) {
                                throw new MojangsonParseException("Expected " + expected + " got " + TagType.FLOAT);
                            }
                            try {
                                handler.value(Float.parseFloat(element.toString()));
                                element.setLength(0);
                                return TagType.FLOAT;
                            } catch (NumberFormatException ex) {
                                throw new MojangsonParseException("Expected float, got: " + element.toString() + ch);
                            }
                        case '}':
                        case ',':
                        case ']':
                            in.unread(ch);
                        case 'd':
                        case 'D':
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            if (expected != null && expected != TagType.DOUBLE) {
                                throw new MojangsonParseException("Expected " + expected + " got " + TagType.DOUBLE);
                            }
                            try {
                                handler.value(Double.parseDouble(element.toString()));
                                element.setLength(0);
                                return TagType.DOUBLE;
                            } catch (NumberFormatException ex) {
                                throw new MojangsonParseException("Expected double, got: " + element.toString() + ch);
                            }
                    }
                    break;
            }
        }
        throw new MojangsonParseException("Unexpected EOF in numeric value");
    }

    private static class ListNumericResultHandler implements NumericResultHandler {
        private List<Tag> data;

        public ListNumericResultHandler(List<Tag> data) {
            this.data = data;
        }

        @Override
        public void value(byte b) {
            data.add(new ByteTag(b));
        }

        @Override
        public void value(short s) {
            data.add(new ShortTag(s));
        }

        @Override
        public void value(int i) {
            data.add(new IntTag(i));
        }

        @Override
        public void value(long l) {
            data.add(new LongTag(l));
        }

        @Override
        public void value(float f) {
            data.add(new FloatTag(f));
        }

        @Override
        public void value(double d) {
            data.add(new DoubleTag(d));
        }
    }

    private static class CompoundNumericResultHandler implements NumericResultHandler {
        private final String key;
        private final CompoundTag tag;

        public CompoundNumericResultHandler(String key, CompoundTag tag) {
            this.key = key;
            this.tag = tag;
        }

        @Override
        public void value(byte b) {
            tag.setByte(key, b);
        }

        @Override
        public void value(short s) {
            tag.setShort(key, s);
        }

        @Override
        public void value(int i) {
            tag.setInt(key, i);
        }

        @Override
        public void value(long l) {
            tag.setLong(key, l);
        }

        @Override
        public void value(float f) {
            tag.setFloat(key, f);
        }

        @Override
        public void value(double d) {
            tag.setDouble(key, d);
        }
    }

    private static interface NumericResultHandler {
        public void value(byte b);
        public void value(short s);
        public void value(int i);
        public void value(long l);
        public void value(float f);
        public void value(double d);
    }
}