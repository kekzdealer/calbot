package at.kurumi.user.operations;

import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

/**
 * Change how the bot refers to you. Default will be the discord name you had during the "hello" operation.
 */
public class NicknameOperation extends Operation {

    private final UserSLO userSLO;

    public NicknameOperation(UserSLO userSLO) {
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "nickname";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        final var discordId = CommandUtil.extractDiscordUserId(e);

        try {
            final var prefName = CommandUtil.getCommandValue(e, "argument0").orElseThrow();

            return userSLO.updateUserNameByDiscordId(discordId, prefName)
                    .map(u -> super.simpleReply(e, String.format("Okay, I will call you %s from now on", prefName)))
                    .orElseGet(() -> super.simpleReply(e, "Sorry, I wasn't able to update your nickname"));
        } catch (NoSuchElementException noSuchElementException) {
            return super.missingArgumentReply(e, "Please provide a new nickname.");
        }
    }
}
