package top.sanguohf.egg.constant;

public class SqlConfigProperties {

    private static SqlConfigProperties instance = null;

    public static SqlConfigProperties getInstance(){
        if(instance == null){
            synchronized (SqlConfigProperties.class){
                if(instance == null){
                    instance = new SqlConfigProperties();
                }
            }
        }
        return instance;
    }

    private String[] packages;

    private boolean consoleSql=false;

    private DbType dbType = DbType.MYSQL;

    public void setPackages(String[] packages1) {
        packages = packages1;
    }

    public String[] getPackage(){
        return packages;
    }

    public boolean isConsoleSql() {
        return consoleSql;
    }

    public void setConsoleSql(boolean consoleSql) {
        this.consoleSql = consoleSql;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }
}
