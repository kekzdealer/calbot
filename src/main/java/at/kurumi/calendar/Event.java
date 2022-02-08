package at.kurumi.calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Event {

    private int id;
    private Timestamp start;
    private Timestamp end;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    private static final Logger LOG = LogManager.getLogger();

    public void createTable(Connection connection) {
        LOG.info("Attempting to create event table");
        final var ddl = "CREATE TABLE event(" +
                "id INTEGER PRIMARY KEY," +
                "title VARCHAR(200) NOT NULL," +
                "start TIMESTAMP WITH TIME ZONE NOT NULL," +
                "end TIMESTAMP WITH TIME ZONE NOT NULL);";
        try {
            connection.createStatement().executeUpdate(ddl);
        } catch (SQLException e) {
            LOG.error("Failed to create event table");
            LOG.debug(e.getMessage());
        }
    }

    public int insertEvent(Connection connection, String title, Timestamp start, Timestamp end) {
        LOG.debug("Inserting event {} from {} to {}", title, start.toString(), end.toString());
        final var dml = "INSERT INTO event(title, start, end) VALUES(?, ?, ?);";
        try {
            final var preparedStatement = connection.prepareStatement(dml);
            preparedStatement.setString(0, title);

            preparedStatement.setTimestamp(1, start);
            preparedStatement.setTimestamp(2, end);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Failed to insert event");
            LOG.debug(e.getMessage());
            return 0;
        }
    }

    public void deleteEvent(Connection connection, long id) {
        // TODO implement deleteEvent
    }
}
