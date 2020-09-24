package com.dounion.server.core.helper;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class DataHelper {

    /**
     * 获取BigDecimal
     * @param o
     * @return
     */
    public static BigDecimal getBigDecimal(Object o){

        if(o == null){
            return BigDecimal.ZERO;
        }

        if(o instanceof BigDecimal){
            return (BigDecimal) o;
        }

        if(o instanceof Number){
            return new BigDecimal(o.toString());
        }

        if(o instanceof String){
            String s = (String) o;
            if(!isNumber(s)){
                return BigDecimal.ZERO;
            }
            return new BigDecimal((String) o);
        }

        if(o instanceof Character){
            int i = (char) o - '0';
            return new BigDecimal(i);
        }

        if(isNumber(o.toString())){
            return new BigDecimal(o.toString());
        }

        return BigDecimal.ZERO;
    }


    public static BigDecimal subtract(Object o1, Object o2){
        return getBigDecimal(o1).subtract(getBigDecimal(o2));
    }

    public static boolean isNumber(String s){
        Pattern pattern = Pattern.compile("[\\d]+[\\.]?[\\d]*");
        return pattern.matcher(s).matches();
    }


    /**
     * return o1/o2
     * @param o1
     * @param o2
     * @return
     */
    public static BigDecimal divide(Object o1, Object o2, int roundingMode){
        BigDecimal b2 = getBigDecimal(o2);
        if(b2.equals(BigDecimal.ZERO)){
            return BigDecimal.ZERO;
        }
        return getBigDecimal(o1).divide(b2, BigDecimal.ROUND_HALF_UP, roundingMode);
    }


    /**
     * return o1/o2
     * @param o1
     * @param o2
     * @return
     */
    public static BigDecimal divide(Object o1, Object o2){
        return divide(o1, o2, 2);
    }



    /**
     * return percent of o2/o1
     * @param o1 分母
     * @param o2 分子
     * @return
     */
    public static BigDecimal percent(Object o1, Object o2){
        return divide(o2, o1).multiply(new BigDecimal(100));
    }


    /**
     * return o1 + o2
     * @param o1
     * @param o2
     * @return
     */
    public static BigDecimal add(Object o1, Object o2) {
        return getBigDecimal(o1).add(getBigDecimal(o2));
    }
}
