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
public class EntityInsertSql extends AbstractEntityJoinTable {
    private String tableName;
    private String tableAlias;
    private List<EntityInsert> insertList;

    public String toSql() {

        return sqlOne(false);
    }

    public String toSql(DbType dbType) {
        return toSql();
    }

    @Override
    public PreparedStatement toSql(Connection connection) throws SQLException {
        String sqlOne = sqlOne(true);
        return EntityParseUtil.setValueForStatement(insertList,sqlOne,connection);
    }

    @Override
    public PreparedStatement toSql(Connection connection, DbType dbType) throws SQLException {
        return toSql(connection);
    }

    @Override
    public String sqlOne(boolean isPrepare){
        StringBuilder builder = new StringBuilder();
        builder.append(" insert into ")
                .append(tableName).append("(");
        for(int i =0;i<insertList.size();i++){
            builder.append(insertList.get(i).getColumn());
            if(i!=insertList.size()-1)
                builder.append(",");
        }
        builder.append(")").append(" values(");
        for(int i =0;i<insertList.size();i++){
            Object value=insertList.get(i).getValue();
            if(!isPrepare) {
                if (value instanceof String)
                    builder.append("'");
                builder.append(value);
                if (value instanceof String)
                    builder.append("'");
            }else builder.append("?");
            if(i!=insertList.size()-1)
                builder.append(",");
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public void addValue(List list) {
        for(EntityInsert insert:insertList){
            insert.addValue(list);
        }
    }
}
