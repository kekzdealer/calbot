package at.kurumi.user.operations;

import at.kurumi.commands.Operation;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

/**
 * Starts the sign-up process.
 */
public class HelloOperation extends Operation {

    private final UserSLO userSLO;

    public HelloOperation(UserSLO userSLO) {
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "hello";
    }

    @Override
    public void handle(ChatInputInteractionEvent e) {
        final var discordUser = e.getInteraction().getUser();
        final var username = discordUser.getUsername();
        final var discordId = discordUser.getId().asLong();

        final var user = userSLO.createUser(username, discordId);

        user.ifPresentOrElse(u -> {
            e.reply("Welcome " + username);
            LOG.debug("Created user {} with id {}", username, discordId);
        }, () -> {
            e.reply("Sorry, I could not create your user profile.");
            LOG.error("Failed to create user {} with discordId {}", username, discordId);
        });
    }
}
