package at.kurumi.calendar.operations;

import at.kurumi.calendar.EventSLO;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import reactor.core.publisher.Mono;

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
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        final var messageSpec = InteractionApplicationCommandCallbackSpec.builder();

        final var discordId = CommandUtil.extractDiscordUserId(e);

        final var user = userSLO.getUserByDiscordId(discordId);

        return user.map(u -> {
            final var title = CommandUtil.getCommandValue(e, "argument0");
            final var start = CommandUtil.getCommandValueAsUTCInstant(e, "argument1");
            final var end = CommandUtil.getCommandValueAsUTCInstant(e, "argument2");
            return eventSLO.createEvent(u, title, start, end)
                    .map(event -> {
                        LOG.debug("Created event \"{}\", from {} to {}",
                                title,
                                start.getEpochSecond(),
                                end.getEpochSecond());
                        messageSpec.content(String.format("Created event \"%s\", from <t:%d> to <t:%d>",
                                title,
                                start.getEpochSecond(),
                                end.getEpochSecond()));
                        return e.reply(messageSpec.build());
                    }).orElseGet(() -> {
                        LOG.debug("Failed to create event \"{}\", from {} to {}",
                                title,
                                start.getEpochSecond(),
                                end.getEpochSecond());
                        messageSpec.content("Sorry, I could not create the event");
                        return e.reply(messageSpec.build());
                    });
        }).orElseGet(() -> {
            LOG.error("Failed to retrieve user data for user with discordId {}", discordId);
            messageSpec.content("Sorry, I could not retrieve your user data.");
            return e.reply(messageSpec.build());
        });
    }
}
