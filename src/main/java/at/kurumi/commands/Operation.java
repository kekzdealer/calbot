package at.kurumi.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public abstract class Operation {

    protected final Logger LOG = LogManager.getLogger(getName() + "Operation");

    public abstract String getName();

    public abstract void handle(ChatInputInteractionEvent e);

    public static void insertIntoMap(Map<String, Operation> operations, Operation instance) {
        operations.put(instance.getName(), instance);
    }
}
