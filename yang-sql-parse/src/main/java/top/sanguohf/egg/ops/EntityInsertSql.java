package top.sanguohf.egg.ops;
import lombok.Data;
import top.sanguohf.egg.base.EntityInsert;
import top.sanguohf.egg.constant.DbType;

import java.util.List;

@Data
public class EntityInsertSql extends AbstractEntityJoinTable {
    private String tableName;
    private String tableAlias;
    private List<EntityInsert> insertList;

    public String toSql() {
        StringBuilder builder = new StringBuilder();
        builder.append(" insert into ")
                .append(tableName).append("(");
        for(int i =0;i<insertList.size();i++){
            builder.append(insertList.get(i).getColumn());
            if(i!=insertList.size()-1)
                builder.append(",");
        }
        builder.append(")").append(" values(");
        for(int i =0;i<insertList.size();i++){
            Object value=insertList.get(i).getValue();
            if(value instanceof String)
                builder.append("'");
            builder.append(value);
            if(value instanceof String)
                builder.append("'");
            if(i!=insertList.size()-1)
                builder.append(",");
        }
        builder.append(")");
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
