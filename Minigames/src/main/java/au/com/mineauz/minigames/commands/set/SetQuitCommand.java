package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetQuitCommand implements ICommand {

    @Override
    public String getName() {
        return "quit";
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Sets the quitting position of a Minigame to where you are standing.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> quit"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set a Minigames quit position!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.quit";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        Player player = (Player) sender;
        minigame.setQuitPosition(player.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Quit position has been set for " + minigame);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
