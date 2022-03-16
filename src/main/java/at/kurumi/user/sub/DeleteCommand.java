package at.kurumi.user.sub;

import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * User sub-command to delete a set of profile data. Basically "unregisters" the user
 */
@Stateless
public class DeleteCommand extends Command {

    private final UserSLO userSLO;

    @Inject
    public DeleteCommand(UserSLO userSLO) {
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "Delete you profile data";
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return Collections.emptyList();
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
