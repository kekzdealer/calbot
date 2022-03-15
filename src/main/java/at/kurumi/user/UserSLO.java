package at.kurumi.user;

import at.kurumi.Database;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Stateless
public class UserSLO {

    private static final Logger LOG = LogManager.getLogger();

    private final Database database;

    @Inject
    public UserSLO(Database database) {
        this.database = database;
    }

    public Optional<User> createUser(String name, long discordId, ZoneId timezone) {
        final var user = new User();
        user.setName(name);
        user.setDiscordId(discordId);
        user.setTimezone(timezone.getId());
        return database.createEntity(user);
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

    /**
     * Update the user's timezone with a new two character timezone indicator.
     * <p><a href="https://en.wikipedia.org/wiki/List_of_tz_database_time_zones">Wikipedia TZ list</a></p>
     *
     * @param discordId the user's discordId
     * @param timezone  the user's new timezone
     * @return the persistent user entity
     */
    public Optional<User> updateUserTzByDiscordId(long discordId, String timezone) {
        final List<Consumer<User>> transformations = List.of(
                u -> u.setTimezone(timezone)
        );
        return database.updateEntityWhere(transformations, "discordId", discordId, User.class);
    }

    public boolean deleteUserByDiscordId(long discordId) {
        return database.deleteEntityWhere("discordId", discordId, User.class);
    }
}
