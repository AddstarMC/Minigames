package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LoadoutCommand implements ICommand {

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

    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String @Nullable [] args) {
        MinigamePlayer ply = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
        if (ply.isInMinigame()) {
            if (args == null) {
                LoadoutModule.getMinigameModule(ply.getMinigame()).displaySelectionMenu(ply, false);
            } else {
                String ln = args[0];
                if (LoadoutModule.getMinigameModule(ply.getMinigame()).hasLoadout(ln)) {
                    ply.setLoadout(LoadoutModule.getMinigameModule(ply.getMinigame()).getLoadout(ln));
                    ply.sendInfoMessage(
                            MinigameMessageManager.getMinigamesMessage("player.loadout.nextSpawnName", ln));
                } else {
                    ply.sendMessage(MinigameMessageManager.getMinigamesMessage("player.loadout.error.noLoadout", ln), MinigameMessageType.ERROR);
                }
            }
        } else {
            ply.sendMessage(MinigameUtils.getLang("command.loadout.noMinigame"), MinigameMessageType.ERROR);
        }
        return true;
    }

    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         @NotNull String @Nullable [] args) {
        if (args != null) {
            MinigamePlayer ply = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
            if (ply.isInMinigame()) {
                if (args.length == 1) {
                    return MinigameUtils.tabCompleteMatch(
                            new ArrayList<>(LoadoutModule.getMinigameModule(ply.getMinigame()).getLoadoutMap().keySet()),
                            args[0]);
                }
            }
        }
        return null;
    }

}
