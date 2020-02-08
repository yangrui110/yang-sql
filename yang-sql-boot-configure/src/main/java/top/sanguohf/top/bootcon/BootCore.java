package top.sanguohf.top.bootcon;

import top.sanguohf.top.bootcon.annotation.ScanEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
//@SpringBootApplication
//@ScanEntity("com.yang.sql.bootcore")
public class BootCore {
    public static void main(String[] args) {
        SpringApplication.run(BootCore.class);
    }
}
