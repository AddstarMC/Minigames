package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.BooleanUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetItemDropCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "itemdrop";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_ITEMSDROP_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_ITEMSDROP_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.itemdrop";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            if (args[0].equalsIgnoreCase("player") && args.length >= 2) {
                Boolean bool = BooleanUtils.toBooleanObject(args[1]);

                if (bool != null) {
                    minigame.setItemDrops(bool);

                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_ITEMSDROP_DROP,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                            Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(), MinigameMessageManager.getMgMessage(
                                    bool ? MgCommandLangKey.COMMAND_STATE_ENABLED : MgCommandLangKey.COMMAND_STATE_DISABLED)));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTBOOL,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("death") && args.length >= 2) {
                Boolean bool = BooleanUtils.toBooleanObject(args[1]);

                if (bool != null) {
                    minigame.setDeathDrops(bool);

                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_ITEMSDROP_DEATH,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                            Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(), MinigameMessageManager.getMgMessage(
                                    bool ? MgCommandLangKey.COMMAND_STATE_ENABLED : MgCommandLangKey.COMMAND_STATE_DISABLED)));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTBOOL,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return MinigameUtils.tabCompleteMatch(List.of("death", "player"), args[0]);
        } else if (args.length == 2) {
            return MinigameUtils.tabCompleteMatch(List.of("true", "false"), args[0]);
        }
        return null;
    }

}
