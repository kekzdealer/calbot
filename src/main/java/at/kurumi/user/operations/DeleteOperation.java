package at.kurumi.user.operations;

import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

/**
 * Permanently delete all your data from the database.
 */
public class DeleteOperation extends Operation {

    private final UserSLO userSLO;

    public DeleteOperation(UserSLO userSLO) {
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        final var discordId = CommandUtil.extractDiscordUserId(e);

        if (userSLO.deleteUserByDiscordId(discordId)) {
            return super.simpleReply(e, "Profile deleted. Goodbye.");
        } else {
            return super.simpleReply(e, "Sorry, I wasn't able to delete your profile.");
        }
    }
}
