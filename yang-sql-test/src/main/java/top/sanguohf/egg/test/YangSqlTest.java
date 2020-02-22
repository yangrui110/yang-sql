package top.sanguohf.egg.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import top.sanguohf.top.bootcon.annotation.ScanEntity;

@EnableTransactionManagement
@ScanEntity({"top.sanguohf.egg.test.entity","top.sanguohf.egg.configure.entity"})
@SpringBootApplication
public class YangSqlTest {
    public static void main(String[] args) {
        SpringApplication.run(YangSqlTest.class);
    }
}
