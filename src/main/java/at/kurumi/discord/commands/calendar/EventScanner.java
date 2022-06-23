package at.kurumi.discord.commands.calendar;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.User;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.Timer;
import jakarta.inject.Inject;

import java.time.Instant;

/**
 * Scans for upcoming events and sends notification messages as needed.
 */
@Startup
@Singleton
public class EventScanner {

    private final GatewayDiscordClient discordClient;
    private final EventSLO eventSLO;

    @Inject
    public EventScanner(GatewayDiscordClient discordClient, EventSLO eventSLO) {
        this.discordClient = discordClient;
        this.eventSLO = eventSLO;
    }

    @Schedule(minute = "*/1", info = "Scan for events and notify participants", persistent = false)
    public void scanAndNotify(Timer timer) {
        final var events = eventSLO.getEventsDueBy(Instant.now());
        events.forEach(event -> {
            final var notification = String.format("Event: %s from <t:%d> to <t:%d>!",
                    event.getTitle(),
                    event.getStart().getTime(),
                    event.getEnd().getTime());
            final var participants = event.getUsers();
            participants.forEach(user -> discordClient.getUserById(Snowflake.of(user.getDiscordId()))
                    .flatMap(User::getPrivateChannel)
                    .flatMap(privateChannel -> privateChannel.createMessage(notification))
                    .block());
        });
    }
}
