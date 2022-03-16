package at.kurumi.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

import java.util.Map;

public abstract class Operation {

    /**
     * Discord-Interface level logger is mostly for logging actual errors.
     */
    protected final Logger LOG = LogManager.getLogger(getClass().getSimpleName());

    public abstract String getName();

    public abstract Mono<Void> handle(ChatInputInteractionEvent e);


}
