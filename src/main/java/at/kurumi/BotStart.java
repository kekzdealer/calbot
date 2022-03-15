package at.kurumi;

import at.kurumi.calendar.CalendarProgram;
import at.kurumi.commands.Command;
import at.kurumi.register.RegisterProgram;
import at.kurumi.user.UserProgram;
import at.kurumi.work.WorkProgram;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Startup
public class BotStart {

    private static final Logger LOG = LogManager.getLogger();
    private static final long KURIS_LAB_GUILD_ID = 136661702287556608L;

    private final Map<String, Command> commands = new HashMap<>();

    /*
    This list might grow very long in the future, so I'm using field injection instead of constructor injection.
    I don't want a constructor with more than or so parameters.
     */
    @Inject private CalendarProgram calendarProgram;
    @Inject private UserProgram userProgram;
    @Inject private ShutdownProgram shutdownProgram;
    @Inject private RegisterProgram registerProgram;
    @Inject private WorkProgram workProgram;

    private GatewayDiscordClient discordClient;

    @PostConstruct
    public void startup() {
        discordClient = DiscordClientBuilder.create("token").build()
                .login()
                .doOnError(e -> LOG.error("Failed to authenticate with Discord"))
                .doOnSuccess(e -> LOG.info("Connected to Discord"))
                .block();
        if (discordClient == null) {
            throw new RuntimeException("GatewayDiscordClient is null");
        }

        discordClient.on(ReadyEvent.class)
                .doOnNext(e -> LOG.info("Logged in as {}", e.getSelf().getUsername()))
                .subscribe();
        discordClient.on(ApplicationCommandInteractionEvent.class, this::delegateToProgram)
                .subscribe();
        discordClient.on(ChatInputAutoCompleteEvent.class, this::delegateAutoComplete)
                .subscribe();

        registerCommand(calendarProgram );
        registerCommand(userProgram);
        registerCommand(shutdownProgram);
        registerCommand(registerProgram);
        registerCommand(workProgram);
    }

    @PreDestroy
    public void shutdown() {
        discordClient.logout();
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

    private void registerCommand(Command command) {
        Command.guildCommand(discordClient,
                KURIS_LAB_GUILD_ID,
                command.getName(),
                command.getDescription(),
                command.getOptions());
        commands.put(command.getName(), command);
        LOG.info("Registered {} command", command.getName());
    }

}
