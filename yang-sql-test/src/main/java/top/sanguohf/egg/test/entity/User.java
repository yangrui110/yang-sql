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
    protected String id;
}
