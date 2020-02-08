package top.sanguohf.egg.ops;

import lombok.Data;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.EntityParseUtil;
import top.sanguohf.egg.util.StringUtils;

@Data
public class EntityPageSql {

    private EntitySelectSql selectSql;

    public EntityPageSql(EntitySelectSql selectSql){
        this.selectSql = selectSql;
    }

    /** @param page 默认最小值为1
     * @param size
     * */
    public String toPageSql(int page, int size) {
        // TODO: implement
        int start = (page-1);
        StringBuilder builder = new StringBuilder();
        builder.append(selectSql.toSql()).append(" limit ").append(start).append(",").append(size);
        return builder.toString();
    }

    /** @param page
     * @param size
     * @param dbType
     * */
    public String toPageSql(int page, int size, DbType dbType) {
        // TODO: implement
        if(dbType.getValue().equals(DbType.SQL.getValue())){
            return toSQL(page,size);
        }else if(dbType.getValue().equals(DbType.MYSQL.getValue())){
            return toPageSql(page,size);
        }else if(dbType.getValue().equals(DbType.ORACLE.getValue())){
            return toOracleSql(page,size);
        }
        return toPageSql(page,size);
    }

    public String toCountSql() {
        // TODO: implement
        StringBuilder builder = new StringBuilder();
        builder.append("select count(1) as total");
        makeOtherCommon(builder);
        return builder.toString();
    }

    /** @param dbType
     **/
    public String toCountSql(DbType dbType) {
        // TODO: implement
        return toCountSql();
    }

    private String toSQL(int page, int size){
        StringBuilder builder = new StringBuilder();
        builder.append("WITH selectTemp AS (SELECT TOP 100 PERCENT ROW_NUMBER ( ) OVER ( ORDER BY CURRENT_TIMESTAMP ) AS __row_number__,").append(EntityParseUtil.parseList(selectSql.getColumns()));
        makeOtherCommon(builder);
        int start = (page-1)*size;
        int end = page*size;
        builder.append(") SELECT * FROM selectTemp  WHERE __row_number__ BETWEEN ").append(start).append(" AND ").append(end).append("  ORDER BY __row_number__");
        return builder.toString();
    }

    private String toOracleSql(int page,int size){
        StringBuilder builder = new StringBuilder();
        builder.append("with selectTemp as(select ROWNUM AS __rownum__,").append(EntityParseUtil.parseList(selectSql.getColumns()));
        builder.append(" from ").append(selectSql.getTabelName());
        int start = (page-1)*size;
        int end = page*size;
        if(!StringUtils.isEmpty(selectSql.getTableAlias()))
            builder.append(" ").append(selectSql.getTableAlias());
        //构造Where条件
        if(selectSql.getWheres()!=null) {
            builder.append(" where ").append(selectSql.getWheres().toSql());
            builder.append(" and ").append("ROWNUM <").append(end);
        }else {
            builder.append(" where ").append("ROWNUM <").append(end);
        }
        //插入join条件
        if(selectSql.getJoins() !=null)
            builder.append(" ").append(selectSql.getJoins().toSql());
        //
        if(selectSql.getOrderBys()!=null&&selectSql.getOrderBys().size()>0){
            builder.append(" ").append(" order by ").append(EntityParseUtil.parseList(selectSql.getOrderBys()));
        }
        builder.append(") select * from selectTemp where __rownum__ >= ").append(start);
        return builder.toString();
    }

    private void makeOtherCommon(StringBuilder builder){
        builder.append(" from ").append(selectSql.getTabelName());
        if(!StringUtils.isEmpty(selectSql.getTableAlias()))
            builder.append(" ").append(selectSql.getTableAlias());
        //构造Where条件
        if(selectSql.getWheres()!=null) {
            builder.append(" where ").append(selectSql.getWheres().toSql());
        }
        //插入join条件
        if(selectSql.getJoins() !=null)
            builder.append(" ").append(selectSql.getJoins().toSql());
        //
        if(selectSql.getOrderBys()!=null&&selectSql.getOrderBys().size()>0){
            builder.append(" ").append(" order by ").append(EntityParseUtil.parseList(selectSql.getOrderBys()));
        }
    }
}
