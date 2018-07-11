package com.mmall.test;

import org.junit.Test;

import java.math.BigDecimal;

public class BigDecimalTest {

    @Test
    public void test(){
        System.out.println(0.1+1.2);
        System.out.println(0.1-1.2);
        System.out.println(0.1*1.2);
        System.out.println(550.3/100);
    }

    @Test
    public void test1(){
        BigDecimal b1 = new BigDecimal(0.1);
        BigDecimal b2 = new BigDecimal(1.2);
        System.out.println(b1.add(b2));

    }

    @Test
    public void test2(){
        BigDecimal b1 = new BigDecimal("0.1");
        BigDecimal b2 = new BigDecimal("1.2");
        System.out.println(b1.add(b2));

    }
}
