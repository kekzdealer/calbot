package at.kurumi;

import at.kurumi.calendar.CalendarProgram;
import at.kurumi.calendar.Clock;
import at.kurumi.calendar.EventSLO;
import at.kurumi.commands.Command;
import at.kurumi.register.RegisterProgram;
import at.kurumi.user.UserProgram;
import at.kurumi.user.UserSLO;
import at.kurumi.work.WorkProgram;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BotStart {

    private static final Logger LOG = LogManager.getLogger();
    private static final long KURIS_LAB_GUILD_ID = 136661702287556608L;

    private final Map<String, Command> commands = new HashMap<>();

    public GatewayDiscordClient login(String token) {
        final var gatewayDiscordClient = DiscordClientBuilder.create(token).build()
                .login()
                .doOnError(e -> LOG.error("Failed to authenticate with Discord"))
                .doOnSuccess(e -> LOG.info("Connected to Discord"))
                .block();

        if (gatewayDiscordClient == null) {
            throw new RuntimeException("GatewayDiscordClient is null");
        }

        gatewayDiscordClient.on(ReadyEvent.class)
                .doOnNext(e -> LOG.info("Logged in as {}", e.getSelf().getUsername()))
                .subscribe();

        return gatewayDiscordClient;
    }

    public void setup(GatewayDiscordClient client) {
        final var database = new Database();
        final var userSLO = new UserSLO(database);
        final var eventSLO = new EventSLO(database);

        final var clock = new Clock();

        registerCommand(client, new CalendarProgram(eventSLO, userSLO));
        registerCommand(client, new UserProgram(userSLO));
        registerCommand(client, new ShutdownProgram());
        registerCommand(client, new RegisterProgram(userSLO));
        registerCommand(client, new WorkProgram(clock));

        client.on(ApplicationCommandInteractionEvent.class, this::delegateToProgram)
                .subscribe();
        client.on(ChatInputAutoCompleteEvent.class, this::delegateAutoComplete)
                .subscribe();
        client.onDisconnect().block();
    }

    private Mono<Void> delegateToProgram(ApplicationCommandInteractionEvent event) {
        return Optional.ofNullable(commands.get(event.getCommandName()))
                .map(command -> command.handle(event))
                .orElseThrow(IllegalStateException::new);
    }

    private Mono<Void> delegateAutoComplete(ChatInputAutoCompleteEvent event) {
        return Optional.ofNullable(commands.get(event.getCommandName()))
                .map(command -> command.handleAutoComplete(event))
                .orElseThrow(IllegalStateException::new);
    }

    private void registerCommand(GatewayDiscordClient client, Command command) {
        Command.guildCommand(client,
                KURIS_LAB_GUILD_ID,
                command.getName(),
                command.getDescription(),
                command.getOptions());
        commands.put(command.getName(), command);
        LOG.info("Registered {} command", command.getName());
    }

    public static void main(String[] args) {
        final var o = new BotStart();
        o.setup(o.login(args[0]));
    }

}
