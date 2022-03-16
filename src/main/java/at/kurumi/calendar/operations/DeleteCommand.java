package at.kurumi.calendar.operations;

import at.kurumi.calendar.EventSLO;
import at.kurumi.commands.Command;
import at.kurumi.commands.CommandUtil;
import at.kurumi.user.UserSLO;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.util.List;

@Stateless
public class DeleteCommand extends Command {

    private static final String PARAM_TARGET = "target";

    private final EventSLO eventSLO;
    private final UserSLO userSLO;

    @Inject
    public DeleteCommand(EventSLO eventSLO, UserSLO userSLO) {
        this.eventSLO = eventSLO;
        this.userSLO = userSLO;
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "Delete an event";
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return List.of(CommandUtil.optionData(PARAM_TARGET,
                "The event to delete",
                ApplicationCommandOption.Type.INTEGER.getValue(),
                true));
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        final var discordId = CommandUtil.extractDiscordUserId(e);
        final var user = userSLO.getUserByDiscordId(discordId);

        return user.map(u -> {
            final var target = CommandUtil.getRequiredParameterAsLong(e, PARAM_TARGET);
            if(eventSLO.deleteEventById(u, target.intValue())) {
                return super.simpleReply(e, "Deleted event with id " + target);
            } else {
                return super.simpleReply(e, "I could not delete the even with id " + target);
            }
        }).orElseGet(() -> {
            LOG.error("Failed to retrieve user data for user with discordId {}", discordId);
            return super.simpleReply(e, "I could not retrieve your user data.");
        });
    }
}
