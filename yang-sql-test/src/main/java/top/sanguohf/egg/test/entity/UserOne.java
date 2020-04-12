package top.sanguohf.egg.test.entity;

import lombok.Data;
import top.sanguohf.egg.annotation.IgnoreSelectReback;
import top.sanguohf.egg.annotation.TableName;

import java.math.BigDecimal;
import java.util.Date;


@Data
@TableName("user")
public class UserOne {

    private String userName;
    private String password;
    private String id;
    private BigDecimal price;
    @IgnoreSelectReback
    private Date blobOne;
    @IgnoreSelectReback
    private byte[] bys;
}
