package at.kurumi.commands;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.discordjson.json.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class CommandUtil {

    private CommandUtil() {

    }

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

    public static void removeGuildCommand(GatewayDiscordClient client, long guildId,
                                          String commandName) {
        final var applicationId = client.getApplicationInfo().block()
                .getId()
                .asLong();
        final var applicationService = client.getRestClient().getApplicationService();
        final var registeredCommands = applicationService
                .getGuildApplicationCommands(applicationId, guildId)
                .collectMap(ApplicationCommandData::name)
                .block();

        final var commandId = Long.parseLong(registeredCommands.get(commandName).id());

        applicationService.deleteGuildApplicationCommand(applicationId, guildId, commandId).subscribe();
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
    public static ImmutableApplicationCommandOptionData optionData(String name, String desc, int type, boolean required) {
        return ApplicationCommandOptionData.builder()
                .name(name)
                .description(desc)
                .type(type)
                .required(required)
                .build();
    }


    public static Optional<String> getCommandValue(ChatInputInteractionEvent e, String name) {
        return e.getOption(name)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString);
    }

    public static Optional<Instant> getCommandValueAsUTCInstant(ChatInputInteractionEvent e, String name)
            throws DateTimeParseException {
        // 1) create the parsing pattern
        final var formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");
        return getCommandValue(e, name).map(formatter::parse)
                // 2) java.time.Instant representation of the date-time from the user's time zone
                .map(Instant::from)
                // 3) User local Instant is combined with time zone information
                .map(instant -> ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()))
                // 4) Convert back to Instant, but this time normalized to UTC
                .map(ZonedDateTime::toInstant);
    }

    public static long extractDiscordUserId(ChatInputInteractionEvent e) {
        return e.getInteraction().getUser().getId().asLong();
    }

}
