package at.kurumi.calendar;

import at.kurumi.calendar.operations.CreateCommand;
import at.kurumi.calendar.operations.DeleteCommand;
import at.kurumi.calendar.operations.ShareCommand;
import at.kurumi.calendar.operations.TodayCommand;
import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalendarCommand extends Command {

    private final List<Command> operations = new ArrayList<>();

    @Inject private CreateCommand createCommand;
    @Inject private DeleteCommand deleteCommand;
    @Inject private TodayCommand todayCommand;
    @Inject private ShareCommand shareCommand;

    @PostConstruct
    public void init() {
        operations.add(createCommand);
        operations.add(deleteCommand);
        operations.add(todayCommand);
        operations.add(shareCommand);
    }

    @Override
    public String getName() {
        return "calendar";
    }

    @Override
    public String getDescription() {
        return "Create and manage calender events";
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return Collections.singletonList(OPERATION_OPTION_DATA);
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        return super.throughOperation(e, operations, CommandUtil.getRequiredParameterAsString(e, PARAM_OPERATION));
    }
}
