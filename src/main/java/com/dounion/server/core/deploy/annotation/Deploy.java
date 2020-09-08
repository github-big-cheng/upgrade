package com.dounion.server.core.deploy.annotation;

import com.dounion.server.eum.AppTypeEnum;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Deploy {

    /**
     * 应用类型
     * @return
     */
    AppTypeEnum appType();

}
