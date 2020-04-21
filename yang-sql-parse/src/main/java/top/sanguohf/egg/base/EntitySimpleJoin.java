package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.SqlParse;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.ops.EntityJoinTable;
import top.sanguohf.egg.ops.EntitySelectSql;
import top.sanguohf.egg.util.StringUtils;

@Data
public class EntitySimpleJoin implements SqlParse {
    private String joinConditions;
    private String relation;
    private EntityJoinTable tableName;
    private String tableAlias;

    public String toSql() {
        StringBuilder builder = new StringBuilder();
        builder.append(getRelation()).append(" ");
        if(tableName instanceof EntitySelectSql){
            builder.append("(");
        }
        builder.append(tableName.toSql());
        if(tableName instanceof EntitySelectSql){
            builder.append(")");
        }
        if(!StringUtils.isEmpty(getTableAlias()))
            builder.append(" ").append(getTableAlias()).append(" ");
        if(!StringUtils.isEmpty(getJoinConditions()))
            builder.append(" on ");
        //后期这里会变成子查询的范畴
        builder.append(getJoinConditions());
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
