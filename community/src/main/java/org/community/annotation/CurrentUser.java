package org.community.annotation;

import java.lang.annotation.*;


// HttpServletRequest의 JWT를 근거로 request 보낸 유저엔티티를 조회하여 리턴
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {

}
