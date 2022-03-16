package com.base.framwork;

import com.base.utils.MathUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void mathUtils() {
        int a = 352;
        int b = 288;
        int divisor = MathUtils.getGreastCommonDivisor(a, b);
        System.out.println(divisor);
        System.out.println(a / divisor);
        System.out.println(b / divisor);

    }


}