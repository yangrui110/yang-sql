package top.sanguohf.egg.test.entity;

import lombok.Data;
import top.sanguohf.egg.annotation.Field;
import top.sanguohf.egg.annotation.IgnoreSelectReback;

@Data
public class Classes {

    @IgnoreSelectReback
    private String classesId;

    @IgnoreSelectReback
    @Field(alias = "classesName")
    private String name;

    private Integer age;
}
