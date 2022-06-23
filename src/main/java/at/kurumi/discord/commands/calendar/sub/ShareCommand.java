package at.kurumi.discord.commands.calendar.sub;

import at.kurumi.discord.commands.calendar.EventSLO;
import at.kurumi.discord.commands.Command;
import at.kurumi.discord.commands.CommandUtil;
import at.kurumi.discord.commands.user.User;
import at.kurumi.discord.commands.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Share an event with another user.
 */
@Stateless
public class ShareCommand extends Command {

    private static final String PARAM_TARGET_EVENT = "event";
    private static final String PARAM_TARGET_USER = "recipient";

    private final EventSLO eventSLO;
    private final UserSLO userSLO;

    @Inject
    public ShareCommand(EventSLO eventSLO, UserSLO userSLO) {
        this.eventSLO = eventSLO;
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "share";
    }

    @Override
    public String getDescription() {
        return "Share an event with someone else";
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return List.of(
                CommandUtil.optionData(PARAM_TARGET_EVENT,
                        "The event you want to share",
                        ApplicationCommandOption.Type.INTEGER.getValue(),
                        true),
                CommandUtil.optionData(PARAM_TARGET_USER,
                        "The recipient user",
                        ApplicationCommandOption.Type.USER.getValue(),
                        true)
        );
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        // Get sharing user
        final var discordId = CommandUtil.extractDiscordUserId(e);
        final var user = userSLO.getUserByDiscordId(discordId);
        if(user.isEmpty()) {
            return super.simpleReply(e, "Sorry, I wasn't able to retrieve your user data.");
        }

        // Get recipient user
        final var recipientDiscordId = CommandUtil.getRequiredParameterAsUser(e, PARAM_TARGET_USER)
                .getId()
                .asLong();
        final var recipientUser = userSLO.getUserByDiscordId(recipientDiscordId);
        if(recipientUser.isEmpty()) {
            return super.simpleReply(e, "I wasn't able to retrieve the recipients user data.");
        }

        final var eventId = CommandUtil.getRequiredParameterAsLong(e, PARAM_TARGET_EVENT).intValue();

        return shareEvent(e, user.get(), eventId, recipientUser.get());
    }

    private Mono<Void> shareEvent(ChatInputInteractionEvent e, User user, int eventId, User target) {
        final var event = eventSLO.shareEvent(user, eventId, target);

        return event.map(event1 -> super.simpleReply(e, String.format("Shared event \"%s\", from <t:%d> to <t:%d> with",
                event1.getTitle(),
                event1.getStart().getTime(),
                event1.getEnd().getTime())))
                .orElseGet(() -> super.simpleReply(e, "Sorry, I wasn't able to share the event"));
    }
}
