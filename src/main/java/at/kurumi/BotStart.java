package at.kurumi;

import at.kurumi.commands.calendar.CalendarCommand;
import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.register.RegisterCommand;
import at.kurumi.commands.shutdown.ShutdownCommand;
import at.kurumi.commands.user.UserCommand;
import at.kurumi.commands.work.WorkCommand;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.object.presence.Status;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Schedule;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

import java.util.*;

@Startup
public class BotStart {

    private static final Logger LOG = LogManager.getLogger();
    private static final long KURIS_LAB_GUILD_ID = 136661702287556608L;

    private final Map<String, Command> commands = new HashMap<>();

    /*
    This list might grow very long in the future, so I'm using field injection instead of constructor injection.
    I don't want a constructor with more than or so parameters.
     */
    @Inject private CalendarCommand calendarCommand;
    @Inject private UserCommand userCommand;
    @Inject private ShutdownCommand shutdownCommand;
    @Inject private RegisterCommand registerCommand;
    @Inject private WorkCommand workCommand;

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

        registerCommand(calendarCommand);
        registerCommand(userCommand);
        registerCommand(shutdownCommand);
        registerCommand(registerCommand);
        registerCommand(workCommand);
    }

    @PreDestroy
    public void shutdown() {
        discordClient.logout();
    }

    /**
     * Update Discord presence with a new "X is playing..." message.
     */
    @Schedule(hour="*/1", info="Update the Discord presence every hour", persistent = false)
    public void cyclePresence() {
        final var r= new Random();
        final var dia = List.of("Digital Interaction Assistant",
                "Dynamically Integrating Assistant",
                "Delectable Illumination Assistant",
                "Dutiful Irrigation Assistant",
                "Dashing Incident Assistant",
                "Dubious Investment Advisor");
        final var selection = dia.get(r.nextInt(dia.size()));
        final var activity = ClientActivity.playing(selection);
        final var status = Status.ONLINE;
        discordClient.updatePresence(ClientPresence.of(status, activity)).block();
    }

    private Mono<Void> delegateToProgram(ApplicationCommandInteractionEvent event) {
        return Optional.ofNullable(commands.get(event.getCommandName()))
                .map(command -> command.handle((ChatInputInteractionEvent) event))
                .orElseThrow(IllegalStateException::new);
    }

    private Mono<Void> delegateAutoComplete(ChatInputAutoCompleteEvent event) {
        return Optional.ofNullable(commands.get(event.getCommandName()))
                .map(command -> command.handleAutoComplete(event))
                .orElseThrow(IllegalStateException::new);
    }

    private void registerCommand(Command command) {
        CommandUtil.guildCommand(discordClient,
                KURIS_LAB_GUILD_ID,
                command.getName(),
                command.getDescription(),
                command.getOptions());
        commands.put(command.getName(), command);
        LOG.info("Registered {} command", command.getName());
    }

}
