package top.sanguohf.egg.reflect;

import lombok.Data;
import top.sanguohf.egg.annotation.*;

@Data
@ViewTable
public class UserClassesView {

    @MainTable(tableAlias = "aliasClass")
    private Classes classes;

    @ReferTable(tableAlias = "userOne", condition = "aliasClass.classesId = userOne.teacherId")
    private TeacherView teacherView;

}
