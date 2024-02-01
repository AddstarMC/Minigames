package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DeniedCommandCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "deniedcommand";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"deniedcomd", "deniedcom"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_DENIEDCMDS_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_DENIEDCMDS_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.deniedcommands";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("add") && args.length >= 2) {
                PLUGIN.getPlayerManager().addDeniedCommand(args[1]);
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_DENIEDCMDS_ADD_SUCCESS,
                        Placeholder.unparsed(MinigamePlaceHolderKey.COMMAND.getKey(), args[1]));
                return true;
            } else if (args[0].equalsIgnoreCase("remove") && args.length >= 2) {
                PLUGIN.getPlayerManager().removeDeniedCommand(args[1]);
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_DENIEDCMDS_REMOVE_SUCCESS,
                        Placeholder.unparsed(MinigamePlaceHolderKey.COMMAND.getKey(), args[1]));
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                String coms = String.join(", ", PLUGIN.getPlayerManager().getDeniedCommands());
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_DENIEDCMDS_LIST_SUCCESS,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), coms));
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return MinigameUtils.tabCompleteMatch(List.of("add", "remove", "list"), args[0]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            return MinigameUtils.tabCompleteMatch(PLUGIN.getPlayerManager().getDeniedCommands(), args[1]);
        }
        return null;
    }

}
