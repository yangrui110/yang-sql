package top.sanguohf.egg.ops;

import lombok.Data;
import top.sanguohf.egg.base.EntitySimpleJoin;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.EntityParseUtil;
import top.sanguohf.egg.util.StringUtils;

import java.util.List;

@Data
public class EntityPageSql{

    private EntitySelectSql selectSql;

    public EntityPageSql(EntitySelectSql selectSql){
        this.selectSql = selectSql;
    }

    /** @param page 默认最小值为1
     * @param size
     * */
    public String toPageSql(int page, int size,boolean isPrepare) {
        // TODO: implement
        int start = (page-1);
        StringBuilder builder = new StringBuilder();
        builder.append(selectSql.sqlOne(isPrepare)).append(" limit ").append(isPrepare?"?":start).append(",").append(isPrepare?"?":size);
        return builder.toString();
    }

    /** @param page
     * @param size
     * @param dbType
     * */
    public String toPageSql(int page, int size,boolean isPrepare, DbType dbType) {
        // TODO: implement
        if(dbType.getValue().equals(DbType.SQL.getValue())){
            return toSQL(page,size,isPrepare);
        }else if(dbType.getValue().equals(DbType.MYSQL.getValue())){
            return toPageSql(page,size,isPrepare);
        }else if(dbType.getValue().equals(DbType.ORACLE.getValue())){
            return toOracleSql(page,size,isPrepare);
        }
        return toPageSql(page,size,isPrepare);
    }

    public void addValue(int page, int size, DbType dbType,List list){
        int start = (page-1)*size;
        int end = page*size;
        if(dbType.getValue().equals(DbType.ORACLE.getValue())){
            this.selectSql.addValue(list);
            list.add(end);
            list.add(start);
        }else if(dbType.getValue().equals(DbType.SQL.getValue())){
            this.selectSql.addValue(list);
            list.add(start+1);
            list.add(end);
        }else{
            this.selectSql.addValue(list);
            list.add(start);
            list.add(end);
        }
    }

    public String toCountSql(boolean isPrepare) {
        // TODO: implement
        StringBuilder builder = new StringBuilder();
        builder.append("select count(1) as __total__");
        makeOtherCommon(builder,isPrepare,true);
        return builder.toString();
    }

    /** @param dbType
     **/
    public String toCountSql(DbType dbType,boolean isPrepare) {
        // TODO: implement
        return toCountSql(isPrepare);
    }
    public void addCountValue(List list){
        selectSql.addValue(list);
    }

    private String toSQL(int page, int size,boolean isPrepare){
        StringBuilder builder = new StringBuilder();
        builder.append("WITH selectTemp AS (SELECT TOP 100 PERCENT ROW_NUMBER ( ) OVER ( ORDER BY CURRENT_TIMESTAMP ) AS __row_number__,").append(EntityParseUtil.parseList(selectSql.getColumns()));
        makeOtherCommon(builder,isPrepare,false);
        int start = (page-1)*size+1;
        int end = page*size;
        builder.append(") SELECT * FROM selectTemp  WHERE __row_number__ BETWEEN ").append(isPrepare?"?":start).append(" AND ").append(isPrepare?"?":end).append("  ORDER BY __row_number__");
        return builder.toString();
    }



    private String toOracleSql(int page,int size,boolean isPrepare){
        StringBuilder builder = new StringBuilder();
        builder.append("with selectTemp as(select ROWNUM AS __rownum__,").append(EntityParseUtil.parseList(selectSql.getColumns()));
        builder.append(" from ").append(selectSql.getTabelName());
        int start = (page-1)*size+1;
        int end = page*size;
        if(!StringUtils.isEmpty(selectSql.getTableAlias()))
            builder.append(" ").append(selectSql.getTableAlias());
        //构造Where条件
        if(selectSql.getWheres()!=null) {
            builder.append(" where ").append(selectSql.getWheres().sqlOne(isPrepare));
            builder.append(" and ").append("ROWNUM <").append(isPrepare?"?":end);
        }else {
            builder.append(" where ").append("ROWNUM <").append(isPrepare?"?":end);
        }
        //插入join条件
        if(selectSql.getJoins() !=null){
            List<EntitySimpleJoin> joins = selectSql.getJoins();
            for(EntitySimpleJoin simpleJoin:joins){
                builder.append(" ").append(simpleJoin.toSql());
            }
        }
        //
        if(selectSql.getOrderBys()!=null&&selectSql.getOrderBys().size()>0){
            builder.append(" ").append(" order by ").append(EntityParseUtil.parseList(selectSql.getOrderBys()));
        }
        builder.append(") select * from selectTemp where __rownum__ >= ").append(isPrepare?"?":start);
        return builder.toString();
    }

    private void makeOtherCommon(StringBuilder builder,boolean isPrepare,boolean isCount){
        builder.append(" from ").append(selectSql.getTabelName().toSql());
        if(!StringUtils.isEmpty(selectSql.getTableAlias()))
            builder.append(" ").append(selectSql.getTableAlias());
        //构造Where条件
        if(selectSql.getWheres()!=null) {
            builder.append(" where ").append(selectSql.getWheres().sqlOne(isPrepare));
        }
        //插入join条件
        if(selectSql.getJoins() !=null) {
            List<EntitySimpleJoin> joins = selectSql.getJoins();
            for(EntitySimpleJoin simpleJoin:joins){
                builder.append(" ").append(simpleJoin.toSql());
            }

        }
        //
        if(selectSql.getOrderBys()!=null&&selectSql.getOrderBys().size()>0&&!isCount){
            builder.append(" ").append(" order by ").append(EntityParseUtil.parseList(selectSql.getOrderBys()));
        }
    }

}
