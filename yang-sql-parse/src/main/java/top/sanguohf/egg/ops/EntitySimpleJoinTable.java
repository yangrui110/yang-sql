package top.sanguohf.egg.ops;

import lombok.Data;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.StringUtils;

@Data
public class EntitySimpleJoinTable extends AbstractEntityJoinTable {

    private String tableName;

    public String toSql() {
        StringBuilder builder=new StringBuilder();
        builder.append(tableName);
        if(!StringUtils.isEmpty(tableAlias))
            builder.append(" ").append(tableAlias);
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
