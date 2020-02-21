package top.sanguohf.egg.test.entity;

import top.sanguohf.egg.annotation.TableName;

import java.math.BigDecimal;
import java.util.Date;


@TableName("user")
public class UserOne {

    private String userName;
    private String password;
    private String id;
    private BigDecimal price;
    private Date blobOne;
    private byte[] bys;
}
