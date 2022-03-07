package at.kurumi;

import at.kurumi.commands.Command;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * Terminates the Discord gateway connection through a logout event.
 */
public class ShutdownProgram extends Command {

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
    public Mono<Void> handle(ApplicationCommandInteractionEvent e) {
        LOG.info("Shutting down");
        return e.getClient().logout();
    }
}
