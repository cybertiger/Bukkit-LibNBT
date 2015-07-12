/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.nbt;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author antony
 */
public class MojangsonTest {
    
    public MojangsonTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    private void parse(String mojangsonSource) {
        MojangsonLexer lexer = new MojangsonLexer(new ANTLRInputStream(mojangsonSource));
        MojangsonParser parser = new MojangsonParser(new CommonTokenStream(lexer));
        MojangsonParser.ObjectContext object = parser.object();
        
    }

    @Test
    public void test() {
        parse("{}");
        parse("{\"key\": 1b}");
        parse("{\"key\": 1s}");
        parse("{\"key\": 1}");
        parse("{\"key\": 1i}");
        parse("{\"key\": 1l}");
        parse("{\"key\": 1f}");
        parse("{\"key\": 1d}");
        parse("{\"key\": 1.0f}");
        parse("{\"key\": 1.0d}");
        parse("{\"key\": 1.0}");
    }
}