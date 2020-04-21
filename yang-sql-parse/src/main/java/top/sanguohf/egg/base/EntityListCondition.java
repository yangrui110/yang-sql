package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.constant.DbType;

import java.util.List;

@Data
public class EntityListCondition implements EntityCondition {

    private List<? extends EntityCondition> condition;
    private String combine;

    public String toSql() {
        return sqlOne(false);
    }

    public String toSql(DbType dbType) {
        return toSql();
    }

    @Override
    public String sqlOne(boolean isPrepare) {
        StringBuilder builder = new StringBuilder();
        if(condition.size()>0)
            builder.append(" (");
        for(int i=0;i<condition.size();i++){
            builder.append(condition.get(i).sqlOne(isPrepare)).append(" ");
            if(i!=condition.size()-1)
                builder.append(combine).append(" ");
        }
        if(condition.size()>0)
            builder.append(") ");
        return builder.toString();
    }

    @Override
    public void addValue(List list) {
        for(EntityCondition dom:condition){
            dom.addValue(list);
        }
    }
}
