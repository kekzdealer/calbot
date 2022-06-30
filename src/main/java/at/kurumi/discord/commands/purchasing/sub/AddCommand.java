package at.kurumi.discord.commands.purchasing.sub;

import at.kurumi.discord.commands.Command;
import at.kurumi.discord.commands.CommandUtil;
import at.kurumi.purchasing.GroceriesI;
import at.kurumi.purchasing.ShoppingList;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import jakarta.ejb.Stateless;
import reactor.core.publisher.Mono;

import jakarta.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class AddCommand extends Command {

    private static final String PARAM_NAME = "name";
    private static final String PARAM_NOTE = "note";

    private final ShoppingList shoppingList;

    @Inject
    public AddCommand(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "Add an item to the grocery list";
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        return List.of(
                CommandUtil.optionData(PARAM_NAME,
                        "Item name",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        true),
                CommandUtil.optionData(PARAM_NOTE,
                        "Quantity, Amount, Type, ...",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        false)
        );
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent e) {
        final var name = CommandUtil.getRequiredParameterAsString(e, PARAM_NAME);
        final var note = CommandUtil.getParameterAsString(e, PARAM_NOTE);
        shoppingList.add(name, note.orElse(""));

        return super.simpleReply(e, "Added");
    }

    @Override
    public Mono<Void> handleAutoComplete(ChatInputAutoCompleteEvent e) {
        final var focusedOption = e.getFocusedOption();
        final var fragment = focusedOption.getValue()
                .map(ApplicationCommandInteractionOptionValue::asString)
                .orElse("");
        if(focusedOption.getName().equals(PARAM_NAME)) {
            return e.respondWithSuggestions(toSuggestionList(shoppingList.getSuggestionsForName(fragment)));
        } else {
            final var name = e.getOption(PARAM_NAME)
                    .flatMap(ApplicationCommandInteractionOption::getValue)
                    .map(ApplicationCommandInteractionOptionValue::asString)
                    .orElseThrow(() -> new RuntimeException("Why is there no value?"));
            return e.respondWithSuggestions(toSuggestionList(shoppingList.getSuggestionsForNote(name, fragment)));
        }
    }

    private List<ApplicationCommandOptionChoiceData> toSuggestionList(List<GroceriesI> list) {
        return list.stream()
                .map(item -> ApplicationCommandOptionChoiceData.builder()
                        .name(PARAM_NAME)
                        .value(item.getName())
                        .build()).collect(Collectors.toList());
    }
}
