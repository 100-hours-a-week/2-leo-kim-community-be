package org.community.annotation;

import java.lang.annotation.*;

// lazy loading을 적용시킨 PostEntity
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentPost {

}
