package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.SqlParse;

import java.util.List;

@Data
public class EntityInsert implements SqlParse {
    private String column;
    private Object value;

    @Override
    public void addValue(List list) {
        list.add(value);
    }
}
