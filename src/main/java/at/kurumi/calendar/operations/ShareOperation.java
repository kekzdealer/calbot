package at.kurumi.calendar.operations;

import at.kurumi.calendar.EventSLO;
import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.User;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

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
            return super.simpleReply(e, "Sorry, I wasn't able to retrieve your user data.");
        }

        long recipientDiscordId;
        try {
            recipientDiscordId = CommandUtil.getCommandValue(e, "argument0")
                    .map(Long::parseLong)
                    .orElseThrow();
        } catch (NumberFormatException numberFormatException) {
            return super.invalidArgumentReply(e, "Only integer numbers allowed");
        } catch (NoSuchElementException noSuchElementException) {
            return super.missingArgumentReply(e, "Please specify the recipient id");
        }
        // Get recipient user
        final var recipientUser = userSLO.getUserByDiscordId(recipientDiscordId);
        if(!recipientUser.isPresent()) {
            return super.simpleReply(e, "I wasn't able to retrieve the recipients user data.");
        }
        // Parse TARGET input parameter to int
        int target;
        try {
            target = CommandUtil.getCommandValue(e, Command.TARGET_OPTION_DATA.name())
                    .map(Integer::parseInt)
                    .orElseThrow();
        } catch (NumberFormatException numberFormatException) {
            return super.invalidArgumentReply(e, "Only integer numbers allowed");
        } catch (NoSuchElementException noSuchElementException) {
            return super.missingArgumentReply(e, "Please specify a target");
        }

        return shareEvent(e, user.get(), target, recipientUser.get());
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
