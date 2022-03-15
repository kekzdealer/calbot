package at.kurumi.register.operations;

import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

import java.time.DateTimeException;
import java.time.ZoneId;

public class RegisterOperation extends Operation {

    private final UserSLO userSLO;

    public RegisterOperation(UserSLO userSLO) {
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        final var discordUser = e.getInteraction().getUser();

        final var username = discordUser.getUsername();
        final var discordId = discordUser.getId().asLong();

        final var timezoneInput = CommandUtil.getCommandValue(e, "timezone")
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
