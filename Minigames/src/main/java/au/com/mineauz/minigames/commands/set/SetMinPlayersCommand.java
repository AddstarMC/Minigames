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

public class SetMinPlayersCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "minplayers";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_MINPLAYERS_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_MINPLAYERS_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.minplayers";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+")) {
                int min = Integer.parseInt(args[0]);
                minigame.setMinPlayers(min);

                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_MINPLAYERS_SUCCESS,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                        Placeholder.unparsed(MinigamePlaceHolderKey.MIN.getKey(), String.valueOf(min)));
                return true;
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTNUMBER,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        return null;
    }
}
