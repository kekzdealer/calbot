package at.kurumi.calendar;

import at.kurumi.calendar.operations.CreateOperation;
import at.kurumi.calendar.operations.DeleteOperation;
import at.kurumi.calendar.operations.ShareOperation;
import at.kurumi.calendar.operations.TodayOperation;
import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CalendarCommand extends Command {

    private final Map<String, Operation> operations = new HashMap<>();

    @Inject
    public CalendarCommand(EventSLO eventSLO, UserSLO userSLO) {
        Operation.insertIntoMap(operations, new CreateOperation(eventSLO, userSLO));
        Operation.insertIntoMap(operations, new TodayOperation(eventSLO, userSLO));
        Operation.insertIntoMap(operations, new DeleteOperation(eventSLO, userSLO));
        Operation.insertIntoMap(operations, new ShareOperation(eventSLO, userSLO));
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
        return List.of(
                Command.TARGET_OPTION_DATA,
                Command.OPERATION_OPTION_DATA,
                CommandUtil.optionData(
                        "argument0",
                        "Optional first argument",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        false
                ),
                CommandUtil.optionData(
                        "argument1",
                        "Optional second argument",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        false
                ),
                CommandUtil.optionData(
                        "argument2",
                        "Optional third argument",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        false
                ));
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        return CommandUtil.getCommandValue(e, Command.OPERATION_OPTION_DATA.name())
                .map(opName -> operations.get(opName).handle(e))
                .orElseGet(() -> e.reply("Operation type is either missing or unsupported."));
    }

    @Override
    public Mono<Void> handleAutoComplete(ChatInputAutoCompleteEvent e) {
        if(e.getFocusedOption().getName().equals("operation")) {
            return e.respondWithSuggestions(operations.keySet().stream()
                    .map(opName -> ApplicationCommandOptionChoiceData.builder()
                            .name(opName)
                            .value(opName)
                            .build())
                    .collect(Collectors.toList()));
        } else {
            return Mono.empty();
        }
    }
}
