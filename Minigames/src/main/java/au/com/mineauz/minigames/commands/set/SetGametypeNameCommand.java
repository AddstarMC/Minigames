package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetGametypeNameCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "gametypename";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_GAMETYPENAME_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_GAMETYPENAME_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.gametypename";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            if (args[0].equalsIgnoreCase("null")) {
                minigame.setGameTypeName(null);
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_GAMETYPENAME_REMOVE,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
            } else {
                String gameTypeName = String.join(" ", args);

                minigame.setGameTypeName(gameTypeName);
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_GAMETYPENAME_SUCCESS,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), gameTypeName));
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        return null;
    }

}
