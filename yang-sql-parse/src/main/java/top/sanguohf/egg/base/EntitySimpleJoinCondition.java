package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.StringUtils;

@Data
public class EntitySimpleJoinCondition implements JoinCondition {
    private String leftTableAlias;
    private String leftTableColumn;
    private String relation;
    private String rightTableColumn;
    private String rightTableAlias;

    public String toSql() {
        StringBuilder builder = new StringBuilder();
        if(!StringUtils.isEmpty(leftTableAlias))
            builder.append(leftTableAlias).append(".");
        builder.append(leftTableColumn).append(" ").append(relation);
        if("like".equals(relation))
            builder.append("'%");
        if(!StringUtils.isEmpty(rightTableAlias))
            builder.append(rightTableAlias).append(".");
        builder.append(rightTableColumn);
        if("like".equals(relation))
            builder.append("%'");
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
