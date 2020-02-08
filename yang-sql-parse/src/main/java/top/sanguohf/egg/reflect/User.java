package top.sanguohf.egg.reflect;

import top.sanguohf.egg.annotation.Field;
import top.sanguohf.egg.annotation.Ignore;
import top.sanguohf.egg.annotation.TableName;

@TableName("user")
public class User {
    @Field("user_name")
    private String userName;
    @Ignore
    private String password;
    public String table;
    protected String id;
}
