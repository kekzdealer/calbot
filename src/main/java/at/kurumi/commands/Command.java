package at.kurumi.commands;

import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandOptionData;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;

import java.util.List;

public abstract class Command {

    /**
     * Get the list of options for this command.
     *
     * @return List of option data
     */
    public abstract List<ApplicationCommandOptionData> getOptions();

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
    protected ImmutableApplicationCommandOptionData optionData(String name, String desc, int type, boolean required) {
        return ApplicationCommandOptionData.builder()
                .name(name)
                .description(desc)
                .type(type)
                .required(required)
                .build();
    }

}
