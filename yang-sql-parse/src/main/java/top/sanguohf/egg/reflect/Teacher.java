package top.sanguohf.egg.reflect;

import lombok.Data;
import top.sanguohf.egg.annotation.OrderBy;

@Data
public class Teacher {

    private String teacherId;

    @OrderBy
    private String teacherName;
}
