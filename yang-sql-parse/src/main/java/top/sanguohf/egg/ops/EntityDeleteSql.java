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
       return sqlOne(false);
    }

    public String toSql(DbType dbType) {
        return toSql();
    }

    @Override
    public PreparedStatement toSql(Connection connection) throws SQLException {
        String sqlOne = sqlOne(true);
        return EntityParseUtil.setValueForStatement(wheres,sqlOne,connection);
    }

    @Override
    public PreparedStatement toSql(Connection connection, DbType dbType) throws SQLException {
        return toSql(connection);
    }

    @Override
    public String sqlOne(boolean isPrepare){
        StringBuilder builder = new StringBuilder();
        builder.append("delete from ").append(tableName).append(" where ").append(EntityParseUtil.listInsertsToString(wheres," and ",isPrepare));
        return builder.toString();
    }

    @Override
    public void addValue(List list) {
        for(EntityInsert insert:wheres){
            insert.addValue(list);
        }
    }
}
