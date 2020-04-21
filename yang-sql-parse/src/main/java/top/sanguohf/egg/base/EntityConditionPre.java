package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.constant.DbType;

/**
 * 预先定义的查询条件，直接字符串拼接返回
 * */
@Data
public class EntityConditionPre implements EntityCondition {

    private String column;
    private String tableAlias;
    private String relation;
    private Object value;

    @Override
    public String sqlOne(boolean isPrepare) {
        StringBuilder builder = new StringBuilder();
        builder.append(column).append(" ");
        builder.append(relation==null?"=":relation);
        if(value instanceof String) {
            builder.append(" '").append(value).append("'");
        }else builder.append(" ").append(value);
        return builder.toString();
    }

    @Override
    public String toSql() {
        return null;
    }

    @Override
    public String toSql(DbType dbType) {
        return null;
    }
}
