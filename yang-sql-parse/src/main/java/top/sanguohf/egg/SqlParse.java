package top.sanguohf.egg;


import top.sanguohf.egg.constant.DbType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface SqlParse {

    default String toSql() {return "";}
    default String toSql(DbType dbType) {return "";}

    /**
     * @deprecated 已被废弃，暂时无使用
     * */
    default PreparedStatement toSql(Connection connection) throws SQLException {return null;}
    /**
     * @deprecated 已被废弃，暂时无使用
     * */
    default PreparedStatement toSql(Connection connection,DbType dbType) throws SQLException {return null;}

    default String sqlOne(boolean isPrepare){return "";}

    default void addValue(List list){}
}
