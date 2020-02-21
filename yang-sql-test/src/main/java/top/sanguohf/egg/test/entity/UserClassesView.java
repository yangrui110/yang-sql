package top.sanguohf.egg.test.entity;

import lombok.Data;
import top.sanguohf.egg.annotation.MainTable;
import top.sanguohf.egg.annotation.ReferTable;
import top.sanguohf.egg.annotation.ViewTable;

@Data
@ViewTable
public class UserClassesView {

    @MainTable(tableAlias = "userOne")
    private User user;

    @ReferTable(tableAlias = "aliasClass",relation = "left join",condition = "userOne.id = aliasClass.age")
    private Classes classes;

    @ReferTable(tableAlias = "aliasTeacher",relation = "left join",condition = "aliasTeacher.teacherId > [Classes]")
    private Teacher teacher;
}
