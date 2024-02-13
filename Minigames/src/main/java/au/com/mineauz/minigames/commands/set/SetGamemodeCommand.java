package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class SetGamemodeCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "gamemode";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"gm"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_GAMEMODE_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_GAMEMODE_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.gamemode";
    }

    private @Nullable GameMode matchGameMode(@NotNull String toMatch) {
        if (toMatch.matches("[0-9]+")) {
            //the moment, the support gets dropped, we will also drop the support for it.
            return GameMode.getByValue(Integer.parseInt(toMatch));
        } else {
            for (GameMode gameMode : GameMode.values()) {
                if (toMatch.equalsIgnoreCase(gameMode.toString())) {
                    return gameMode;
                }
            }
        }

        return null;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            GameMode gameMode = matchGameMode(args[0]);

            if (gameMode != null) {
                minigame.setDefaultGamemode(gameMode);

                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_GAMEMODE_SUCCESS,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                        Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), Component.translatable(gameMode.translationKey())));
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return MinigameUtils.tabCompleteMatch(Arrays.stream(GameMode.values()).map(gm -> gm.name().toLowerCase()).toList(), args[0]);
        }
        return null;
    }
}
