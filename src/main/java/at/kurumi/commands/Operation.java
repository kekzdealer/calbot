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

    public static void insertIntoMap(Map<String, Operation> operations, Operation instance) {
        operations.put(instance.getName(), instance);
    }
}
