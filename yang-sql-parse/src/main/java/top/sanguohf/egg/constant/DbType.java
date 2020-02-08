package top.sanguohf.egg.constant;

public enum  DbType {

    SQL("sql"),
    MYSQL("mysql"),
    HSQL("hsql"),
    ORACLE("oracle"),;

    private String value;

    DbType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
