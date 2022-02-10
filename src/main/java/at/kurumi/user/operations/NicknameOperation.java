package at.kurumi.user.operations;

import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

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
    public void handle(ChatInputInteractionEvent e) {
        final var discordId = CommandUtil.extractDiscordUserId(e);

        final var prefName = CommandUtil.getCommandValue(e, "argument0");

        final var user = userSLO.updateUserNameByDiscordId(discordId, prefName);

        user.ifPresentOrElse(u -> {
            e.reply(String.format("Okay, I will call you %s from now on", prefName));
            LOG.debug("Updated a nickname to {}", prefName);
        }, () -> {
            e.reply("Sorry, I could not update your nickname");
            LOG.error("Failed to update user with discordId {} nickname to {}", discordId, prefName);
        });
    }
}
