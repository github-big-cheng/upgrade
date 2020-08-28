package com.dounion.server.core.request.annotation;

import com.dounion.server.eum.ResponseTypeEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseType {

    ResponseTypeEnum value();

}
