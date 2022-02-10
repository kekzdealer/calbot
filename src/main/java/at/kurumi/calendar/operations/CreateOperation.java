package at.kurumi.calendar.operations;

import at.kurumi.calendar.EventSLO;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.User;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

import java.time.Instant;

/**
 * Create a new event.
 */
public class CreateOperation extends Operation {

    private final EventSLO eventSLO;
    private final UserSLO userSLO;

    public CreateOperation(EventSLO eventSLO, UserSLO userSLO) {
        this.eventSLO = eventSLO;
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public void handle(ChatInputInteractionEvent e) {
        final var discordId = CommandUtil.extractDiscordUserId(e);

        final var user = userSLO.getUserByDiscordId(discordId);

        user.ifPresentOrElse(u -> {
            createEvent(e,
                    u,
                    CommandUtil.getCommandValue(e, "argument0"),
                    CommandUtil.getCommandValueAsUTCInstant(e, "argument1"),
                    CommandUtil.getCommandValueAsUTCInstant(e, "argument2"));
        }, () -> {
            e.reply("Sorry, I could not retrieve your user data.");
            LOG.error("Failed to retrieve user data for user with discordId {}", discordId);
        });
    }

    private void createEvent(ChatInputInteractionEvent e, User user, String title, Instant start, Instant end) {
        final var event = eventSLO.createEvent(user, title, start, end);

        event.ifPresentOrElse(event1 -> {
            e.reply(String.format("Created event \"%s\", from <t:%d> to <t:%d>", title, start.getEpochSecond(), end.getEpochSecond()));
            LOG.debug("Created event \"{}\", from {} to {}", title, start.getEpochSecond(), end.getEpochSecond());
        }, () -> {
            e.reply("Sorry, I could not create the event");
            LOG.debug("Failed to create event \"{}\", from {} to {}", title, start.getEpochSecond(), end.getEpochSecond());
        });
    }
}
