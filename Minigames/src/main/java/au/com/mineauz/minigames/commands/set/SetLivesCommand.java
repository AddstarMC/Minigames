package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetLivesCommand implements ICommand {

    @Override
    public String getName() {
        return "lives";
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Sets the amount of lives a player has in the Minigame. If they run out, they are booted from the game. 0 means unlimited.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> lives <number>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the number of lives in a Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.lives";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+")) {
                int lives = Integer.parseInt(args[0]);
                minigame.setLives(lives);

                sender.sendMessage(ChatColor.GRAY + minigame.getName(false) + "'s lives has been set to " + lives);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
