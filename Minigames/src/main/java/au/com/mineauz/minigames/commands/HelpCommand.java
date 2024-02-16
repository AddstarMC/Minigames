package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.set.ASetCommand;
import au.com.mineauz.minigames.commands.set.SetCommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class HelpCommand extends ACommand {
    private final Pattern NUM_PATTERN = Pattern.compile("^\\d*$");
    private final int COMMANDS_PER_SITE = 6; // just a random number. Change it if you know a better one!

    private static boolean sendHelpInfo(@NotNull CommandSender sender, @NotNull ICommandInfo setCommand) {
        if (setCommand.getPermission() == null || sender.hasPermission(setCommand.getPermission())) {
            if (setCommand.getAliases() != null && setCommand.getAliases().length > 0) {
                TextComponent.Builder info = Component.text();
                info.append(Component.join(JoinConfiguration.arrayLike(), Arrays.stream(setCommand.getAliases()).map(Component::text).toList()));

                MinigameMessageManager.sendMessage(sender, MinigameMessageType.NONE,
                        MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_HELP_INFO_HEADER,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), setCommand.getName())).appendNewline().
                                append(info.appendNewline().append(setCommand.getUsage()).appendNewline().append(setCommand.getDescription()).
                                        colorIfAbsent(NamedTextColor.WHITE)));//todo needs formatting (not hardcoded)
            } else {
                MinigameMessageManager.sendMessage(sender, MinigameMessageType.NONE,
                        MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_HELP_INFO_HEADER,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), setCommand.getName())).appendNewline().
                                append(setCommand.getUsage().appendNewline().append(setCommand.getDescription()).
                                        colorIfAbsent(NamedTextColor.WHITE)));//todo needs formatting (not hardcoded)
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public @NotNull String getName() {
        return "help";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_HELP_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_HELP_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.help";
    }

    private Component makePage(@NotNull Permissible permissible, int pageNumber) {
        List<ICommandInfo> allCommands = new ArrayList<>(CommandDispatcher.getCommands());
        allCommands.addAll(SetCommand.getSetCommands());
        // filter per permission
        allCommands = allCommands.stream().filter(cmd -> cmd.getPermission() == null || permissible.hasPermission(cmd.getPermission())).toList();

        final int numPages = (int) Math.ceil((float) allCommands.size() / COMMANDS_PER_SITE);
        pageNumber = Math.max(1, Math.min(pageNumber, numPages)); // stay in range

        final List<ICommandInfo> commandsOfPage = allCommands.subList(COMMANDS_PER_SITE * (pageNumber - 1), Math.min(allCommands.size(), pageNumber * COMMANDS_PER_SITE));
        // command name + description + click event for detailed info
        final Component pageCore = Component.join(JoinConfiguration.newlines(), commandsOfPage.stream().
                map(cmd -> Component.text(cmd.getName()).append(Component.text(" - ")).append(cmd.getDescription()).
                        clickEvent(ClickEvent.suggestCommand("/minigame help " + cmd.getName()))).toList()).colorIfAbsent(NamedTextColor.WHITE); //todo needs formatting (not hardcoded)

        final Component header = MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_HELP_LIST_HEADER,
                Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(pageNumber)),
                Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), String.valueOf(numPages)));
        final Component footer = MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_DIVIDER_LARGE); //todo clickable next/back buttons on footer

        return header.appendNewline().append(pageCore).appendNewline().append(footer);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length > 0) {
            if (NUM_PATTERN.matcher(args[0]).matches()) {
                MinigameMessageManager.sendMessage(sender, MinigameMessageType.NONE, makePage(sender, Integer.parseInt(args[0])));
            } else {
                ACommand subCommand = CommandDispatcher.getCommand(args[0]);

                if (subCommand != null) {
                    return sendHelpInfo(sender, subCommand);
                } else {
                    ASetCommand setCommand = SetCommand.getSetCommand(args[0]);

                    if (setCommand != null) {
                        return sendHelpInfo(sender, setCommand);
                    } else {
                        return false;
                    }
                }
            }
        } else {
            MinigameMessageManager.sendMessage(sender, MinigameMessageType.NONE, makePage(sender, 1));
        }
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<ICommandInfo> allCommands = new ArrayList<>(CommandDispatcher.getCommands());
            allCommands.addAll(SetCommand.getSetCommands());
            // filter per permission
            allCommands = allCommands.stream().filter(cmd -> cmd.getPermission() == null || sender.hasPermission(cmd.getPermission())).toList();
            // get number of filtered commands before the next step
            final int numPages = (int) Math.ceil((float) allCommands.size() / COMMANDS_PER_SITE);

            // can't reuse the stream from above, since using Stream#count() would terminate it.
            // first map commands to name + aliases, then append all possible page numbers
            List<String> result = Stream.concat(allCommands.stream().flatMap(c -> {
                if (c.getAliases() != null) {
                    return Stream.concat(Stream.of(c.getName()), Arrays.stream(c.getAliases()));
                } else {
                    return Stream.of(c.getName());
                }
            }), IntStream.range(1, numPages).boxed().map(String::valueOf)).toList();

            return MinigameUtils.tabCompleteMatch(result, args[0]);
        }

        return null;
    }
}
