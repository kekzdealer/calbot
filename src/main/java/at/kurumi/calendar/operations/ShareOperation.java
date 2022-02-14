package at.kurumi.calendar.operations;

import at.kurumi.calendar.EventSLO;
import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.User;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

/**
 * Share an event with another user.
 */
public class ShareOperation extends Operation {

    private final EventSLO eventSLO;
    private final UserSLO userSLO;

    public ShareOperation(EventSLO eventSLO, UserSLO userSLO) {
        this.eventSLO = eventSLO;
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "share";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        // Get sharing user
        final var discordId = CommandUtil.extractDiscordUserId(e);
        final var user = userSLO.getUserByDiscordId(discordId);
        if(!user.isPresent()) {
            LOG.error("Failed to retrieve user data for user with discordId {}", discordId);
            return e.reply(super.replyBuilder().content("Sorry, I could not retrieve your user data.").build());
        }
        // recipient user discord id input parameter in String form
        final var recipientDiscordIdStr = CommandUtil.getCommandValue(e, "argument0");
        if(recipientDiscordIdStr.isEmpty()) {
            return e.reply(super.replyBuilder().content("Missing recipient user discord id!").build());
        }
        // Parse to long
        long recipientDiscordId;
        try {
            recipientDiscordId = Long.parseLong(recipientDiscordIdStr);
        } catch (NumberFormatException numberFormatException) {
            return e.reply(super.replyBuilder().content("Invalid recipient format: Only integer numbers allowed").build());
        }
        // Get recipient user
        final var recipientUser = userSLO.getUserByDiscordId(recipientDiscordId);
        if(!recipientUser.isPresent()) {
            LOG.error("Failed to retrieve user data for user with discordId {}", discordId);
            return e.reply(super.replyBuilder().content("Sorry, I could not retrieve the recipients user data.").build());
        }
        // Parse TARGET input parameter to int
        int target;
        try {
            target = Integer.parseInt(CommandUtil.getCommandValue(e, Command.TARGET_OPTION_DATA.name()));
        } catch (NumberFormatException numberFormatException) {
            return e.reply(super.replyBuilder().content("Invalid target format: Only integer numbers allowed").build());
        }

        return shareEvent(e, user.get(), target, recipientUser.get());
    }

    private Mono<Void> shareEvent(ChatInputInteractionEvent e, User user, int eventId, User target) {
        final var event = eventSLO.shareEvent(user, eventId, target);

        return event.map(event1 -> {
            LOG.debug("Created event \"{}\", from {} to {}",
                    event1.getTitle(), event1.getStart().getTime(), event1.getEnd().getTime());
            return e.reply(super.replyBuilder().content(String.format("Shared event \"%s\", from <t:%d> to <t:%d> with",
                    event1.getTitle(), event1.getStart().getTime(), event1.getEnd().getTime())).build());
        }).orElseGet(() -> {
            LOG.debug("Failed to share event with id {}", eventId);
            return e.reply(super.replyBuilder().content("Sorry, I could not share the event").build());
        });
    }
}
