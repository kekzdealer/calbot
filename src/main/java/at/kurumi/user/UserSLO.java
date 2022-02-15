package at.kurumi.user;

import at.kurumi.Database;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class UserSLO {

    private static final Logger LOG = LogManager.getLogger();

    private final Database database;

    public UserSLO(Database database) {
        this.database = database;
    }

    public Optional<User> createUser(String name, long discordId) {
        Transaction trx = null;
        try (final var session = database.openSession()){
            trx = session.beginTransaction();
            final var user = new User();
            user.setName(name);
            user.setDiscordId(discordId);
            session.persist(user);
            trx.commit();
            return Optional.of(user);
        } catch (PersistenceException persistenceException) {
            if(trx != null) {
                trx.rollback();
            }
            LOG.error("Failed to persist new user");
            LOG.debug(persistenceException.getMessage());
            return Optional.empty();
        }
    }

    public Optional<User> getUserByDiscordId(long discordId) {
        return database.getSingleResultWhere("discordId", discordId, User.class);
    }

    public Optional<User> updateUserNameByDiscordId(long discordId, String username) {
        final List<Consumer<User>> transformations = List.of(
                u -> u.setName(username)
        );
        return database.updateEntityWhere(transformations, "discordId", discordId, User.class);
    }

    public boolean deleteUserByDiscordId(long discordId) {
        Transaction trx = null;
        try (final var session = database.openSession()) {
            trx = session.beginTransaction();

            final var criteriaBuilder = session.getCriteriaBuilder();
            var criteria = criteriaBuilder.createQuery(User.class);

            final var root = criteria.from(User.class);
            criteria = criteria.select(root)
                    .where(criteriaBuilder.equal(root.get("discordId"), discordId));

            final var query = session.createQuery(criteria);
            final var user = query.getSingleResult();

            session.remove(user);
            trx.commit();

            return true;
        } catch (PersistenceException persistenceException) {
            if(trx != null) {
                trx.rollback();
            }
            LOG.error("Failed to delete user");
            LOG.debug(persistenceException.getMessage());
            return false;
        }
    }
}
