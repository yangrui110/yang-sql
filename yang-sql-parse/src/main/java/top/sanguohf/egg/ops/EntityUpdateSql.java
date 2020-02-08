package top.sanguohf.egg.ops;

import lombok.Data;
import top.sanguohf.egg.base.EntityCondition;
import top.sanguohf.egg.base.EntityInsert;
import top.sanguohf.egg.constant.DbType;

import java.util.List;

@Data
public class EntityUpdateSql extends AbstractEntityJoinTable {
    private String tableName;
    private String tableAlias;
    private List<EntityInsert> updates;
    private EntityCondition wheres;

    public String toSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("update ").append(tableName).append(" set ");
        for(int i=0;i<updates.size();i++){
            builder.append(updates.get(i).getColumn()).append(" = ");
            Object value=updates.get(i).getValue();
            if(value instanceof String){
                builder.append("'");
            }
            builder.append(value);
            if(value instanceof String){
                builder.append("'");
            }
            if(i!=updates.size()-1)
                builder.append(",");
        }
        builder.append(" where ").append(wheres.toSql());
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
