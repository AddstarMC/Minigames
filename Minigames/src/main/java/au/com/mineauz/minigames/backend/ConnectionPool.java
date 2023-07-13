package au.com.mineauz.minigames.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ConnectionPool {
    private final List<ConnectionHandler> connections;
    private final String connectionString;
    private final Properties props;
    private long maxIdleTime;

    public ConnectionPool(String connectionString, Properties properties) {
        this.connectionString = connectionString;
        props = properties;
        connections = Collections.synchronizedList(new ArrayList<>());
        maxIdleTime = TimeUnit.SECONDS.toMillis(30);
    }

    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    public void setMaxIdleTime(long maxTime) {
        maxIdleTime = maxTime;
    }

    public void removeExpired() {
        synchronized (connections) {
            Iterator<ConnectionHandler> it = connections.iterator();
            while (it.hasNext()) {
                ConnectionHandler handler = it.next();

                if (!handler.isInUse()) {
                    if (System.currentTimeMillis() - handler.getCloseTime() > maxIdleTime) {
                        // Timeout
                        handler.closeConnection();
                        it.remove();
                    }
                } else {
                    if (System.currentTimeMillis() - handler.getOpenTime() > maxIdleTime) {
                        // So we don't just accumulate connections forever
                        handler.release();
                    }
                }
            }
        }
    }

    /**
     * @return Returns a free connection from the pool of connections. Creates a new connection if there are none available
     */
    public ConnectionHandler getConnection() throws SQLException {
        synchronized (connections) {
            for (int i = 0; i < connections.size(); ++i) {
                ConnectionHandler con = connections.get(i);

                if (con.lease()) {
                    // Check connection
                    boolean healthy = true;

                    try {
                        if (con.getConnection().isClosed()) {
                            healthy = false;
                        }
                    } catch (SQLException e) {
                        healthy = false;
                    }

                    // Get rid of the connection
                    if (!healthy) {
                        con.closeConnection();
                        connections.remove(i--);
                        // It's ok
                    } else {
                        return con;
                    }
                }
            }
        }

        // Create a new connection
        return createConnection();
    }

    private ConnectionHandler createConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(connectionString, props);
        ConnectionHandler handler = new ConnectionHandler(connection);
        connections.add(handler);
        return handler;
    }

    public void closeConnections() {
        synchronized (connections) {
            for (ConnectionHandler c : connections) {
                c.closeConnection();
            }

            connections.clear();
        }
    }
}