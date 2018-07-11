package com.mmall.util;

import java.math.BigDecimal;

public class BigDecimalUtil {
    private BigDecimalUtil(){

    }


    public static BigDecimal add(Double v1, Double v2){
        if(v1 == null){
            v1 =new Double(0);
        }
        if(v2 == null){
            v2 =new Double(0);
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }

    public static BigDecimal sub(Double v1,Double v2){
        if(v1 == null){
            v1 =new Double(0);
        }
        if(v2 == null){
            v2 =new Double(0);
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }


    public static BigDecimal mul(Double v1,Double v2){
        if(v1 == null){
            v1 =new Double(0);
        }
        if(v2 == null){
            v2 =new Double(0);
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }

    public static BigDecimal div(Double v1,Double v2){
        if(v1 == null){
            v1 =new Double(0);
        }
        if(v2 == null){
            v2 =new Double(0);
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);//四舍五入,保留2位小数

        //除不尽的情况
    }
}
