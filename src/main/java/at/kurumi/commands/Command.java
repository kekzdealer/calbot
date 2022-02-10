package at.kurumi.commands;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandOptionData;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public abstract class Command {

    public static final ImmutableApplicationCommandOptionData TARGET_OPTION_DATA = optionData(
            "target",
            "The target of this operation. If supported, the default target will be used, when left blank",
            ApplicationCommandOption.Type.STRING.getValue(),
            false
    );
    public static final ImmutableApplicationCommandOptionData OPERATION_OPTION_DATA = optionData(
            "operation",
            "What operation to execute",
            ApplicationCommandOption.Type.STRING.getValue(),
            true
    );

    protected final Logger LOG = LogManager.getLogger(getName() + "-command");

    /**
     * Get the name this command should be listed under in Discord.
     *
     * @return name String
     */
    public abstract String getName();

    /**
     * Get a description of this command.
     *
     * @return description String
     */
    public abstract String getDescription();

    /**
     * Get the list of options for this command.
     *
     * @return List of option data
     */
    public abstract List<ApplicationCommandOptionData> getOptions();

    /**
     * Take care of an event and respond.
     *
     * @param e an event to respond to
     */
    public abstract void handle(ApplicationCommandInteractionEvent e);

    /**
     * Create a global scoped command. Can be used everywhere. Global commands have a TTL of up to 1 hour so may be
     * slow to appear on discord.
     *
     * @param client client instance
     * @param name command name
     * @param desc command description
     * @param options command parameters
     */
    public static void globalCommand(GatewayDiscordClient client, String name, String desc,
                              List<ApplicationCommandOptionData> options) {
        final var command = commandRequest(name, desc, options);
        final var applicationId = client.getRestClient().getApplicationId().block();
        client.getRestClient().getApplicationService()
                .createGlobalApplicationCommand(applicationId, command)
                .subscribe();
    }

    /**
     * Create a guild scoped command. Can only be used in guild text channels.
     *
     * @param client client instance
     * @param guildId guild id
     * @param name command name
     * @param desc command description
     * @param options command parameters
     */
    public static void guildCommand(GatewayDiscordClient client, long guildId, String name, String desc,
                             List<ApplicationCommandOptionData> options) {
        final var command = commandRequest(name, desc, options);
        final var applicationId = client.getRestClient().getApplicationId().block();
        client.getRestClient().getApplicationService()
                .createGuildApplicationCommand(applicationId, guildId, command)
                .subscribe();
    }

    /**
     * Encapsulated command builder.
     *
     * @param name    command name
     * @param desc    command description
     * @param options command parameters
     * @return immutable command request ready for submission
     */
    private static ImmutableApplicationCommandRequest commandRequest(String name, String desc,
                                                             List<ApplicationCommandOptionData> options) {
        return ApplicationCommandRequest.builder()
                .name(name)
                .description(desc)
                .addAllOptions(options)
                .build();
    }

    /**
     * Encapsulated command option builder.
     *
     * @param name option name
     * @param desc option description
     * @param type option type -> {@link discord4j.core.object.command.ApplicationCommandOption.Type}
     * @param required required option flag
     * @return immutable command option
     */
    protected static ImmutableApplicationCommandOptionData optionData(String name, String desc, int type, boolean required) {
        return ApplicationCommandOptionData.builder()
                .name(name)
                .description(desc)
                .type(type)
                .required(required)
                .build();
    }

}
