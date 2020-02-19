package top.sanguohf.egg.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MainTable {
    String tableAlias() default "";
    //默认是包含全部的列，优先级大于excludeColumns参数
    String[] includeColumns() default {};
    //默认是不排除任何一个列
    String[] excludeColumns() default {};
}
