package at.kurumi.user;

import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.commands.Operation;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProgram extends Command {

    private static final Logger LOG = LogManager.getLogger("User");

    private final UserSLO userSLO;

    private final Map<String, Operation> operations = new HashMap<>();

    public UserProgram(UserSLO userSLO) {
        this.userSLO = userSLO;

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
                        "Optional argument",
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
        }
    }

    private void hello(ApplicationCommandInteractionEvent e) {
        final var discordId = 11313131231L;
        final var name = "UserXX11";

        // TODO insert into db
    }
}
