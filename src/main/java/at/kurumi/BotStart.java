package at.kurumi;

import at.kurumi.calendar.CalendarProgram;
import at.kurumi.commands.Command;
import at.kurumi.db.Database;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BotStart {

    private static final Map<String, Command> COMMANDS = new HashMap<>();

    public void loginAndListen(String token) {
        final var client = DiscordClientBuilder.create(token).build()
                .login()
                .block();

        if(client == null) {
            return;
        }

        final var database = new Database();

        final var calendarProgram = new CalendarProgram(database);
        Command.guildCommand(client,
                136661702287556608L,
                calendarProgram.getName(),
                calendarProgram.getDescription(),
                calendarProgram.getOptions());
        COMMANDS.put(calendarProgram.getName(), calendarProgram);

        client.on(ApplicationCommandInteractionEvent.class, event -> {
            Optional.of(COMMANDS.get(event.getCommandName()))
                    .ifPresentOrElse(
                            command -> command.handle(event),
                            () -> event.reply("Unknown command"));
            return Mono.empty();
        }).subscribe();

        // has to be at the end
        client.onDisconnect().block();
    }

    public static void main(String[] args) {
        new BotStart().loginAndListen(args[0]);
    }

}
