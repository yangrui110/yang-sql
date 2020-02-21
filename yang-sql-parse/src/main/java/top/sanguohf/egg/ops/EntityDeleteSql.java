package top.sanguohf.egg.ops;

import lombok.Data;
import top.sanguohf.egg.base.EntityInsert;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.EntityParseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Data
public class EntityDeleteSql extends AbstractEntityJoinTable {
    private String tableName;
    private String tableAlias;
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
        return EntityParseUtil.setValueForStatement(wheres,sqlOne,connection);
    }

    @Override
    public PreparedStatement toSql(Connection connection, DbType dbType) throws SQLException {
        return toSql(connection);
    }

    public String one(boolean isPrepare){
        StringBuilder builder = new StringBuilder();
        builder.append("delete from ").append(tableName).append(" where ").append(EntityParseUtil.listInsertsToString(wheres," and ",isPrepare));
        return builder.toString();
    }
}
