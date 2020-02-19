package top.sanguohf.egg.reflect;

import lombok.Data;
import top.sanguohf.egg.annotation.*;

@Data
@ViewTable
public class UserClassesView {

    @Condition(column = "teacherId",value = "1")
    @MainTable(tableAlias = "userOne")
    private TeacherView teacherView;

    @ReferTable(tableAlias = "aliasClass",relation = "left join",condition = "userOne.id = aliasClass.classesId and userOne.userName = aliasClass.name")
    private Classes classes;

    @OrderBys({
            @OrderBy(column = "teacherId",direct = "desc")
    })
    @Conditions({
            @Condition(column = "teacherId",value = "1"),
            @Condition(column = "teacherName",value = "8")
    })
    @ReferTable(tableAlias = "teacher",relation = "left join",condition = "userOne.id = teacher.teacherId",includeColumns = {"teacherName"})
    private Teacher teacher;

}
