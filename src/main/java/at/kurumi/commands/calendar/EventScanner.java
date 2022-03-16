package at.kurumi.commands.calendar;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.inject.Inject;

import java.time.Instant;

/**
 * Scans for upcoming events and sends notification messages as needed.
 */
@Startup
@Singleton
public class EventScanner {

    @Resource
    TimerService timerService;

    private GatewayDiscordClient discordClient;
    private EventSLO eventSLO;

    @Inject
    public EventScanner(GatewayDiscordClient discordClient, EventSLO eventSLO) {
        this.discordClient = discordClient;
        this.eventSLO = eventSLO;
    }

    @PostConstruct
    public void initTimer() {
        timerService.createTimer(0, 60000, "Event notification timer. Runs every minute.");
        // TODO look at calendar or date controlled timers in timer service once you can download src from mvn again
    }

    @Timeout
    public void scanAndNotify(Timer timer) {
        final var events = eventSLO.getEventsDueBy(Instant.now());
        events.forEach(event -> {
            final var notification = String.format("Event: %s from <t:%d> to <t:%d>!",
                    event.getTitle(),
                    event.getStart().getTime(),
                    event.getEnd().getTime());
            final var participants = event.getUsers();
            participants.forEach(user -> {
                discordClient.getUserById(Snowflake.of(user.getDiscordId()))
                        .flatMap(User::getPrivateChannel)
                        .flatMap(privateChannel -> privateChannel.createMessage(notification))
                        .block();
            });
        });
    }
}
