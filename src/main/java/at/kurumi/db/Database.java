package at.kurumi.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

    public static final String DBMS = "postgresql";

    /**
     * Open a database connection.
     *
     * @param url database url in the form of host:port
     * @param user the username
     * @param password the password
     * @return a connection object
     * @throws SQLException if something goes wrong
     */
    public Connection connect(String url, String user, String password) throws SQLException {
        final var properties = new Properties();
        properties.put("user", user);
        properties.put("password", password);
        // using this format jdbc:postgresql://host:port/
        final var jdbcString = String.format("jdbc:%s://%s/", DBMS, url);
        return DriverManager.getConnection(jdbcString, properties);
    }

    public static void main(String[] args) throws SQLException {
        final var connection = new Database()
                .connect("localhost:5432", "kurumi", "kurumi");
        System.out.println("connected to db");
        connection.close();
    }

}
