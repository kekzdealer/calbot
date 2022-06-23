package at.kurumi.discord.commands.user.sub;

import at.kurumi.discord.commands.Command;
import at.kurumi.discord.commands.CommandUtil;
import at.kurumi.discord.commands.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Sets a new nickname for a user. The default nickname is whatever discord name the user had during registration.
 */
public class NicknameCommand extends Command {

    private static final String PARAM_NICKNAME = "nickname";

    private final UserSLO userSLO;

    public NicknameCommand(UserSLO userSLO) {
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "nickname";
    }

    @Override
    public String getDescription() {
        return "Change what the interface should call you";
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return List.of(CommandUtil.optionData(
                PARAM_NICKNAME,
                "Your new nickname",
                ApplicationCommandOption.Type.STRING.getValue(),
                true
        ));
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        final var discordId = CommandUtil.extractDiscordUserId(e);

        final var prefName = CommandUtil.getRequiredParameterAsString(e, PARAM_NICKNAME);

        return userSLO.updateUserNameByDiscordId(discordId, prefName)
                .map(u -> super.simpleReply(e, String.format("Okay, I'll' call you %s from now on", prefName)))
                .orElseGet(() -> super.simpleReply(e, "Sorry, I wasn't able to update your nickname"));
    }
}
