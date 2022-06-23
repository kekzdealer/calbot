package at.kurumi.discord.commands.user;

import at.kurumi.discord.commands.Command;
import at.kurumi.discord.commands.CommandUtil;
import at.kurumi.discord.commands.user.sub.DeleteCommand;
import at.kurumi.discord.commands.user.sub.NicknameCommand;
import at.kurumi.discord.commands.user.sub.ShowCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class UserCommand extends Command {

    private final List<Command> operations = new ArrayList<>();

    @Inject private DeleteCommand deleteCommand;
    @Inject private NicknameCommand nicknameCommand;
    @Inject private ShowCommand showCommand;

    @PostConstruct
    public void init() {
        operations.add(deleteCommand);
        operations.add(nicknameCommand);
        operations.add(showCommand);
    }

    @Override
    public String getName() {
        return "profile";
    }

    @Override
    public String getDescription() {
        return "Manage your user profile";
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
