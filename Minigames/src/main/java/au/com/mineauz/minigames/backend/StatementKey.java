package au.com.mineauz.minigames.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementKey {
    private String sql;
    private boolean returnGeneratedKeys;
    private boolean valid;

    public StatementKey(String sql) {
        this(sql, false);
    }

    public StatementKey(String sql, boolean returnGeneratedKeys) {
        this.sql = sql;
        this.returnGeneratedKeys = returnGeneratedKeys;
        this.valid = true;
    }

    public String getSQL() {
        return sql;
    }

    public boolean returnsGeneratedKeys() {
        return returnGeneratedKeys;
    }

    public boolean isValid() {
        return valid;
    }

    PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        try {
            return connection.prepareStatement(sql, (returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS));
        } catch (SQLException e) {
            valid = false;
            throw e;
        }
    }
}
