package at.kurumi.commands.calendar.sub;

import at.kurumi.commands.calendar.EventSLO;
import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.time.*;
import java.util.Collections;
import java.util.List;

/**
 * List all events due today, in chronological order
 */
@Stateless
public class TodayCommand extends Command {

    private final EventSLO eventSLO;
    private final UserSLO userSLO;

    @Inject
    public TodayCommand(EventSLO eventSLO, UserSLO userSLO) {
        this.eventSLO = eventSLO;
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "today";
    }

    @Override
    public String getDescription() {
        return "List all events due today in chronological order";
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        final var discordId = CommandUtil.extractDiscordUserId(e);
        final var user = userSLO.getUserByDiscordId(discordId);

        return user.map(u -> {
            /*
            Define "today" using two Instants from 00:00 - 23:59, from the user's time zone perspective,
            then convert both to UTC
             */
            final var localDate = LocalDate.now(ZoneId.of(u.getTimezone()));
            final var instant0000 = LocalDateTime.of(localDate, LocalTime.MIDNIGHT).toInstant(ZoneOffset.UTC);
            final var instant2359 = LocalDateTime.of(localDate, LocalTime.MAX).toInstant(ZoneOffset.UTC);

            final var events = eventSLO.getEventsInTimeSpanForUser(u, instant0000, instant2359);
            // Sort in ascending chronological order according to event start time (early to late)
            Collections.sort(events);

            final var reply = new StringBuilder("__Your events for today:__\n");
            events.forEach(event -> reply.append(String.format("- %s from <t:%d> to <t:%d>",
                    event.getTitle(),
                    event.getStart().getTime(),
                    event.getEnd().getTime())));
            return super.simpleReply(e, reply.toString());
        }).orElseGet(() -> {
            LOG.error("Failed to retrieve user data for user with discordId {}", discordId);
            return super.simpleReply(e, "Sorry, I wasn't able to retrieve your user data.");
        });
    }
}
