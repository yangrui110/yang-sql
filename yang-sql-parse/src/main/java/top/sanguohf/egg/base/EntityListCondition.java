package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.constant.DbType;

import java.util.List;

@Data
public class EntityListCondition implements EntityCondition {

    private List<EntityConditionDom> condition;
    private String combine;

    public String toSql() {
        StringBuilder builder = new StringBuilder();
        if(condition.size()>0)
            builder.append(" (");
        for(int i=0;i<condition.size();i++){
            builder.append(condition.get(i).toSql()).append(" ");
            if(i!=condition.size()-1)
                builder.append(combine).append(" ");
        }
        if(condition.size()>0)
            builder.append(") ");
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
