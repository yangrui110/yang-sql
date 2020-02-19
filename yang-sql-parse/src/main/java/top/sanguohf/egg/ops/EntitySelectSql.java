package top.sanguohf.egg.ops;

import lombok.Data;
import top.sanguohf.egg.base.EntityColumn;
import top.sanguohf.egg.base.EntityCondition;
import top.sanguohf.egg.base.EntityOrderBy;
import top.sanguohf.egg.base.EntitySimpleJoin;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.EntityParseUtil;
import top.sanguohf.egg.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

@Data
public class EntitySelectSql extends AbstractEntityJoinTable {
    private EntityJoinTable tabelName;
    private List<EntityColumn> columns;
    private EntityCondition wheres;
    private List<EntitySimpleJoin> joins;
    private List<EntityOrderBy> orderBys;

    public String toSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("select ").append(EntityParseUtil.parseList(columns));
        builder.append(" from ");
        if(tabelName instanceof EntitySelectSql){
            builder.append("(");
        }
        builder.append(tabelName.toSql());
        if(tabelName instanceof EntitySelectSql){
            builder.append(")");
        }
        if(!StringUtils.isEmpty(tableAlias))
            builder.append(" ").append(tableAlias);
        //插入join条件
        if(joins !=null){
            for(EntitySimpleJoin simpleJoin:joins){
                builder.append(" ").append(simpleJoin.toSql());
            }
        }
        //构造Where条件
        if(wheres!=null)
            builder.append(" where ").append(wheres.toSql());
        //
        if(orderBys!=null&&orderBys.size()>0){
            builder.append(" ").append(" order by ").append(EntityParseUtil.parseList(orderBys));
        }
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }

    public List<EntityColumn> getColumns() {
        if(columns==null)
            columns=new LinkedList<>();
        return columns;
    }

    public List<EntityOrderBy> getOrderBys() {
        if(orderBys==null)
            orderBys=new LinkedList<>();
        return orderBys;
    }
}