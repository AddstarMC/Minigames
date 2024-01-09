package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.BooleanUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetDisplayScoreboardCommand implements ICommand { //todo allow sidebar, below name all of the vanilla stuff

    @Override
    public @NotNull String getName() {
        return "displayscoreboard";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{
                "showscoreboard",
                "dispscore",
                "displayscore",
                "showscore"
        };
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MinigameLangKey.COMMAND_SET_DISPLAYSCOREBOARD_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MinigameLangKey.COMMAND_SET_DISPLAYSCOREBOARD_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.displayscoreboard";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            Boolean bool = BooleanUtils.toBooleanObject(args[0]);

            if (bool != null) {
                minigame.setDisplayScoreboard(bool);

                if (bool) {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO,
                            MinigameLangKey.COMMAND_SET_DISPLAYSCOREBOARD_SUCCESS,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO,
                            MinigameLangKey.COMMAND_SET_DISPLAYSCOREBOARD_REMOVED,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                }
                return true;
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOTBOOL,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @Nullable [] args) {
        if (args != null) {
            if (args.length == 1) {
                return MinigameUtils.tabCompleteMatch(List.of("true", "false"), args[0]);
            }
        }
        return null;
    }

}
