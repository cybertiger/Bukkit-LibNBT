/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.nbt;

import java.io.IOException;
import java.io.StringReader;
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
public class MojangsonParserTest {
    public MojangsonParserTest() {
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

    private void testParse(String s) throws IOException {
        MojangsonParser parser = new MojangsonParser(new StringReader(s));
        TagTuple result = parser.parse();
        System.out.println("Parsed: " + result + " from " + s);
        s = result.toString();
        parser = new MojangsonParser(new StringReader(result.toString()));
        TagTuple result2 = parser.parse();
        System.out.println("Parsed: " + result + " from " + result2);
        assertEquals(result, result2);
    }

    @Test
    public void test() throws IOException {
        testParse("{}");
        testParse("test : {}");
        testParse("\"test\" : {}");
        testParse("\"test\\\"\\\\\\/\\b\\f\\n\\r\\t\\u00a7\" : {}");
        testParse("{ key : value }");
        testParse("{ key : \"value\" }");
        testParse("{ key : 1b }");
        testParse("{ key : 1s }");
        testParse("{ key : 1 }");
        testParse("{ key : 1l }");
        testParse("{ key : 1f }");
        testParse("{ key : 1d }");
        testParse("{ key : <>}");
        testParse("{ key : <1,2,4,5,6 , 7,8 >}");
        testParse("{ key : «»}");
        testParse("{ key : «1,2,4,5,6 , 7,8 »}");
        testParse("{ \"key\" : {} }");
        testParse("{ \"key\" : [] }");
        testParse("{ key : [1b, 2b ,3b]}");
        testParse("{ key : [1s, 2s ,3s]}");
        testParse("{ key : [1, 2 ,3]}");
        testParse("{ key : [1l, 2l ,3l]}");
        testParse("{ key : [1f, 2f ,3f]}");
        testParse("{ key : [1d, 2d ,3d]}");
        testParse("{BlockEntity : { Patterns: [], Base: 8 }}");
    }
}