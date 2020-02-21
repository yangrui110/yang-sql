package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.constant.DbType;

import java.util.List;

@Data
public class EntityConditionDom implements EntityCondition {

    private EntityCondition left;
    private String relation;
    private EntityCondition right;

    public String toSql() {
        return sqlOne(false);
    }

    public String toSql(DbType dbType) {
        return toSql();
    }

    @Override
    public String sqlOne(boolean isPrepare) {
        StringBuilder builder = new StringBuilder();
        builder.append(" ").append(left.sqlOne(isPrepare)).append(" ").append(relation);
        builder.append(" ").append(right.sqlOne(isPrepare)).append(" ");
        return builder.toString();
    }

    @Override
    public void addValue(List list) {
        left.addValue(list);
        right.addValue(list);
    }
}
