package top.sanguohf.top.bootcon.config;

import lombok.Data;
import org.springframework.stereotype.Component;
import top.sanguohf.egg.constant.DbType;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@Component
@Data
public class DataBaseTypeInit {

    private DbType dbType;

    public DataBaseTypeInit(DataSource dataSource) throws SQLException {
        DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
        String dataBaseType = metaData.getDatabaseProductName();	              //获取数据库类型
        if(("Microsoft SQL Server").equals(dataBaseType)){
            this.dbType=DbType.SQL;
        }else if(("HSQL Database Engine").equals(dataBaseType)){
            this.dbType = DbType.HSQL;
        }else if(("MySQL").equals(dataBaseType)){
            this.dbType=DbType.MYSQL;
        }else if(("Oracle").equals(dataBaseType)){
            this.dbType=DbType.ORACLE;
        }
    }

}
