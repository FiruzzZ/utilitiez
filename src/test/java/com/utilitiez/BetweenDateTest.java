package com.utilitiez;

import java.time.Month;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utilities.general.UTIL;

/**
 *
 * @author FiruzzZ
 */
public class BetweenDateTest {

    @Test
    public void sameFrom() {
        Date d = UTIL.customDate(2000, Month.JANUARY, 1);
        Date h = UTIL.customDate(2000, Month.JANUARY, 20);
        Date candi = UTIL.customDate(2000, Month.JANUARY, 1);
        Assertions.assertTrue(UTIL.between(candi, d, h, true));
    }

    @Test
    public void sameTo() {
        Date d = UTIL.customDate(2000, Month.JANUARY, 1);
        Date h = UTIL.customDate(2000, Month.JANUARY, 20);
        Date candi = UTIL.customDate(2000, Month.JANUARY, 20);
        Assertions.assertTrue(UTIL.between(candi, d, h, true));
    }

    @Test
    public void allSame() {
        Date d = UTIL.customDate(2000, Month.JANUARY, 1);
        Date h = UTIL.customDate(2000, Month.JANUARY, 1);
        Date candi = UTIL.customDate(2000, Month.JANUARY, 1);
        Assertions.assertTrue(UTIL.between(candi, d, h, true));
    }

    @Test
    public void withoutTo() {
        Date d = UTIL.customDate(2000, Month.JANUARY, 1);
        Date candi = UTIL.customDate(2000, Month.JANUARY, 1);
        Assertions.assertTrue(UTIL.between(candi, d, null, true));
    }

    @Test
    public void before() {
        Date d = UTIL.customDate(2000, Month.JANUARY, 1);
        Date h = UTIL.customDate(2000, Month.JANUARY, 30);
        Date candi = UTIL.customDate(1999, Month.JANUARY, 1);
        Assertions.assertFalse(UTIL.between(candi, d, h, true));
    }

    @Test
    public void beforeWithoutTo() {
        Date d = UTIL.customDate(2000, Month.JANUARY, 1);
        Date h = null;
        Date candi = UTIL.customDate(1999, Month.JANUARY, 1);
        Assertions.assertFalse(UTIL.between(candi, d, h, true));
    }

    @Test
    public void after() {
        Date d = UTIL.customDate(2000, Month.JANUARY, 1);
        Date h = UTIL.customDate(2000, Month.JANUARY, 30);
        Date candi = UTIL.customDate(2001, Month.JANUARY, 1);
        Assertions.assertFalse(UTIL.between(candi, d, h, true));
    }
}
