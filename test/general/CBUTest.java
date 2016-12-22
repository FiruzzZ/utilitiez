/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utilities.general.UTIL;

/**
 *
 * @author JoseLuis
 */
public class CBUTest {

    public CBUTest() {
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

    @Test(expected = IllegalArgumentException.class)
    public void tooShort() {
        UTIL.VALIDAR_CBU("123412341");
    }
    @Test(expected = IllegalArgumentException.class)
    public void tooLong() {
        UTIL.VALIDAR_CBU("12341234123412341234123");
    }

    @Test(expected = NumberFormatException.class)
    public void nonNumeric() {
        UTIL.VALIDAR_CBU("123412341234123412341A");
    }

    @Test
    public void validOne() {
        UTIL.VALIDAR_CBU("2850001040094658598428");
    }
}
