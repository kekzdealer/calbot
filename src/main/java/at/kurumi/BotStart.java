package at.kurumi;

import at.kurumi.commands.CalenderEventCommand;
import at.kurumi.commands.Command;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
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

        final var eventCommand = new CalenderEventCommand();
        Command.guildCommand(client,
                136661702287556608L,
                eventCommand.getName(),
                eventCommand.getDescription(),
                eventCommand.getOptions());

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
