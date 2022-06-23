package at.kurumi.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ImmutableApplicationCommandOptionData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

import java.util.List;

public abstract class Command {

    protected static final String PARAM_OPERATION = "operation";

    public static final ImmutableApplicationCommandOptionData OPERATION_OPTION_DATA = CommandUtil.optionData(
            PARAM_OPERATION,
            "The operation to execute",
            ApplicationCommandOption.Type.SUB_COMMAND.getValue(),
            true
    );

    protected final Logger LOG = LogManager.getLogger();

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
     * @return empty Mono after a reply
     */
    public abstract Mono<Void> handle(ChatInputInteractionEvent e);

    protected Mono<Void> throughOperation(ChatInputInteractionEvent e, List<Command> subCommands, String command) {
        return subCommands.stream()
                .filter(c -> c.getName().equals(command))
                .findFirst()
                .map(c -> c.handle(e))
                .orElseGet(() -> e.reply(String.format("There is no operation called %s, sorry", command)));
    }

    /**
     * Deliver autocomplete suggestions
     *
     * @param e and event to respond to
     * @return empty Mono after a reply
     */
    public Mono<Void> handleAutoComplete(ChatInputAutoCompleteEvent e) {
        return Mono.empty();
    }

    /**
     * Build a simple String reply.
     *
     * @param content reply content
     * @return a reply specification callback object
     */
    protected Mono<Void> simpleReply(ChatInputInteractionEvent e, String content) {
        return e.reply(InteractionApplicationCommandCallbackSpec.builder()
                .content(content)
                .build());
    }

    /**
     * Build a simple String reply, but with "Missing input argument: " prepended.
     *
     * @param content reply content
     * @return a reply specification callback object
     */
    protected Mono<Void> missingArgumentReply(ChatInputInteractionEvent e, String content) {
        return simpleReply(e, String.format("Missing input argument: %s", content));
    }

    /**
     * Build a simple String reply, but with "Invalid argument format: " prepended.
     *
     * @param content reply content
     * @return a reply specification callback object
     */
    protected Mono<Void> invalidArgumentReply(ChatInputInteractionEvent e, String content) {
        return simpleReply(e, String.format("Invalid input argument: %s", content));
    }
}
