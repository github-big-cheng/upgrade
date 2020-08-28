package com.dounion.server.core.helper;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;

public class SpringApp {

    private SpringApp(){
    }

    private ApplicationContext context;

    public static void init(ApplicationContext context) {
        instance.context = context;
    }

    private static SpringApp instance = new SpringApp();

    public static SpringApp getInstance(){
        return instance;
    }

    public <T> T getBean(String beanName){
        return (T) context.getBean(beanName);
    }

    public <T> T getBean(Class clazz){
        return (T) context.getBean(clazz);
    }

    public <T> T getBean(String beanName, Class<T> clz){
        return context.getBean(beanName, clz);
    }

    /**
     * 根据接口类型获取所有实例 beanName
     * @param interfaceClz
     * @return
     */
    public String[] getBeanNamesByInterfaceType(Class interfaceClz){
        return this.context.getBeanNamesForType(interfaceClz);
    }

    /**
     * 根据接口类型获取所有实例 beanName
     * @param interfaceClz
     * @return
     */
    public <T> Map<String, T> getObjectByInterfaceType(Class<T> interfaceClz){
        return this.context.getBeansOfType(interfaceClz);
    }


    /**
     * 根据注解类型获取所有实例 beanName
     * @param annotationClz
     * @return
     */
    public String[] getBeanNamesByAnnotationType(Class<? extends Annotation> annotationClz){
        return this.context.getBeanNamesForAnnotation(annotationClz);
    }

    /**
     * 根据注解类型获取所有实例
     * @param annotationClz
     * @return
     */
    public <T> Map<String, T> getObjectByAnnotationType(Class<? extends Annotation> annotationClz){
        return (Map<String, T>) this.context.getBeansWithAnnotation(annotationClz);
    }

    /**
     *
     * @param location
     * @return
     */
    public Resource getResource(String location){
        if(this.context == null){
            return null;
        }
        return this.context.getResource(location);
    }

    /**
     *
     * @param locationPattern
     * @return
     */
    public Resource[] getResources(String locationPattern) throws IOException {
        if(this.context == null){
            return null;
        }
        return this.context.getResources(locationPattern);
    }

}
