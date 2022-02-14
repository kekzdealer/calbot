package at.kurumi.user.operations;

import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

/**
 * Print all user data.
 */
public class ListOperation extends Operation {

    private final UserSLO userSLO;

    public ListOperation(UserSLO userSLO) {
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        final var discordId = CommandUtil.extractDiscordUserId(e);

        final var user = userSLO.getUserByDiscordId(discordId);

        return user.map(u -> {
            LOG.debug("Printing user information about user with discordId {}", discordId);
            return e.reply(super.replyBuilder().content(String.format("%s's profile:\n" +
                            "```id:\t\t\t%d\n" +
                            "discordId:\t%d\n" +
                            "nickname:\t%s```",
                    u.getName(), u.getId(), u.getDiscordId(), u.getName())).build());
        }).orElseGet(() -> {
            LOG.error("Failed to read user information for user with discordId {}", discordId);
            return e.reply(super.replyBuilder().content("Sorry, I could not retrieve your information.").build());
        });
    }
}
