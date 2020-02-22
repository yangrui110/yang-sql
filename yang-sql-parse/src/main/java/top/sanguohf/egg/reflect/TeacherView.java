package top.sanguohf.egg.reflect;

import top.sanguohf.egg.annotation.MainTable;
import top.sanguohf.egg.annotation.ReferTable;
import top.sanguohf.egg.annotation.ViewTable;

@ViewTable
public class TeacherView {

    @MainTable(tableAlias = "oneOs")
    private Teacher teacher;

    @ReferTable(tableAlias = "op",relation = "inner join",condition = "oneOs.teacherId = op.id")
    private User user;
}
