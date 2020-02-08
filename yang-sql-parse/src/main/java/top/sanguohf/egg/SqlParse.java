package top.sanguohf.egg;


import top.sanguohf.egg.constant.DbType;

public interface SqlParse {

    public String toSql();

    public String toSql(DbType dbType);
}
