package com.vinrecipe.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton class managing the MySQL database connection.
 * Pattern: Singleton — only one Connection instance per app session.
 *
 * Credentials are loaded from the .env file at runtime (never hardcoded)
 * so secrets are not exposed in version control.
 */
public class DatabaseConnection {

    private static Connection instance = null;

    // Private constructor — prevents external instantiation
    private DatabaseConnection() {}

    /**
     * Returns the shared Connection, creating it if needed.
     * Reads DB credentials from .env file in the project root.
     */
    public static Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            Properties env = loadEnv();

            String host     = env.getProperty("DB_HOST", "localhost");
            String port     = env.getProperty("DB_PORT", "3306");
            String name     = env.getProperty("DB_NAME", "defaultdb");
            String user     = env.getProperty("DB_USER", "root");
            String password = env.getProperty("DB_PASSWORD", "");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + name
                    + "?sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true"
                    + "&autoReconnect=true&cachePrepStmts=true&prepStmtCacheSize=250"
                    + "&useServerPrepStmts=true&connectTimeout=8000&socketTimeout=30000";

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instance = DriverManager.getConnection(url, user, password);
                System.out.println("[DB] Connected to " + name + " on " + host);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        return instance;
    }

    /** Load .env from project root directory. */
    private static Properties loadEnv() {
        Properties props = new Properties();
        String[] candidates = { ".env", "../../.env", "../../../.env" };
        for (String path : candidates) {
            try (FileInputStream fis = new FileInputStream(path)) {
                props.load(fis);
                System.out.println("[DB] Loaded credentials from: " + path);
                return props;
            } catch (IOException ignored) {}
        }
        System.err.println("[DB] WARNING: .env not found — using defaults (local MySQL)");
        return props;
    }

    /** Close the connection (call on app shutdown). */
    public static void close() {
        if (instance != null) {
            try {
                instance.close();
                instance = null;
                System.out.println("[DB] Connection closed");
            } catch (SQLException e) {
                System.err.println("[DB] Error closing connection: " + e.getMessage());
            }
        }
    }
}
