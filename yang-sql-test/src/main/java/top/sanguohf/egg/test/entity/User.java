package top.sanguohf.egg.test.entity;

import lombok.Data;
import top.sanguohf.egg.annotation.Field;
import top.sanguohf.egg.annotation.Id;
import top.sanguohf.egg.annotation.IgnoreSelectReback;
import top.sanguohf.egg.annotation.TableName;

import java.math.BigDecimal;

@Data
@TableName("user")
public class User {
    @Id
    @Field("user_name")
    private String userName;
    private String password;
    protected String id;
    @IgnoreSelectReback
    private BigDecimal price;
}
