package com.dounion.server.core.helper;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Map;

public class BeanHelper {

    /**
     * map转bean
     *
     * @param map
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<? extends T> beanClass) throws Exception {

        T t = beanClass.newInstance();
        if (CollectionUtils.isEmpty(map)) {
            return t;
        }

        BeanInfo beanInfo = Introspector.getBeanInfo(t.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();// bean中全部属性

        PropertyDescriptor descriptor;//bean属性描述
        String propertyName;//bean属性名称
        Class fieldType;//bean属性类别
        String paraValueStr;//request对应的属性值字符串
        Object paraValueObj;//request转换为对应类别的属性值
        for (int i = 0; i < propertyDescriptors.length; i++) {
            descriptor = propertyDescriptors[i];
            propertyName = descriptor.getName();
            fieldType = descriptor.getPropertyType();
            paraValueStr = (String) map.get(propertyName);
            if (StringUtils.isEmpty(paraValueStr)) {
                continue;
            }
            if (long.class.equals(fieldType)) {
                paraValueObj = Long.parseLong(paraValueStr.trim());
            } else if (short.class.equals(fieldType)) {
                paraValueObj = Short.parseShort(paraValueStr.trim());
            } else if (int.class.equals(fieldType)) {
                paraValueObj = Integer.parseInt(paraValueStr.trim().replaceAll("^(-?\\d+)\\.[0]+$", "$1"));
            } else {
                paraValueObj = paraValueStr;
            }
            descriptor.getWriteMethod().invoke(t, paraValueObj);
        }
        return t;
    }


    /**
     * bean转map
     *
     * @param obj
     * @return
     */
    public static Map<?, ?> objectToMap(Object obj) {
        if (obj == null)
            return null;
        return new org.apache.commons.beanutils.BeanMap(obj);
    }

}
