package at.kurumi.calendar;

import at.kurumi.calendar.operations.CreateOperation;
import at.kurumi.calendar.operations.DeleteOperation;
import at.kurumi.calendar.operations.TodayOperation;
import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarProgram extends Command {

    private final Map<String, Operation> operations = new HashMap<>();

    // to be injected
    public CalendarProgram(EventSLO eventSLO, UserSLO userSLO) {
        Operation.insertIntoMap(operations, new CreateOperation(eventSLO, userSLO));
        Operation.insertIntoMap(operations, new TodayOperation(eventSLO, userSLO));
        Operation.insertIntoMap(operations, new DeleteOperation(eventSLO, userSLO));
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
                Command.optionData(
                        "argument0",
                        "Optional first argument",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        false
                ),
                Command.optionData(
                        "argument1",
                        "Optional second argument",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        false
                ),
                Command.optionData(
                        "argument3",
                        "Optional third argument",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        false
                ));
    }

    @Override
    public void handle(ApplicationCommandInteractionEvent e) {
        if(e instanceof ChatInputInteractionEvent) {
            final var e_ = (ChatInputInteractionEvent) e;

            final var opName = CommandUtil.getCommandValue(e_, Command.OPERATION_OPTION_DATA.name());
            operations.get(opName).handle(e_);
        } else {
            e.reply("Input event type unsupported by this program");
        }
    }

}
