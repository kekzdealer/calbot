package at.kurumi.commands;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalField;
import java.util.List;

/**
 * <ol>
 *     <li>name:String</li>
 *     <li>from:String, yyyy-mm-ddThh:mm</li>
 *     <li>to:String, yyyy-mm-ddThh:mm</li>
 * </ol>
 */
public class CalenderEventCommand extends Command {

    private final List<ApplicationCommandOptionData> options;

    public CalenderEventCommand() {
        options = List.of(
                super.optionData(
                        "title",
                        "The event title",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        true
                ),
                super.optionData(
                        "from",
                        "Start date and time",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        true
                ),
                super.optionData(
                        "to",
                        "End date and time",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        true
                ));
    }

    @Override
    public String getName() {
        return "event";
    }

    @Override
    public String getDescription() {
        return "Create and manage calender events";
    }

    @Override
    public void handle(ApplicationCommandInteractionEvent e) {
        if(e instanceof ChatInputInteractionEvent) {
            final var event = (ChatInputInteractionEvent) e;

            final var title = CommandUtil.getCommandValue(event, "title");

            // Raw date-time strings as entered by the user, from the user's time zone
            final var start = CommandUtil.getCommandValue(event, "from");
            final var end = CommandUtil.getCommandValue(event, "to");
            // Correct offset to UTC
            final var utcStart = dateTimeAsUniversalTimestamp(start);
            final var utcEnd = dateTimeAsUniversalTimestamp(end);
            // Wrap in java.sql.Timestamp for insertion into db
            final var timestampStart = Timestamp.from(utcStart);
            final var timestampEnd = Timestamp.from(utcEnd);


            // todo do the thing

            e.reply("ayo");
        } else {
            e.reply("Could not process event");
        }
    }

    private Instant dateTimeAsUniversalTimestamp(String localDateTime) throws DateTimeParseException {
        final var formatter = DateTimeFormatter.ofPattern("dd:MM HH:mm");
        // 2) java.time.Instant representation of the date-time from the user's time zone
        final var localInstant = Instant.from(formatter.parse(localDateTime));
        // 3) User local Instant is combined with time zone information
        final var zonedDateTime = ZonedDateTime
                // TODO get the zone data for the requesting user from the db, UTC default
                .ofInstant(localInstant, ZoneId.systemDefault());
        // 4) Convert back to Instant, but this time normalized to UTC
        return zonedDateTime.toInstant();
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return options;
    }
}
