package top.sanguohf.egg.reflect;

import top.sanguohf.egg.annotation.Field;
import top.sanguohf.egg.annotation.Id;
import top.sanguohf.egg.annotation.IgnoreSelectReback;
import top.sanguohf.egg.annotation.TableName;

@TableName("user")
public class User {
    @Id
    @Field("user_name")
    private String userName;
    @IgnoreSelectReback
    private String password;
    public String table;
    protected String id;
}
