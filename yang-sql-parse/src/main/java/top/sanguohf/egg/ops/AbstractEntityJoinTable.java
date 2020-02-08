package top.sanguohf.egg.ops;

import lombok.Data;

@Data
public abstract class AbstractEntityJoinTable implements EntityJoinTable {
    public String tableAlias;
}
