package top.sanguohf.egg.ops;

import lombok.Data;
import top.sanguohf.egg.base.EntityInsert;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.EntityParseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
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
        return one(false);
    }

    public String toSql(DbType dbType) {
        return toSql();
    }

    @Override
    public PreparedStatement toSql(Connection connection) throws SQLException {
        String sqlOne = one(true);
        LinkedList<EntityInsert> list = new LinkedList<>();
        list.addAll(updates);
        list.addAll(wheres);
        return EntityParseUtil.setValueForStatement(list,sqlOne,connection);
    }

    @Override
    public PreparedStatement toSql(Connection connection, DbType dbType) throws SQLException {
        return toSql(connection);
    }

    public String one(boolean isPrepare){
        StringBuilder builder = new StringBuilder();
        builder.append("update ").append(tableName).append(" set ");
        builder.append(EntityParseUtil.listInsertsToString(updates,",",isPrepare));
        builder.append(" where ").append(EntityParseUtil.listInsertsToString(wheres," and ",isPrepare));
        return builder.toString();
    }
}
