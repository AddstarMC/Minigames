package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.minigame.modules.MgModules;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LoadoutCommand extends ACommand {

    public @NotNull String getName() {
        return "loadout";
    }

    public boolean canBeConsole() {
        return false;
    }

    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_LOADOUT_DESCRIPTION);
    }

    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_LOADOUT_USAGE);
    }

    public @Nullable String getPermission() {
        return "minigame.loadout.menu";
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {

        if (sender instanceof Player player) {
            MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);
            if (mgPlayer.isInMinigame()) {
                LoadoutModule module = LoadoutModule.getMinigameModule(mgPlayer.getMinigame());

                if (module != null) {
                    if (args.length > 0) {
                        String loadoutName = args[0];
                        if (module.hasLoadout(loadoutName)) {
                            mgPlayer.setLoadout(module.getLoadout(loadoutName));


                            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.PLAYER_LOADOUT_NEXTRESPAWN,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.LOADOUT.getKey(), loadoutName));
                        } else {
                            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.PLAYER_LOADOUT_ERROR_NOLOADOUT,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.LOADOUT.getKey(), loadoutName));
                        }
                    } else {
                        module.displaySelectionMenu(mgPlayer, false);
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTGAMEMECHANIC,
                            Placeholder.component(MinigamePlaceHolderKey.MINIGAME.getKey(), mgPlayer.getMinigame().getDisplayName()),
                            Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), MgModules.LOADOUT.getName()));
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTINMINIGAME_SELF);
            }
        } else {
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_SENDERNOTAPLAYER);
        }
        return true;
    }

    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @Nullable [] args) {
        if (args != null) {
            MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
            if (mgPlayer.isInMinigame()) {
                LoadoutModule module = LoadoutModule.getMinigameModule(mgPlayer.getMinigame());
                if (module != null && args.length == 1) {
                    return MinigameUtils.tabCompleteMatch(new ArrayList<>(module.getLoadoutMap().keySet()), args[0]);
                }
            }
        }
        return null;
    }
}
