package at.kurumi.discord.commands.calendar.sub;

import at.kurumi.discord.commands.calendar.EventSLO;
import at.kurumi.discord.commands.Command;
import at.kurumi.discord.commands.CommandUtil;
import at.kurumi.discord.commands.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Create a new event.
 */
@Stateless
public class CreateCommand extends Command {

    private static final String PARAM_TITLE = "title";
    private static final String PARAM_START = "start";
    private static final String PARAM_END = "end";

    private final EventSLO eventSLO;
    private final UserSLO userSLO;

    @Inject
    public CreateCommand(EventSLO eventSLO, UserSLO userSLO) {
        this.eventSLO = eventSLO;
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create a new event";
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return List.of(
                CommandUtil.optionData(PARAM_TITLE,
                        "The event title",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        true),
                CommandUtil.optionData(PARAM_START,
                        "Date and time that the event begins at. Format: yyyy-MM-dd HH:mm",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        true),
                CommandUtil.optionData(PARAM_END,
                        "Date and time that the event ends at. Format: yyyy-MM-dd HH:mm",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        true)
        );
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        final var discordId = CommandUtil.extractDiscordUserId(e);
        final var user = userSLO.getUserByDiscordId(discordId);

        return user.map(u -> {
            final var title = CommandUtil.getRequiredParameterAsString(e, PARAM_TITLE);
            try {
                final var start = getAsUTCInstant(CommandUtil.getRequiredParameterAsString(e, PARAM_START));
                final var end = getAsUTCInstant(CommandUtil.getRequiredParameterAsString(e, PARAM_END));
                return eventSLO.createEvent(u, title, start, end)
                        .map(event -> {
                            LOG.debug("Created event \"{}\", from {} to {}",
                                    title,
                                    start.getEpochSecond(),
                                    end.getEpochSecond());
                            return super.simpleReply(e, String.format("Created event \"%s\", from <t:%d> to <t:%d>",
                                    title,
                                    start.getEpochSecond(),
                                    end.getEpochSecond()));
                        }).orElseGet(() -> {
                            LOG.debug("Failed to create event \"{}\", from {} to {}",
                                    title,
                                    start.getEpochSecond(),
                                    end.getEpochSecond());
                            return super.simpleReply(e, "Sorry, I could not create the event");
                        });
            } catch (DateTimeParseException dateTimeParseException) {
                LOG.debug(dateTimeParseException.getMessage());
                return super.simpleReply(e, "Failed to parse one of the two date strings. " +
                        "Double check the format or contact a developer");
            }
        }).orElseGet(() -> {
            LOG.error("Failed to retrieve user data for user with discordId {}", discordId);
            return super.simpleReply(e, "Sorry, I could not retrieve your user data.");
        });
    }

    // TODO this needs to take the user's timezone into consideration when first parsing the date string.
    private Instant getAsUTCInstant(String dateString) throws DateTimeParseException {
        // 1) create the parsing pattern
        final var formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");
        return Mono.just(dateString)
                .map(formatter::parse)
                // 2) java.time.Instant representation of the date-time from the user's time zone
                .map(Instant::from)
                // 3) User local Instant is combined with time zone information
                .map(instant -> ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()))
                // 4) Convert back to Instant, but this time normalized to UTC
                .map(ZonedDateTime::toInstant)
                .block();
    }
}
