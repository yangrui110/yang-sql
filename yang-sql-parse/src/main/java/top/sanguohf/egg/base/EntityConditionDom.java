package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.constant.DbType;

@Data
public class EntityConditionDom implements EntityCondition {

    private EntityCondition left;
    private String relation;
    private EntityCondition right;

    public String toSql() {
        StringBuilder builder = new StringBuilder();
        builder.append(" ").append(left.toSql()).append(" ").append(relation);
        builder.append(" ").append(right.toSql()).append(" ");
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
