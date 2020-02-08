package top.sanguohf.egg.ops;

import lombok.Data;
import top.sanguohf.egg.base.EntityInsert;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.EntityParseUtil;

import java.util.List;

/**
 * 设置更新为主键更新
 * */
@Data
public class EntityUpdateSql extends AbstractEntityJoinTable {
    private String tableName;
    private String tableAlias;
    private List<EntityInsert> updates;
    private List<EntityInsert> wheres;

    public String toSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("update ").append(tableName).append(" set ");
        builder.append(EntityParseUtil.listInsertsToString(updates,","));
        builder.append(" where ").append(EntityParseUtil.listInsertsToString(wheres," and "));
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
