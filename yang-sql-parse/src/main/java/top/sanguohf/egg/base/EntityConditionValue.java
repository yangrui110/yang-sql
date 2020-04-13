package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.StringUtils;

import java.util.List;

@Data
public class EntityConditionValue implements EntityCondition {

    private Object column;

    private String tableAlias;

    public String toSql() {
        return sqlOne(false);
    }

    public String toSql(DbType dbType) {
        return toSql();
    }

    @Override
    public String sqlOne(boolean isPrepare) {
        StringBuilder builder = new StringBuilder();
        if (!StringUtils.isEmpty(tableAlias))
            builder.append(tableAlias).append(".");
        if (!isPrepare){
            if (column instanceof String)
                builder.append("'");
            builder.append(column);
            if (column instanceof String)
                builder.append("'");
        }else if(column instanceof List){
            builder.append("(");
            for(int i=0;i<((List) column).size();i++){
                builder.append("?");
                if(i!=((List) column).size()-1){
                    builder.append(",");
                }
            }
            builder.append(")");
        }else if(column instanceof Object[]){
            builder.append("(");
            for(int i=0;i<((Object[]) column).length;i++){
                builder.append("?");
                if(i!=((Object[]) column).length-1){
                    builder.append(",");
                }
            }
            builder.append(")");
        }else builder.append("?");
        return builder.toString();
    }

    @Override
    public void addValue(List list) {
        if(column instanceof List){
            for(Object os:(List)column){
                list.add(os);
            }
        }else if(column instanceof Object[]){
            for(Object os:(Object[]) column){
                list.add(os);
            }
        }else list.add(column);
    }
}
