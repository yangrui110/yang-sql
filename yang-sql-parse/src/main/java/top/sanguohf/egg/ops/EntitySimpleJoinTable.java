package top.sanguohf.egg.ops;

import lombok.Data;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.StringUtils;

@Data
public class EntitySimpleJoinTable extends AbstractEntityJoinTable {

    private EntityJoinTable tableName;

    public String toSql() {
        StringBuilder builder=new StringBuilder();
        if(!StringUtils.isEmpty(tableAlias))
            builder.append(tableAlias).append(".");
        builder.append(tableName.toSql());
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
