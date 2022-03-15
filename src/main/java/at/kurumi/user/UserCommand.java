package at.kurumi.user;

import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import at.kurumi.user.operations.DeleteOperation;
import at.kurumi.user.operations.HelloOperation;
import at.kurumi.user.operations.ListOperation;
import at.kurumi.user.operations.NicknameOperation;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class UserCommand extends Command {

    private final Map<String, Operation> operations = new HashMap<>();

    private final UserSLO userSLO;

    @Inject
    public UserCommand(UserSLO userSLO) {
        this.userSLO = userSLO;
    }

    @PostConstruct
    public void init() {
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
                CommandUtil.optionData(
                        "operation",
                        "What operation to execute",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        true
                ),
                CommandUtil.optionData(
                        "argument0",
                        "Optional first argument",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        false
                ));
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        return CommandUtil.getCommandValue(e, "operation")
                .map(opName -> operations.get(opName).handle(e))
                .orElseGet(() -> e.reply("Operation type is either missing or unsupported."));
    }

}
