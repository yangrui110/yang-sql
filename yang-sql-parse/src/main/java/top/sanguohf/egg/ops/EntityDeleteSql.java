package top.sanguohf.egg.ops;

import lombok.Data;
import top.sanguohf.egg.base.EntityInsert;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.EntityParseUtil;

import java.util.List;

@Data
public class EntityDeleteSql extends AbstractEntityJoinTable {
    private String tableName;
    private String tableAlias;
    private List<EntityInsert> wheres;
    public String toSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("delete from ").append(tableName).append(" where ").append(EntityParseUtil.listInsertsToString(wheres," and "));
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
