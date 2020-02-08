package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.SqlParse;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.StringUtils;

@Data
public class EntityOrderBy implements SqlParse {
    private String column;
    private String tableAlias;
    private String direct;

    public String toSql() {
        StringBuilder builder = new StringBuilder();
        if(!StringUtils.isEmpty(tableAlias))
            builder.append(tableAlias).append(".");
        builder.append(column).append(" ").append(direct);
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
