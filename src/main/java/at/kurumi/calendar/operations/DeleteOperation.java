package at.kurumi.calendar.operations;

import at.kurumi.calendar.EventSLO;
import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import reactor.core.publisher.Mono;

public class DeleteOperation extends Operation {

    private final EventSLO eventSLO;
    private final UserSLO userSLO;

    public DeleteOperation(EventSLO eventSLO, UserSLO userSLO) {
        this.eventSLO = eventSLO;
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        final var messageSpec = InteractionApplicationCommandCallbackSpec.builder();

        final var discordId = CommandUtil.extractDiscordUserId(e);

        final var user = userSLO.getUserByDiscordId(discordId);

        return user.map(u -> {
            try {
                final var target = Integer.parseInt(CommandUtil.getCommandValue(e, Command.TARGET_OPTION_DATA.name()));
                final var success = eventSLO.deleteEventById(u, target);
                if(success) {
                    messageSpec.content("Deleted event with id " + target);
                } else {
                    messageSpec.content("Sorry, I could not delete the even with id " + target);
                }
                return e.reply(messageSpec.build());
            } catch (NumberFormatException numberFormatException) {
                messageSpec.content("Invalid target format: Only integer numbers allowed");
                return e.reply(messageSpec.build());
            }
        }).orElseGet(() -> {
            LOG.error("Failed to retrieve user data for user with discordId {}", discordId);
            messageSpec.content("Sorry, I could not retrieve your user data.");
            return e.reply(messageSpec.build());
        });
    }
}
