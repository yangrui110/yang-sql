package top.sanguohf.egg.reflect;

import lombok.Data;
import top.sanguohf.egg.annotation.*;

import java.math.BigDecimal;

@Data
@TableName("user")
public class User {
    @Id
    @Field("user_name")
    private String userName;

    //@IgnoreSelectReback
    private String password;

    @IgnoreSelectReback
    @OrderBy
    public String table;

    @OrderBy
    @Condition(value = "1",relation = "=")
    protected String id;

    private BigDecimal price;


}
