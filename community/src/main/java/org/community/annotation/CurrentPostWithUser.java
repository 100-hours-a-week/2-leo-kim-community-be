package org.community.annotation;

import java.lang.annotation.*;

// lazy initialization 문제를 해결하기 위한
// fetch join을 적용시킨 PostEntity
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentPostWithUser {

}
