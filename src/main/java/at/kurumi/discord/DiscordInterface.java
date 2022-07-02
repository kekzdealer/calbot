package at.kurumi.discord;

import at.kurumi.LoggerFacade;
import at.kurumi.discord.commands.Command;
import at.kurumi.discord.commands.CommandUtil;

import at.kurumi.discord.commands.purchasing.sub.AddCommand;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.object.presence.Status;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
@Startup
public class DiscordInterface {

    private static final LoggerFacade log = LoggerFacade.getLogger(DiscordInterface.class);

    private static final long KURIS_LAB_GUILD_ID = 136661702287556608L;

    private final Map<String, Command> commands = new HashMap<>();

    /*
        This list might grow very long in the future, so I'm using field injection instead of constructor injection.
        I don't want a constructor with more than or so parameters.
         */
    @Inject private AddCommand addCommand;

    private GatewayDiscordClient discordClient;

    @PostConstruct
    public void onConstruct() {
        log.info("Constructing Discord Interface");
        discordClient = DiscordClientBuilder.create("token").build()
                .login()
                .doOnError(e -> log.error("Failed to authenticate with Discord"))
                .doOnSuccess(e -> log.trace("Connected to Discord"))
                .block();
        if (discordClient == null) {
            log.error("Discord client is null. Interface will not function");
            return;
        }

        discordClient.on(ReadyEvent.class)
                .doOnNext(e -> log.trace("Logged in as {}", e.getSelf().getUsername()))
                .subscribe();
        discordClient.on(ApplicationCommandInteractionEvent.class, this::delegateToProgram)
                .subscribe();
        discordClient.on(ChatInputAutoCompleteEvent.class, this::delegateAutoComplete)
                .subscribe();

        registerCommand(addCommand);

        setPresence(Status.ONLINE, "Cyberspace Shmyberspace");

        log.info("Constructed Discord Interface");
    }

    @PreDestroy
    public void onDestroy() {
        log.info("Destroying Discord Interface");
        discordClient.logout();
        log.info("Destroyed Discord Interface");
    }

    private void setPresence(Status status, String presence) {
        final var activity = ClientActivity.playing(presence);
        discordClient.updatePresence(ClientPresence.of(status, activity)).block();
    }

    private void sendEmbedInChannel(Snowflake guildId, Snowflake channelId, EmbedCreateSpec embedCreateSpec) {
        discordClient.getGuildById(guildId)
                .flatMap(guild -> guild.getChannelById(channelId)
                        .ofType(MessageChannel.class))
                .flatMap(channel -> channel.createMessage(embedCreateSpec))
                .subscribe();
    }

    private EmbedCreateSpec createOkEmbed(String title, /*User initiator, */String message) {
        return EmbedCreateSpec.builder()
                .color(Color.GREEN)
                .title(title)
                .description(message)
                //.addField("Initiator", initiator.getMention(), false)
                .build();
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
        log.info("Registered {} command", command.getName());
    }

}
