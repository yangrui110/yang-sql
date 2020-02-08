package top.sanguohf.top.bootcon.annotation;

import top.sanguohf.top.bootcon.config.ScanEntityRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({ScanEntityRegister.class})
@Documented
public @interface ScanEntity {

    String[] value() default {};

    String[] basePackages() default {};

}
