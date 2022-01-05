package at.kurumi.commands;

import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;

import java.util.List;

/**
 * <ol>
 *     <li>name:String</li>
 *     <li>from:String, yyyy-mm-ddThh:mm</li>
 *     <li>to:String, yyyy-mm-ddThh:mm</li>
 * </ol>
 */
public class CalenderEventCommand extends Command {

    private final List<ApplicationCommandOptionData> options;

    public CalenderEventCommand() {
        options = List.of(
                super.optionData(
                        "title",
                        "The event title",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        true
                ),
                super.optionData(
                        "from",
                        "Start date and time",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        true
                ),
                super.optionData(
                        "to",
                        "End date and time",
                        ApplicationCommandOption.Type.STRING.getValue(),
                        true
                ));
    }

    public List<ApplicationCommandOptionData> getOptions() {
        return options;
    }
}
