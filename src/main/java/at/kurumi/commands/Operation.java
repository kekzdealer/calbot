package at.kurumi.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

import java.util.Map;

public abstract class Operation {

    protected final Logger LOG = LogManager.getLogger(getName() + "-operation");

    public abstract String getName();

    public abstract Mono<Void> handle(ChatInputInteractionEvent e);

    protected InteractionApplicationCommandCallbackSpec.Builder replyBuilder() {
        return InteractionApplicationCommandCallbackSpec.builder();
    }

    public static void insertIntoMap(Map<String, Operation> operations, Operation instance) {
        operations.put(instance.getName(), instance);
    }
}
