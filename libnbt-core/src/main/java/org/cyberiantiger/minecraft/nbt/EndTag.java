/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.nbt;

/**
 *
 * @author antony
 */
public class EndTag extends Tag<Void> {

    public static final EndTag VALUE = new EndTag();
    static final TagTuple TUPLE = new TagTuple(null, VALUE);

    private EndTag() {
    }

    @Override
    public Void getValue() {
        return null;
    }

    @Override
    public TagType getType() {
        return TagType.END;
    }
    
}
