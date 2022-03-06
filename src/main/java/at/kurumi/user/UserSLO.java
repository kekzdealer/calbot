package at.kurumi.user;

import at.kurumi.Database;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
        final var user = new User();
        user.setName(name);
        user.setDiscordId(discordId);
        return database.createEntity(user) ? Optional.of(user) : Optional.empty();
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
        return database.deleteEntityWhere("discordId", discordId, User.class);
    }
}
