package top.sanguohf.top.bootcon.entity;

import lombok.Data;
import top.sanguohf.egg.annotation.TableName;

@TableName("user")
@Data
public class User {

    private String id;

    private String userName;

    private String password;
}
