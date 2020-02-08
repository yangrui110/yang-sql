package top.sanguohf.top.bootcon.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import top.sanguohf.top.bootcon.service.impl.CommonServiceImpl;

@Configuration
@ConditionalOnWebApplication
public class BootCoreConfiguration implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {

        return new String[]{DataBaseTypeInit.class.getName(),CommonServiceImpl.class.getName()};
    }
}
