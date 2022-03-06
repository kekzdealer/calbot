package at.kurumi;

import at.kurumi.calendar.Event;
import at.kurumi.user.User;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Database {

    private static final Logger LOG = LogManager.getLogger();

    private final SessionFactory sessionFactory;

    public Database() {
        LOG.info("Initializing Hibernate");
        sessionFactory = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Event.class)
                .buildSessionFactory();
    }

    /**
     * Open a session and return it. It is the caller's responsibility to close the session after using it.
     *
     * @return Session object, closeable
     */
    public Session openSession() {
        return sessionFactory.openSession();
    }

    private <T> Query<T> getSingleConstraintQuery(String attr, Object equals, Class<T> from, Session session) {
        final var criteriaBuilder = session.getCriteriaBuilder();
        var criteria = criteriaBuilder.createQuery(from);

        final var root = criteria.from(from);
        criteria = criteria.select(root)
                .where(criteriaBuilder.equal(root.get(attr), equals));

        return session.createQuery(criteria);
    }

    public boolean createEntity(Object entity) {
        try (final var session = openSession()) {
            session.persist(entity);
            session.flush();
            return true;
        } catch (PersistenceException persistenceException) {
            LOG.error("Failed to create entity of type {}", entity.getClass().getTypeName());
            LOG.debug(persistenceException.getMessage());
            return false;
        }
    }

    public <T> Optional<T> getSingleResultWhere(String attr, Object equals, Class<T> from) {
        try (final var session = openSession()) {
            final var query = getSingleConstraintQuery(attr, equals, from, session);

            return Optional.of(query.getSingleResult());
        } catch (PersistenceException persistenceException) {
            LOG.error("Failed to get result from {} where {} equals {}", from.getTypeName(), attr, equals);
            LOG.debug(persistenceException.getMessage());
            return Optional.empty();
        }
    }

    public <T> Optional<T> updateEntityWhere(List<Consumer<T>> transformations, String attr, Object equals, Class<T> from) {
        try (final var session = openSession()) {
            // Fetch one entity from the database according to a single constraint
            final var entity = getSingleConstraintQuery(attr, equals, from, session)
                    .getSingleResult();
            // Execute prepared setter calls on the persistent entity
            transformations.forEach(consumer -> consumer.accept(entity));
            // Flush the session to execute an update
            session.flush();
            return Optional.of(entity);
        } catch (PersistenceException persistenceException) {
            LOG.error("Failed to update entity of type {}", from.getTypeName());
            LOG.debug(persistenceException.getMessage());
            return Optional.empty();
        }
    }

    public <T> boolean deleteEntityWhere(String attr, Object equals, Class<T> from) {
        try (final var session = openSession()) {
            final var query = getSingleConstraintQuery(attr, equals, from, session);

            session.remove(query.getSingleResult());
            session.flush();
            return true;
        } catch (PersistenceException persistenceException) {
            LOG.error("Failed to delete from {} where {} equals {}", from.getTypeName(), attr, equals);
            LOG.debug(persistenceException.getMessage());
            return false;
        }
    }
}
