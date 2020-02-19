package top.sanguohf.egg.annotation;

import top.sanguohf.egg.constant.ValueType;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Conditions.class)
public @interface Condition {
    String value();
    String type() default ValueType.STRING;
    String relation() default "=";
    String column() default "";
}
