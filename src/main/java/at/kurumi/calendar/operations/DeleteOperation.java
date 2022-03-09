package at.kurumi.calendar.operations;

import at.kurumi.calendar.EventSLO;
import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

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
        final var discordId = CommandUtil.extractDiscordUserId(e);
        final var user = userSLO.getUserByDiscordId(discordId);

        return user.map(u -> {
            try {
                final var target = CommandUtil.getCommandValue(e, Command.TARGET_OPTION_DATA.name())
                        .map(Integer::parseInt)
                        .orElseThrow();
                if(eventSLO.deleteEventById(u, target)) {
                    return super.simpleReply(e, "Deleted event with id " + target);
                } else {
                    return super.simpleReply(e, "I could not delete the even with id " + target);
                }
            } catch (NumberFormatException numberFormatException) {
                return super.simpleReply(e, "Invalid target format: Only integer numbers allowed");
            } catch (NoSuchElementException noSuchElementException) {
                return super.simpleReply(e, "Missing input argument: Please specify a target");
            }
        }).orElseGet(() -> {
            LOG.error("Failed to retrieve user data for user with discordId {}", discordId);
            return super.simpleReply(e, "I could not retrieve your user data.");
        });
    }
}
