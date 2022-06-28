package at.kurumi.logging;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
public class LoggingRouter {

    private static final Logger LOG = LogManager.getLogger();
    private Snowflake HOME_GUILD;
    private Snowflake OUTPUT_CHANNEL;
    private Snowflake ERROR_CHANNEL;
    private final GatewayDiscordClient discordClient;

    public LoggingRouter(GatewayDiscordClient discordClient) {
        this.discordClient = discordClient;
    }

    @PostConstruct
    public void init() {
        HOME_GUILD = Snowflake.of(136661702287556608L);
        OUTPUT_CHANNEL = Snowflake.of(988729253900201984L);
        ERROR_CHANNEL = Snowflake.of(988729285885972520L);
    }

    public void internalInfo(String action, String message) {
        LOG.info(intLogFormatter(action, message));
    }

    public void internalWarn(String action, String message) {
        LOG.warn(intLogFormatter(action, message));
    }

    public void internalError(String action, String message) {
        LOG.warn(intLogFormatter(action, message));
    }

    private String intLogFormatter(String action, String message) {
        return String.format("<%s> %s", action, message);
    }

    public void diLog(String action, User initiator, String message) {
        sendMessage(OUTPUT_CHANNEL, createLogEmbed(action, initiator, message));
    }

    public void diError(String action, User initiator, String message) {
        sendMessage(ERROR_CHANNEL, createErrorEmbed(action, initiator, message));
    }

    private EmbedCreateSpec createLogEmbed(String action, User initiator, String message) {
        return EmbedCreateSpec.builder()
                .color(Color.GREEN)
                .title(action)
                .description(message)
                .addField("Initiator", initiator.getMention(), false)
                .build();
    }

    private EmbedCreateSpec createErrorEmbed(String action, User initiator, String message) {
        return EmbedCreateSpec.builder()
                .color(Color.RED)
                .title(action)
                .description(message)
                .addField("Initiator", initiator.getMention(), false)
                .build();
    }

    private void sendMessage(Snowflake channelId, EmbedCreateSpec embedCreateSpec) {
        discordClient.getGuildById(HOME_GUILD)
                .flatMap(guild -> guild.getChannelById(channelId)
                        .ofType(MessageChannel.class))
                .flatMap(channel -> channel.createMessage(embedCreateSpec))
                .subscribe();
    }

}
