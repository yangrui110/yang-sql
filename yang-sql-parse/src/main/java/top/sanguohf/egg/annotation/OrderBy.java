package top.sanguohf.egg.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(OrderBys.class)
public @interface OrderBy {
    String direct() default "desc";
    String column() default "";
}
