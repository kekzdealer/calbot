package at.kurumi;

import at.kurumi.commands.Command;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import jakarta.ejb.Stateless;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * Terminates the Discord gateway connection through a logout event.
 */
@Stateless
public class ShutdownCommand extends Command {

    @Override
    public String getName() {
        return "shutdown";
    }

    @Override
    public String getDescription() {
        return "Shut down the bot";
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        LOG.info("Shutting down");
        return e.getClient().logout();
    }

    @Override
    public Mono<Void> handleAutoComplete(ChatInputAutoCompleteEvent e) {
        return Mono.empty();
    }
}
