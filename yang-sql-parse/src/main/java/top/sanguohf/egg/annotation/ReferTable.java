package top.sanguohf.egg.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReferTable {
    String tableAlias() default "";
    String relation() default "left join";
    //on后面的表之间的关联条件
    String condition() default "";
    //默认是包含全部的列，优先级大于excludeColumns参数
    String[] includeColumns() default {};
    //默认是不排除任何一个列
    String[] excludeColumns() default {};

}
