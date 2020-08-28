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

}
