package at.kurumi.calendar;

import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CalendarProgram extends Command {

    private static final Logger LOG = LogManager.getLogger("Calendar");

    // Dependencies - modify this for automatic DI in the future.
    private final EventSLO eventSLO;
    private final UserSLO userSLO;

    public CalendarProgram(EventSLO eventSLO, UserSLO userSLO) {
        this.eventSLO = eventSLO;
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "calendar";
    }

    @Override
    public String getDescription() {
        return "Create and manage calender events";
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return List.of(
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
    public void handle(ApplicationCommandInteractionEvent e) {
        if(e instanceof ChatInputInteractionEvent) {
            final var event = (ChatInputInteractionEvent) e;
            // Parse arguments
            final var title = CommandUtil.getCommandValue(event, "title");
            // Raw date-time strings as entered by the user, from the user's time zone
            final var start = CommandUtil.getCommandValue(event, "from");
            final var end = CommandUtil.getCommandValue(event, "to");
            // Correct offset to UTC
            final var utcStart = dateTimeAsUniversalTimestamp(start);
            final var utcEnd = dateTimeAsUniversalTimestamp(end);
            // Identify user
            final var snowflake = event.getInteraction().getUser().getId();
            final var userOptional = userSLO.getUserByDiscordId(snowflake.asLong());
            // Process data and reply
            if(userOptional.isPresent()) {
                final var eventORMOptional = eventSLO.createEvent(userOptional.get(), title, utcStart, utcEnd);
                 eventORMOptional.ifPresentOrElse(
                         ev -> e.reply(String.format("Created event \"%s\"", ev.getTitle())),
                         () -> e.reply("Failed to save event"));
            } else {
                e.reply("Failed to identify user");
            }
        } else {
            e.reply("Input event type unsupported by this program");
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

}
