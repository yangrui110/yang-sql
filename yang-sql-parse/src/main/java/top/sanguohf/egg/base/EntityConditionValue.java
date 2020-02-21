package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.StringUtils;

import java.util.List;

@Data
public class EntityConditionValue implements EntityCondition {

    private Object column;

    private String tableAlias;

    public String toSql() {
        return sqlOne(false);
    }

    public String toSql(DbType dbType) {
        return toSql();
    }

    @Override
    public String sqlOne(boolean isPrepare) {
        StringBuilder builder = new StringBuilder();
        if (!StringUtils.isEmpty(tableAlias))
            builder.append(tableAlias).append(".");
        if (!isPrepare){
            if (column instanceof String)
                builder.append("'");
            builder.append(column);
            if (column instanceof String)
                builder.append("'");
        }else builder.append("?");
        return builder.toString();
    }

    @Override
    public void addValue(List list) {
        list.add(column);
    }
}
