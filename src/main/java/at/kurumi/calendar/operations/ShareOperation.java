package at.kurumi.calendar.operations;

import at.kurumi.calendar.EventSLO;
import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.User;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

import java.util.Optional;

/**
 * Share an event with another user.
 */
public class ShareOperation extends Operation {

    private EventSLO eventSLO;
    private UserSLO userSLO;

    public ShareOperation(EventSLO eventSLO, UserSLO userSLO) {
        this.eventSLO = eventSLO;
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "share";
    }

    @Override
    public void handle(ChatInputInteractionEvent e) {
        // Get sharing user
        final var discordId = CommandUtil.extractDiscordUserId(e);
        final var user = userSLO.getUserByDiscordId(discordId);
        if(!user.isPresent()) {
            e.reply("Sorry, I could not retrieve your user data.");
            LOG.error("Failed to retrieve user data for user with discordId {}", discordId);
            return;
        }
        // recipient user discord id input parameter in String form
        final var recipientDiscordIdStr = CommandUtil.getCommandValue(e, "argument0");
        if(recipientDiscordIdStr.isEmpty()) {
            e.reply("Missing recipient user discord id!");
            return;
        }
        // Parse to long
        long recipientDiscordId = -1L;
        try {
            recipientDiscordId = Long.parseLong(recipientDiscordIdStr);
        } catch (NumberFormatException numberFormatException) {
            e.reply("Invalid recipient format: Only integer numbers allowed");
        }
        // Get recipient user
        final var recipientUser = userSLO.getUserByDiscordId(recipientDiscordId);
        if(!recipientUser.isPresent()) {
            e.reply("Sorry, I could not retrieve the recipients user data.");
            LOG.error("Failed to retrieve user data for user with discordId {}", discordId);
            return;
        }
        // Parse TARGET input parameter to int
        int target = -1;
        try {
            target = Integer.parseInt(CommandUtil.getCommandValue(e, Command.TARGET_OPTION_DATA.name()));
        } catch (NumberFormatException numberFormatException) {
            e.reply("Invalid target format: Only integer numbers allowed");
        }

        shareEvent(e, user.get(), target, recipientUser.get());
    }

    private void shareEvent(ChatInputInteractionEvent e, User user, int eventId, User target) {
        final var event = eventSLO.shareEvent(user, eventId, target);

        event.ifPresentOrElse(event1 -> {
            e.reply(String.format("Shared event \"%s\", from <t:%d> to <t:%d> with",
                    event1.getTitle(), event1.getStart().getTime(), event1.getEnd().getTime()));
            LOG.debug("Created event \"{}\", from {} to {}",
                    event1.getTitle(), event1.getStart().getTime(), event1.getEnd().getTime());
        }, () -> {
            e.reply("Sorry, I could not share the event");
            LOG.debug("Failed to share event with id {}", eventId);
        });
    }
}
