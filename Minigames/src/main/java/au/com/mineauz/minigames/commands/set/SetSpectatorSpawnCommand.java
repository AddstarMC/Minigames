package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetSpectatorSpawnCommand implements ICommand {

    @Override
    public String getName() {
        return "spectatorstart";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"specstart"};
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Sets the start position for spectators";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> spectatorstart"};
    }

    @Override
    public String getPermissionMessage() {
        return "You don't have permission to set the spectator start point!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.spectatorstart";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        Player ply = (Player) sender;
        minigame.setSpectatorLocation(ply.getLocation());
        ply.sendMessage(ChatColor.GRAY + "Set the spectator start point to where you are standing");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
