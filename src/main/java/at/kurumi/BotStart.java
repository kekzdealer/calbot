package at.kurumi;

import at.kurumi.calendar.CalendarProgram;
import at.kurumi.calendar.EventSLO;
import at.kurumi.commands.Command;
import at.kurumi.user.UserProgram;
import at.kurumi.user.UserSLO;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BotStart {

    private static final Logger LOG = LogManager.getLogger();

    private static final Map<String, Command> COMMANDS = new HashMap<>();

    public void loginAndListen(String token) {
        final var client = DiscordClientBuilder.create(token).build()
                .login()
                .block();

        if(client == null) {
            return;
        }

        final var database = new Database();
        final var userSLO = new UserSLO(database);
        final var eventSLO = new EventSLO(database);

        registerCommand(client, new CalendarProgram(eventSLO, userSLO));
        registerCommand(client, new UserProgram(userSLO));

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

    public void registerCommand(GatewayDiscordClient client, Command command) {
        Command.guildCommand(client,
                136661702287556608L,
                command.getName(),
                command.getDescription(),
                command.getOptions());
        COMMANDS.put(command.getName(), command);
        LOG.info("Registered {} command", command.getName());
    }

    public static void main(String[] args) {
        new BotStart().loginAndListen(args[0]);
    }

}
