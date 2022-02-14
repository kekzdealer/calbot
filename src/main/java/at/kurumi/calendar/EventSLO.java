package at.kurumi.calendar;

import at.kurumi.Database;
import at.kurumi.user.User;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        } catch (PersistenceException persistenceException) {
            if(trx != null) {
                trx.rollback();
            }
            LOG.error("Failed to persist event");
            LOG.debug("Failed to persist event: {} created by {} -> {}",
                    title,
                    creator.getId(),
                    persistenceException.getMessage());
            return Optional.empty();
        }
    }

    public boolean deleteEventById(User participant, int eventId) {
        Transaction trx = null;
        try (final var session = database.openSession()) {
            trx = session.beginTransaction();

            final var criteriaBuilder = session.getCriteriaBuilder();
            var criteria = criteriaBuilder.createQuery(Event.class);

            final var root = criteria.from(Event.class);
            criteria = criteria.select(root)
                    .where(criteriaBuilder.equal(root.get("id"), eventId));

            final var query = session.createQuery(criteria);
            final var event = query.getSingleResult();

            if(event.getUsers().contains(participant)) {
                session.remove(event);
                trx.commit();
                return true;
            } else {
                trx.rollback();
                return false;
            }
        } catch (PersistenceException persistenceException) {
            if(trx != null) {
                trx.rollback();
            }
            LOG.error("Failed to delete event");
            LOG.debug(persistenceException.getMessage());
            return false;
        }
    }

    /**
     * Share an event from one user to another. A user has to have access to an event (either by creating it
     * or being invited by someone that already has access to it) to share it.
     *
     * @param source the initiating user
     * @param eventId the event id
     * @param target the user to share the event with
     * @return Optional wrapped Event instance, or empty if sharing was not possible
     */
    public Optional<Event> shareEvent(User source, int eventId, User target) {
        Transaction trx = null;
        try (final var session = database.openSession()) {
            trx = session.beginTransaction();
            // Find event
            final var criteriaBuilder = session.getCriteriaBuilder();
            var criteria = criteriaBuilder.createQuery(Event.class);

            final var root = criteria.from(Event.class);
            criteria = criteria.select(root)
                    .where(criteriaBuilder.equal(root.get("id"), eventId));

            final var query = session.createQuery(criteria);
            final var event = query.getSingleResult();
            // Verify source user has access to Event
            if(!event.getUsers().contains(source)) {
                return Optional.empty();
            } else {
                // Share event
                event.addUser(target);
                trx.commit();
                return Optional.of(event);
            }
        } catch (PersistenceException persistenceException) {
            if(trx != null) {
                trx.rollback();
            }
            LOG.error("Failed to persist event update");
            LOG.debug("Failed to persist event update: {} --{}--> {}:\n{}",
                    source.getId(),
                    eventId,
                    target.getId(),
                    persistenceException.getMessage());
            return Optional.empty();
        }
    }

    public List<Event> getEventsInTimeSpanForUser(User user, Instant begin, Instant end) {
        return user.getEvents().stream().filter(event -> {
            final var startInstant = event.getStart().toInstant();
            final var endInstant = event.getEnd().toInstant();
            // Check if in time span
            return startInstant.isAfter(begin) && endInstant.isBefore(end);
        }).collect(Collectors.toList());
    }

}
