package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetFlagCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "flag";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_FLAG_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_FLAG_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.flag";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            if (args[0].equalsIgnoreCase("add") && args.length >= 2) {
                minigame.addFlag(args[1]);
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_FLAG_ADD,
                        Placeholder.parsed(MinigamePlaceHolderKey.FLAG.getKey(), args[1]),
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                return true;
            } else if (args[0].equalsIgnoreCase("remove") && args.length >= 2) {
                if (minigame.removeFlag(args[1])) {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_FLAG_REMOVE,
                            Placeholder.parsed(MinigamePlaceHolderKey.FLAG.getKey(), args[1]));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_FLAG_ERROR_NOFLAG,
                            Placeholder.parsed(MinigamePlaceHolderKey.FLAG.getKey(), args[1]));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                if (minigame.hasFlags()) {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MgCommandLangKey.COMMAND_SET_FLAG_LIST_HEADER,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                    MinigameMessageManager.sendMessage(sender, MinigameMessageType.NONE,
                            MiniMessage.miniMessage().deserialize(String.join("<gray>, </gray>", minigame.getFlags())));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_SET_FLAG_NOFLAGS,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("clear")) {
                if (minigame.hasFlags()) {
                    minigame.getFlags().clear();
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_FLAG_CLEAR,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_SET_FLAG_NOFLAGS,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull @Nullable [] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(List.of("add", "remove", "clear", "list"), args[0]);
        return null;
    }

}
