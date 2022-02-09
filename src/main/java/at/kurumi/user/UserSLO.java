package at.kurumi.user;

import at.kurumi.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import java.util.Optional;

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
        } catch (HibernateException hibernateException) {
            if(trx != null) {
                trx.rollback();
            }
            LOG.error("Failed to persist new user");
            LOG.debug("Failed to persist new user: {}, {}", name, discordId);
            return Optional.empty();
        }
    }

    public Optional<User> getUserByDiscordId(long discordId) {
        Transaction trx = null;
        try (final var session = database.openSession()) {
            trx = session.beginTransaction();

            final var criteriaBuilder = session.getCriteriaBuilder();
            var criteria = criteriaBuilder.createQuery(User.class);

            final var root = criteria.from(User.class);
            criteria = criteria.select(root)
                    .where(criteriaBuilder.equal(root.get("discordId"), discordId));

            final var query = session.createQuery(criteria);
            return Optional.of(query.getSingleResult());
        } catch (HibernateException hibernateException) {
            if(trx != null) {
                trx.rollback();
            }
            LOG.error("Failed to query user");
            LOG.debug("Failed to query user by discordId: {}", discordId);
            return Optional.empty();
        }
    }
}
