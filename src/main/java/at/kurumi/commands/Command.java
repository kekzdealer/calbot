package at.kurumi.commands;

import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ImmutableApplicationCommandOptionData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

import java.util.List;

public abstract class Command {

    public static final ImmutableApplicationCommandOptionData TARGET_OPTION_DATA = CommandUtil.optionData(
            "target",
            "The target of this operation. If supported, the default target will be used, when left blank",
            ApplicationCommandOption.Type.STRING.getValue(),
            true
    );
    public static final ImmutableApplicationCommandOptionData OPERATION_OPTION_DATA = CommandUtil.optionData(
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
     * @return empty Mono after a reply
     */
    public abstract Mono<Void> handle(ChatInputInteractionEvent e);

    /**
     * Deliver autocomplete suggestions
     *
     * @param e and event to respond to
     * @return empty Mono after a reply
     */
    public Mono<Void> handleAutoComplete(ChatInputAutoCompleteEvent e) {
        return Mono.empty();
    }
}
