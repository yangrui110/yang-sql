package top.sanguohf.egg;


import top.sanguohf.egg.constant.DbType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SqlParse {

    default String toSql() {return "";}
    default String toSql(DbType dbType) {return "";}

    default PreparedStatement toSql(Connection connection) throws SQLException {return null;}

    default PreparedStatement toSql(Connection connection,DbType dbType) throws SQLException {return null;}
}
