package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.SqlParse;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.StringUtils;

@Data
public class EntityColumn implements SqlParse {
    private String orignColumn;
    private String aliasColumn;
    private String tableAlias;
    //标识属性
    private String fieldName;

    public String toSql() {
        StringBuilder builder = new StringBuilder();
        if(!StringUtils.isEmpty(tableAlias))
            builder.append(tableAlias).append(".");
        builder.append(orignColumn);
        if(!StringUtils.isEmpty(aliasColumn)&&!aliasColumn.equals(orignColumn))
            builder.append(" as ").append(aliasColumn);
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
