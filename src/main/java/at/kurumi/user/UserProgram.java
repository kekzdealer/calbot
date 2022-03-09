package at.kurumi.user;

import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.operations.DeleteOperation;
import at.kurumi.user.operations.HelloOperation;
import at.kurumi.user.operations.ListOperation;
import at.kurumi.user.operations.NicknameOperation;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProgram extends Command {

    private final Map<String, Operation> operations = new HashMap<>();

    // to be injected
    public UserProgram(UserSLO userSLO) {
        Operation.insertIntoMap(operations, new HelloOperation(userSLO));
        Operation.insertIntoMap(operations, new ListOperation(userSLO));
        Operation.insertIntoMap(operations, new NicknameOperation(userSLO));
        Operation.insertIntoMap(operations, new DeleteOperation(userSLO));
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
                Command.optionData(
                        "operation",
                        "What operation to execute",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        true
                ),
                Command.optionData(
                        "argument0",
                        "Optional first argument",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        false
                ));
    }

    @Override
    public Mono<Void> handle(ApplicationCommandInteractionEvent e) {
        if(e instanceof ChatInputInteractionEvent) {
            final var e_ = (ChatInputInteractionEvent) e;

            return CommandUtil.getCommandValue(e_, "operation")
                    .map(opName -> operations.get(opName).handle(e_))
                    .orElseGet(() -> e.reply("Operation type is either missing or unsupported."));
        } else {
            final var messageSpec = InteractionApplicationCommandCallbackSpec.builder();
            messageSpec.content("Input event type unsupported by this program");
            return e.reply(messageSpec.build());
        }
    }

}
