package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.constant.DbType;

import java.util.List;

@Data
public class EntityListJoinCondition implements JoinCondition {

    private List<JoinCondition> condition;
    private String combine;

    public String toSql() {
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<condition.size();i++){
            builder.append(" (").append(condition.get(i).toSql()).append(") ");
            if(i!=condition.size()-1)
                builder.append(combine);
        }
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
