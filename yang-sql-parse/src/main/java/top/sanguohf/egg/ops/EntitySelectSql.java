package top.sanguohf.egg.ops;

import lombok.Data;
import top.sanguohf.egg.base.EntityColumn;
import top.sanguohf.egg.base.EntityCondition;
import top.sanguohf.egg.base.EntityOrderBy;
import top.sanguohf.egg.base.EntitySimpleJoin;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.EntityParseUtil;
import top.sanguohf.egg.util.StringUtils;

import java.util.List;

@Data
public class EntitySelectSql extends AbstractEntityJoinTable {
    private String tabelName;
    private List<EntityColumn> columns;
    private EntityCondition wheres;
    private EntitySimpleJoin joins;
    private List<EntityOrderBy> orderBys;

    public String toSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("select ").append(EntityParseUtil.parseList(columns));
        builder.append(" from ").append(tabelName);
        if(!StringUtils.isEmpty(tableAlias))
            builder.append(" ").append(tableAlias);
        //构造Where条件
        if(wheres!=null)
            builder.append(" where ").append(wheres.toSql());
        //插入join条件
        if(joins !=null)
            builder.append(" ").append(joins.toSql());
        //
        if(orderBys!=null&&orderBys.size()>0){
            builder.append(" ").append(" order by ").append(EntityParseUtil.parseList(orderBys));
        }
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}