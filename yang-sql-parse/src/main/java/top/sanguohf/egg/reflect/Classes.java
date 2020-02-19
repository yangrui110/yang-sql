package top.sanguohf.egg.reflect;

import lombok.Data;
import top.sanguohf.egg.annotation.Field;

@Data
public class Classes {

    private String classesId;

    @Field(alias = "classesName")
    private String name;

}
