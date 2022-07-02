package at.kurumi;

import at.kurumi.purchasing.Groceries;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.RollbackException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Insert ops (and therefore probably update as well need to be within transactions)
 */
@Stateless
public class Database {

    private static final LoggerFacade LOG = LoggerFacade.getLogger(Database.class);

    private final SessionFactory sessionFactory;

    public Database() {
        LOG.info("Initializing Hibernate");
        sessionFactory = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(Groceries.class)
                .buildSessionFactory();
        LOG.info("Initialized Hibernate");
    }

    /**
     * Open a session and return it. It is the caller's responsibility to close the session after using it.
     *
     * @return Session object, closeable
     */
    public Session openSession() {
        return sessionFactory.openSession();
    }

    private <T> Query<T> createWhereEqualsQuery(String attr, Object equals, Class<T> from, Session session) {
        final var criteriaBuilder = session.getCriteriaBuilder();
        var criteria = criteriaBuilder.createQuery(from);

        final var root = criteria.from(from);
        criteria = criteria.select(root)
                .where(criteriaBuilder.equal(root.get(attr), equals));

        return session.createQuery(criteria);
    }

    private <T> Query<T> createSelectAllFromQuery(Class<T> from, Session session) {
        final var criteriaBuilder = session.getCriteriaBuilder();
        var criteria = criteriaBuilder.createQuery(from);

        final var root = criteria.from(from);
        criteria = criteria.select(root);

        return session.createQuery(criteria);
    }

    public <T> Optional<T> createEntity(T entity) {
        try (final var session = openSession()) {
            final var transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return Optional.of(entity);
        } catch (RollbackException rollbackException) {
            LOG.error("Commit failure: Could not commit new entity of type {}", entity.getClass().getTypeName());
            LOG.debug(rollbackException.getMessage());
            return Optional.empty();
        } catch (PersistenceException persistenceException) {
            LOG.error("Failed to create entity of type {}", entity.getClass().getTypeName());
            LOG.debug(persistenceException.getMessage());
            return Optional.empty();
        }
    }

    public <T> Optional<T> getSingleResultWhere(String attr, Object equals, Class<T> from) {
        try (final var session = openSession()) {
            final var query = createWhereEqualsQuery(attr, equals, from, session);

            return Optional.of(query.getSingleResult());
        } catch (NoResultException noResultException) {
            LOG.warn("No single result of type {} available for {}", from.getTypeName(), equals.toString());
            LOG.debug(noResultException.getMessage());
            return Optional.empty();
        } catch (PersistenceException persistenceException) {
            LOG.error("Failed to get result from {} where {} equals {}", from.getTypeName(), attr, equals);
            LOG.debug(persistenceException.getMessage());
            return Optional.empty();
        }
    }

    public <T> List<T> getResultsWhere(String attr, Object equals, Class<T> from) {
        try (final var session = openSession()) {
            final var query = createWhereEqualsQuery(attr, equals, from, session);

            return query.getResultList();
        } catch (PersistenceException persistenceException) {
            LOG.error("Failed to get results from {} where {} equals {}", from.getTypeName(), attr, equals);
            LOG.debug(persistenceException.getMessage());
            return Collections.emptyList();
        }
    }

    public <T> List<T> getAllResultsFrom(Class<T> from) {
        try (final var session = openSession()) {
            final var query = createSelectAllFromQuery(from, session);

            return query.getResultList();
        } catch (PersistenceException persistenceException) {
            LOG.error("Failed to get results from {}", from.getTypeName());
            LOG.debug(persistenceException.getMessage());
            return Collections.emptyList();
        }
    }

    public <T> Optional<T> updateEntityWhere(List<Consumer<T>> transformations, String attr, Object equals,
                                             Class<T> from) {
        try (final var session = openSession()) {
            final var transaction = session.beginTransaction();
            // Fetch one entity from the database according to a single constraint
            final var entity = createWhereEqualsQuery(attr, equals, from, session)
                    .getSingleResult();
            // Execute prepared setter calls on the persistent entity
            transformations.forEach(consumer -> consumer.accept(entity));
            // Flush the session to execute an update
            transaction.commit();
            return Optional.of(entity);
        } catch (RollbackException rollbackException) {
            LOG.error("Commit failure: Could not commit updated entity of type {}", from.getTypeName());
            LOG.debug(rollbackException.getMessage());
            return Optional.empty();
        } catch (PersistenceException persistenceException) {
            LOG.error("Failed to update entity of type {}", from.getTypeName());
            LOG.debug(persistenceException.getMessage());
            return Optional.empty();
        }
    }

    public <T> boolean deleteEntityWhere(String attr, Object equals, Class<T> from) {
        try (final var session = openSession()) {
            final var transaction = session.beginTransaction();
            final var query = createWhereEqualsQuery(attr, equals, from, session);

            session.remove(query.getSingleResult());
            transaction.commit();
            return true;
        } catch (NoResultException noResultException) {
            LOG.warn("No single result of type {} available for {}", from.getTypeName(), equals.toString());
            LOG.debug(noResultException.getMessage());
            return false;
        } catch (PersistenceException persistenceException) {
            LOG.error("Failed to delete from {} where {} equals {}", from.getTypeName(), attr, equals);
            LOG.debug(persistenceException.getMessage());
            return false;
        }
    }
}
