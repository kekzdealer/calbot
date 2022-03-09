package at.kurumi.user.operations;

import at.kurumi.commands.Operation;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

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
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        final var discordUser = e.getInteraction().getUser();
        final var username = discordUser.getUsername();
        final var discordId = discordUser.getId().asLong();

        final var user = userSLO.createUser(username, discordId);

        return user.map(u -> super.simpleReply(e, "Welcome " + username))
                .orElseGet(() -> super.simpleReply(e,
                        "Sorry, I wasn't able to create a profile for your discord id."));
    }
}
