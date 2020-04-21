package top.sanguohf.egg.ops;

import lombok.Data;
import top.sanguohf.egg.base.EntityColumn;
import top.sanguohf.egg.base.EntityCondition;
import top.sanguohf.egg.base.EntityOrderBy;
import top.sanguohf.egg.base.EntitySimpleJoin;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.EntityParseUtil;
import top.sanguohf.egg.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
public class EntitySelectSql extends AbstractEntityJoinTable {
    private EntityJoinTable tabelName;
    private List<EntityColumn> columns;
    private EntityCondition wheres;
    private List<EntitySimpleJoin> joins;
    private List<EntityOrderBy> orderBys;

    //分组、分组条件、

    public String toSql() {
        return sqlOne(false);
    }

    public String toSql(DbType dbType) {
        return toSql();
    }

    @Override
    public PreparedStatement toSql(Connection connection) throws SQLException {
        String sql = sqlOne(true);
        LinkedList list = new LinkedList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for(int i=0;i<list.size();i++){
            Object o = list.get(i);
            if(o instanceof Date){
                o=new java.sql.Date(((Date)o).getTime());
            }
            preparedStatement.setObject(i+1,o);
        }
        return preparedStatement;
    }

    @Override
    public PreparedStatement toSql(Connection connection, DbType dbType) throws SQLException {
        return toSql(connection);
    }

    @Override
    public String sqlOne(boolean isPrepare) {
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
            builder.append(" where ").append(wheres.sqlOne(isPrepare));
        //
        if(orderBys!=null&&orderBys.size()>0){
            builder.append(" ").append(" order by ").append(EntityParseUtil.parseList(orderBys));
        }
        return builder.toString();
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

    @Override
    public void addValue(List list) {
        if(wheres!=null)
            wheres.addValue(list);
    }
}