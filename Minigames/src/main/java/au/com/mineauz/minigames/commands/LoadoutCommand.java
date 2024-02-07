package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
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
        return MinigameUtils.getLang("command.loadout.description");
    }

    public String[] getUsage() {
        return new String[]{"/minigame loadout", "/minigame loadout <LoadoutName>"};
    }

    public @Nullable String getPermission() {
        return "minigame.loadout.menu";
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
        if (mgPlayer.isInMinigame()) {
            if (args.length > 0) {
                String ln = args[0];
                if (LoadoutModule.getMinigameModule(mgPlayer.getMinigame()).hasLoadout(ln)) {
                    mgPlayer.setLoadout(LoadoutModule.getMinigameModule(mgPlayer.getMinigame()).getLoadout(ln));
                    mgPlayer.sendInfoMessage(
                            MinigameMessageManager.getMinigamesMessage("player.loadout.nextSpawnName", ln));
                } else {
                    mgPlayer.sendMessage(MinigameMessageManager.getMinigamesMessage("player.loadout.error.noLoadout", ln), MinigameMessageType.ERROR);
                }
            } else {
                LoadoutModule.getMinigameModule(mgPlayer.getMinigame()).displaySelectionMenu(mgPlayer, false);
            }
        } else {
            mgPlayer.sendMessage(MinigameUtils.getLang("command.loadout.noMinigame"), MinigameMessageType.ERROR);
        }
        return true;
    }

    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @Nullable [] args) {
        if (args != null) {
            MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
            if (mgPlayer.isInMinigame()) {
                if (args.length == 1) {
                    return MinigameUtils.tabCompleteMatch(
                            new ArrayList<>(LoadoutModule.getMinigameModule(mgPlayer.getMinigame()).getLoadoutMap().keySet()),
                            args[0]);
                }
            }
        }
        return null;
    }

}
