package at.kurumi.commands.register;

import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.List;

@Stateless
public class RegisterCommand extends Command {

    private final UserSLO userSLO;

    @Inject
    public RegisterCommand(UserSLO userSLO) {
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "Sign up to use this discord interface.";
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return List.of(
                CommandUtil.optionData(
                        "timezone",
                        "Enter your timezone as an offset relative to UTC like this: UTC+1",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        true
                ));
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        final var discordUser = e.getInteraction().getUser();

        final var username = discordUser.getUsername();
        final var discordId = discordUser.getId().asLong();

        final var timezoneInput = CommandUtil.getParameterAsString(e, "timezone")
                .filter(s -> s.startsWith("UTC+") || s.startsWith("UTC-"));
        if(!timezoneInput.isPresent()) {
            return super.invalidArgumentReply(e, "Please use the correct timezone format: UTC+X or UTC-X");
        }
        try {
            final var timezone = ZoneId.of(timezoneInput.get());

            return userSLO.createUser(username, discordId, timezone)
                    .map(u -> super.simpleReply(e, "Welcome " + username))
                    .orElseGet(() -> super.simpleReply(e,
                            "Sorry, I wasn't able to create a profile for your discord id."));
        } catch (DateTimeException dateTimeException) {
            LOG.debug(dateTimeException.getMessage());
            return super.invalidArgumentReply(e, "Please use the correct timezone format: UTC+X or UTC-X");
        }
    }
}
