package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LoadoutCommand implements ICommand {


    public String getName() {
        return "loadout";
    }

    public String[] getAliases() {
        return null;
    }

    public boolean canBeConsole() {
        return false;
    }

    public String getDescription() {
        return MinigameUtils.getLang("command.loadout.description");
    }

    public String[] getParameters() {
        return null;
    }

    public String[] getUsage() {
        return new String[]{"/minigame loadout", "/minigame loadout <LoadoutName>"};
    }

    public String getPermissionMessage() {
        return MinigameUtils.getLang("command.loadout.noPermission");
    }

    public String getPermission() {
        return "minigame.loadout.menu";
    }

    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        MinigamePlayer ply = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
        if (ply.isInMinigame()) {
            if (args == null) {
                LoadoutModule.getMinigameModule(ply.getMinigame()).displaySelectionMenu(ply, false);
            } else {
                String ln = args[0];
                if (LoadoutModule.getMinigameModule(ply.getMinigame()).hasLoadout(ln)) {
                    ply.setLoadout(LoadoutModule.getMinigameModule(ply.getMinigame()).getLoadout(ln));
                    ply.sendInfoMessage(MinigameUtils.formStr("player.loadout.nextSpawnName", ln));
                } else {
                    ply.sendMessage(MinigameUtils.formStr("player.loadout.noLoadout", ln), MinigameMessageType.ERROR);
                }
            }
        } else {
            ply.sendMessage(MinigameUtils.getLang("command.loadout.noMinigame"), MinigameMessageType.ERROR);
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args != null) {
            MinigamePlayer ply = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
            if (ply.isInMinigame()) {
                if (args.length == 1) {
                    return MinigameUtils.tabCompleteMatch(new ArrayList<>(
                                    LoadoutModule.getMinigameModule(ply.getMinigame()).getLoadoutMap().keySet()),
                            args[0]);
                }
            }
        }
        return null;
    }

}
