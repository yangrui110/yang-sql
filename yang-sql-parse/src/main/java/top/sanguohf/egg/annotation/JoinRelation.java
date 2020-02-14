package top.sanguohf.egg.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JoinRelation {
    String tableAlias() default "";
    String referTableAlias() default "";
    String relation() default "left join";
    //默认是包含全部的列
    String[] includeColumns() default {};
    //默认是不排除任何一个列
    String[] excludeColumns() default {};

}
