package at.kurumi.register;

import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.register.operations.RegisterOperation;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.util.List;

@Stateless
public class RegisterCommand extends Command {

    private final RegisterOperation registerOperation;

    @Inject
    public RegisterCommand(UserSLO userSLO) {
        registerOperation = new RegisterOperation(userSLO);
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "Sign up to use this discord interface.";
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return List.of(
                CommandUtil.optionData(
                        "timezone",
                        "Enter your timezone as an offset relative to UTC like this: UTC+1",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        true
                ));
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        return registerOperation.handle(e);
    }
}
