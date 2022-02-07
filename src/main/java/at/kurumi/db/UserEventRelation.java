package at.kurumi.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserEventRelation {

    private static final Logger LOG = LogManager.getLogger();

    public void createTable(Connection connection) {
        LOG.info("Attempting to create userEventRelation table");
        final var ddl = "CREATE TABLE userEventRelation(" +
                "userId INTEGER UNIQUE NOT NULL," +
                "eventId INTEGER UNIQUE NOT NULL," +
                "CONSTRAINT fkUserId FOREIGN KEY(userId) REFERENCES user(id)," +
                "CONSTRAINT fkEventId FOREIGN KEY(eventId) REFERENCES event(id)," +
                "PRIMARY KEY (userId, eventId);";
        try (final var stmt = connection.createStatement()){
            stmt.executeUpdate(ddl);
        } catch (SQLException e) {
            LOG.error("Failed to create userEventRelation table");
            LOG.debug(e.getMessage());
        }
    }

    public int insertRelation(Connection connection, long userId, long eventId) {
        LOG.debug("Mapping {} to {}", userId, eventId);
        final var dml = "INSERT INTO userEventRelation VALUES(?, ?);";
        try (final var preparedStatement = connection.prepareStatement(dml)) {
            preparedStatement.setLong(0, userId);
            preparedStatement.setLong(1, eventId);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Failed to insert user-event relation");
            LOG.debug(e.getMessage());
            return 0;
        }
    }

    public List<Long> selectEventForUser(Connection connection, long userId) {
        LOG.debug("Selecting event(s) for user {}", userId);
        final var dql = "SELECT eventId FROM userEventRelation WHERE userId=?";
        final var results = new ArrayList<Long>();
        try (final var ps = connection.prepareStatement(dql)) {
            ps.setLong(0, userId);
            final var resultSet = ps.executeQuery();
            while(resultSet.next()) {
                results.add(resultSet.getLong("eventId"));
            }
            resultSet.close();
            return results;
        } catch (SQLException e) {
            LOG.error("Failed to select event(s) for user");
            LOG.debug(e.getMessage());
            return results;
        }
    }

    public List<Long> selectUserForEvent(Connection connection, long eventId) {
        LOG.debug("Selecting user(s) for event {}", eventId);
        final var dql = "SELECT userId FROM userEventRelation WHERE eventId=?";
        final var results = new ArrayList<Long>();
        try (final var ps = connection.prepareStatement(dql)) {
            ps.setLong(0, eventId);
            final var resultSet = ps.executeQuery();
            while(resultSet.next()) {
                results.add(resultSet.getLong("userId"));
            }
            resultSet.close();
            return results;
        } catch (SQLException e) {
            LOG.error("Failed to select user(s) for event");
            LOG.debug(e.getMessage());
            return results;
        }
    }
}
