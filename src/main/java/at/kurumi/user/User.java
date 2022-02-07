package at.kurumi.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class User {

    private static final Logger LOG = LogManager.getLogger();

    public void createTable(Connection connection) {
        LOG.info("Attempting to create user table");
        final var ddl = "CREATE TABLE user(" +
                "id INTEGER PRIMARY KEY," +
                "name VARCHAR(50) NOT NULL);";
        try {
            connection.createStatement().executeUpdate(ddl);
        } catch (SQLException e) {
            LOG.error("Failed to create user table");
            LOG.debug(e.getMessage());
        }
    }

    public int insertUser(Connection connection, String name) {
        LOG.debug("Inserting user: {}", name);
        final var dml = "INSERT INTO user(name) VALUES(?);";
        try {
            final var preparedStatement = connection.prepareStatement(dml);
            preparedStatement.setString(0, name);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Failed to insert user");
            LOG.debug(e.getMessage());
            return 0;
        }
    }

    public int updateUser(Connection connection, long id, String name) {
        LOG.debug("Updating user: -> {}", name);
        final var dml = "UPDATE user SET" +
                "name=?" +
                "WHERE id=?;";
        try {
            final var stmt = connection.prepareStatement(dml);
            stmt.setString(0, name);
            stmt.setLong(1, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Failed to update user");
            LOG.debug(e.getMessage());
            return 0;
        }
    }

    public void deleteUser(Connection connection, long id) {
        // TODO implement deleteUser
    }
}
