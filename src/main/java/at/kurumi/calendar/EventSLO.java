package at.kurumi.calendar;

import at.kurumi.db.Database;
import at.kurumi.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

public class EventSLO {

    private static final Logger LOG = LogManager.getLogger();

    private final Database database;

    public EventSLO(Database database) {
        this.database = database;
    }

    public Optional<Event> createEvent(User creator, String title, Instant start, Instant end) {
        Transaction trx = null;
        try (final var session = database.openSession()) {
            trx = session.beginTransaction();
            final var event = new Event();
            event.setTitle(title);
            event.setStart(Timestamp.from(start));
            event.setEnd(Timestamp.from(end));
            event.addUser(creator);

            session.persist(event);
            trx.commit();
            return Optional.of(event);
        } catch (HibernateException hibernateException) {
            if(trx != null) {
                trx.rollback();
            }
            LOG.error("Failed to persist event");
            LOG.debug("Failed to persist event: {} created by {} -> {}",
                    title,
                    creator.getId(),
                    hibernateException.getMessage());
            return Optional.empty();
        }
    }
}
