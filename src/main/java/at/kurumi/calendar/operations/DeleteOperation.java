package at.kurumi.calendar.operations;

import at.kurumi.calendar.EventSLO;
import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

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
    public void handle(ChatInputInteractionEvent e) {
        final var discordId = CommandUtil.extractDiscordUserId(e);

        final var user = userSLO.getUserByDiscordId(discordId);

        user.ifPresentOrElse(u -> {
            try {
                final var target = Integer.parseInt(CommandUtil.getCommandValue(e, Command.TARGET_OPTION_DATA.name()));
                final var success = eventSLO.deleteEventById(u, target);
                if(success) {
                    e.reply("Deleted event with id " + target);
                } else {
                    e.reply("Sorry, I could not delete the even with id " + target);
                }
            } catch (NumberFormatException numberFormatException) {
                e.reply("Invalid target format: Only integer numbers allowed");
            }
        }, () -> {
            e.reply("Sorry, I could not retrieve your user data.");
            LOG.error("Failed to retrieve user data for user with discordId {}", discordId);
        });
    }
}
