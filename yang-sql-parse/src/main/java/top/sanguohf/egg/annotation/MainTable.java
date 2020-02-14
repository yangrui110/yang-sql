package top.sanguohf.egg.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MainTable {

    String tableClass() default "";

    String tableAlias() default "";
}
