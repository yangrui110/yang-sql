package top.sanguohf.egg.ops;

import lombok.Data;
import top.sanguohf.egg.base.EntityCondition;
import top.sanguohf.egg.constant.DbType;

@Data
public class EntityDeleteSql extends AbstractEntityJoinTable {
    private String tableName;
    private String tableAlias;
    private EntityCondition wheres;
    public String toSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("delete from ").append(tableName).append(" where ").append(wheres.toSql());
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
