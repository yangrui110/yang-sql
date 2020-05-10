package top.sanguohf.top.bootcon.util;

import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ClassUtils;

import java.io.IOException;

public class ClassInfoUtil {

    public static String getPackageByRelativeName(String name, String[] basePackages) throws IOException {
        for(String basePackage: basePackages) {
            String resourcePath = ClassUtils.convertClassNameToResourcePath(new StandardEnvironment().resolveRequiredPlaceholders(basePackage));
            //Class<?> user = ClassUtils.forName("User", ClassUtils.getDefaultClassLoader());
            String packageSearchPath = "classpath*:" + resourcePath + '/' + "**/*.class";
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(packageSearchPath);
            for (Resource resource : resources) {
                if(resource.getURL().toString().endsWith(name+".class")){
                    String all = resource.getURL().toString().replaceAll("/", ".");
                    int start = all.indexOf(basePackage);
                    int end = all.lastIndexOf(".class");
                    return all.substring(start,end);
                }
            }
        }
        return null;
    }

    public static Class getClassByRelativeName(String name,String[] basePackages) throws IOException, ClassNotFoundException {
        String des = getPackageByRelativeName(name, basePackages);
        return Class.forName(des);
    }
}
