package at.kurumi.user;

import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.operations.HelloOperation;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProgram extends Command {

    private final Map<String, Operation> operations = new HashMap<>();

    // to be injected
    public UserProgram(UserSLO userSLO) {
        Operation.insertIntoMap(operations, new HelloOperation(userSLO));
    }

    @Override
    public String getName() {
        return "user";
    }

    @Override
    public String getDescription() {
        return "Manage your user profile";
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return List.of(
                super.optionData(
                        "operation",
                        "What operation to execute",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        true
                ),
                super.optionData(
                        "argument0",
                        "Optional first argument",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        false
                ));
    }

    @Override
    public void handle(ApplicationCommandInteractionEvent e) {
        if(e instanceof ChatInputInteractionEvent) {
            final var e_ = (ChatInputInteractionEvent) e;

            final var opName = CommandUtil.getCommandValue(e_, "operation");
            operations.get(opName).handle(e_);
        } else {
            e.reply("Input event type unsupported by this program");
        }
    }

}
