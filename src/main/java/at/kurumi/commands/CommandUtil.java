package at.kurumi.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CommandUtil {

    private CommandUtil() {

    }

    public static String getCommandValue(ChatInputInteractionEvent e, String name) {
        return e.getOption(name)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .orElse("");
    }

    public static String getCommandValue(ChatInputInteractionEvent e, String name, String alt) {
        return e.getOption(name)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .orElse(alt);
    }

    public static Instant getCommandValueAsUTCInstant(ChatInputInteractionEvent e, String name)
            throws DateTimeParseException {
        final var dateString = getCommandValue(e, name);
        // 1) create the parsing pattern
        final var formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");
        // 2) java.time.Instant representation of the date-time from the user's time zone
        final var localInstant = Instant.from(formatter.parse(dateString));
        // 3) User local Instant is combined with time zone information
        final var zonedDateTime = ZonedDateTime
                // TODO get the zone data for the requesting user from the db, UTC default
                .ofInstant(localInstant, ZoneId.systemDefault());
        // 4) Convert back to Instant, but this time normalized to UTC
        return zonedDateTime.toInstant();
    }

    public static long extractDiscordUserId(ChatInputInteractionEvent e) {
        return e.getInteraction().getUser().getId().asLong();
    }

}
