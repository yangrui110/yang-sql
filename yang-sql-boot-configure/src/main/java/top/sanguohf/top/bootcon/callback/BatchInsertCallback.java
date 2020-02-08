package top.sanguohf.top.bootcon.callback;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class BatchInsertCallback implements StatementCallback {

    private List<String> sqls;

    public BatchInsertCallback(List<String> sqls){
        this.sqls=sqls;
    }

    @Override
    public int[] doInStatement(Statement statement) throws SQLException, DataAccessException {
        int[] executeBatch=new int[sqls.size()];
        Connection connection = statement.getConnection();
        if (JdbcUtils.supportsBatchUpdates(connection)) {
            int var4 = sqls.size();

            int ix;
            for(ix = 0; ix < var4; ++ix) {
                String sqlStmt = sqls.get(ix);
                statement.addBatch(sqlStmt);
            }
            executeBatch = statement.executeBatch();
        } else {
            for(int i = 0; i < sqls.size(); ++i) {
                if (statement.execute(sqls.get(i))) {
                    throw new InvalidDataAccessApiUsageException("Invalid batch SQL statement: " + sqls.get(i));
                }
                executeBatch[i] = statement.getUpdateCount();
            }
        }
        return executeBatch;
    }
}
