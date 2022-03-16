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
 * User sub-command to display a set of profile data.
 */
@Stateless
public class ShowCommand extends Command {

    private final UserSLO userSLO;

    @Inject
    public ShowCommand(UserSLO userSLO) {
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Show your profile data";
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        final var discordId = CommandUtil.extractDiscordUserId(e);

        final var user = userSLO.getUserByDiscordId(discordId);

        return user.map(u -> super.simpleReply(e, String.format("%s's profile:\n" +
                        "```id:\t\t\t%d\n" +
                        "discordId:\t%d\n" +
                        "nickname:\t%s```",
                        u.getName(), u.getId(), u.getDiscordId(), u.getName())))
                .orElseGet(() -> super.simpleReply(e, "Sorry, I wasn't able to retrieve your information."));
    }
}
