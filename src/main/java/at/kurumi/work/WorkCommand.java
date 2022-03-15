package at.kurumi.work;

import at.kurumi.commands.Command;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import jakarta.ejb.Stateful;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Stateful
public class WorkCommand extends Command {

    private static final long WORK_DURATION_HOURS = 8L;
    private static final long WORK_DURATION_MINUTES = 24L;

    private final Clock clock;

    private PrivateChannel replyChannel;
    private Instant done;


    public WorkCommand(Clock clock) {
        this.clock = clock;
    }

    @Override
    public String getName() {
        return "work";
    }

    @Override
    public String getDescription() {
        return "Some utility regarding an occupation";
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        replyChannel = e.getInteraction().getUser().getPrivateChannel().block();
        done = Instant.now()
                .plus(WORK_DURATION_HOURS, ChronoUnit.HOURS)
                .plus(WORK_DURATION_MINUTES, ChronoUnit.MINUTES);

        clock.everyMinute(() -> {
            if(Instant.now().isAfter(done)) {
                replyChannel.createMessage("Work done").block();
            }
        });

        return null;
    }

    @Override
    public Mono<Void> handleAutoComplete(ChatInputAutoCompleteEvent e) {
        return Mono.empty();
    }

}
