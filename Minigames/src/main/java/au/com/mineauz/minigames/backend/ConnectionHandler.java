package au.com.mineauz.minigames.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class ConnectionHandler {
    private Connection connection;
    private boolean inUse;
    private long openTime;
    private long closeTime;
    private Map<StatementKey, PreparedStatement> preparedStatements;

    public ConnectionHandler(Connection connection) {
        this.connection = connection;

        preparedStatements = Maps.newIdentityHashMap();
    }

    public boolean lease() {
        if (inUse) {
            return false;
        } else {
            inUse = true;
            openTime = System.currentTimeMillis();
            return true;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private PreparedStatement getStatement(StatementKey key) throws SQLException {
        // Check if its already registered
        PreparedStatement statement = preparedStatements.get(key);
        if (statement == null) {
            // Register it
            statement = key.createPreparedStatement(connection);
            preparedStatements.put(key, statement);
        }

        return statement;
    }

    public ResultSet executeQuery(StatementKey key, Object... arguments) throws SQLException {
        PreparedStatement statement = getStatement(key);
        Preconditions.checkNotNull(statement, "Statement was never registered (or failed)");

        inUse = true;
        applyArguments(statement, arguments);
        statement.setFetchSize(100); // MySQL fetches all rows at once by default, not good for memory usage
        return statement.executeQuery();
    }

    public int executeUpdate(StatementKey key, Object... arguments) throws SQLException {
        PreparedStatement statement = getStatement(key);
        Preconditions.checkNotNull(statement, "Statement was never registered (or failed)");

        inUse = true;
        applyArguments(statement, arguments);
        return statement.executeUpdate();
    }

    public void batchUpdate(StatementKey key, Object... arguments) throws SQLException {
        PreparedStatement statement = getStatement(key);
        Preconditions.checkNotNull(statement, "Statement was never registered (or failed)");

        inUse = true;
        applyArguments(statement, arguments);
        statement.addBatch();
    }

    public int[] executeBatch(StatementKey key) throws SQLException {
        PreparedStatement statement = getStatement(key);
        Preconditions.checkNotNull(statement, "Statement was never registered (or failed)");

        inUse = true;
        return statement.executeBatch();
    }

    public ResultSet executeUpdateWithResults(StatementKey key, Object... arguments) throws SQLException {
        Preconditions.checkArgument(key.returnsGeneratedKeys(), "Statement does not return generated keys");

        PreparedStatement statement = getStatement(key);
        Preconditions.checkNotNull(statement, "Statement was never registered (or failed)");

        inUse = true;
        applyArguments(statement, arguments);
        statement.executeUpdate();
        return statement.getGeneratedKeys();
    }

    private void applyArguments(PreparedStatement statement, Object[] arguments) throws SQLException {
        for (int i = 0; i < arguments.length; ++i) {
            statement.setObject(i + 1, arguments[i]);
        }
    }

    public void release() {
        inUse = false;
    }

    public boolean isInUse() {
        return inUse;
    }

    public long getOpenTime() {
        return openTime;
    }

    public long getCloseTime() {
        return closeTime;
    }

    void closeConnection() {
        inUse = false;
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                // Ignore
            }
        }
    }

    public void beginTransaction() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void endTransaction() {
        try {
            if (!connection.getAutoCommit()) connection.setAutoCommit(true);
            else connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void endTransactionFail() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
