package at.kurumi.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class CommandUtil {

    private CommandUtil() {

    }

    public static Optional<String> getCommandValue(ChatInputInteractionEvent e, String name) {
        return e.getOption(name)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString);
    }

    public static Optional<Instant> getCommandValueAsUTCInstant(ChatInputInteractionEvent e, String name)
            throws DateTimeParseException {
        // 1) create the parsing pattern
        final var formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");
        return getCommandValue(e, name).map(formatter::parse)
                // 2) java.time.Instant representation of the date-time from the user's time zone
                .map(Instant::from)
                // 3) User local Instant is combined with time zone information
                .map(instant -> ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()))
                // 4) Convert back to Instant, but this time normalized to UTC
                .map(ZonedDateTime::toInstant);
    }

    public static long extractDiscordUserId(ChatInputInteractionEvent e) {
        return e.getInteraction().getUser().getId().asLong();
    }

}
