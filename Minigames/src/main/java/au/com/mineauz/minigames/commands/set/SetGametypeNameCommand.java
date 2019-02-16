package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetGametypeNameCommand implements ICommand {

    @Override
    public String getName() {
        return "gametypename";
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
        return "Sets the name of the game type that displays when a player joins (Replacing \"Singleplayer\" and \"Free For All\"). " +
                "Typing \"null\" will remove the name.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> gametypename <Name>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You don't have permission to set the gametype name!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.gametypename";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (!args[0].equals("null")) {
                String gtn = "";
                int count = 0;
                for (String arg : args) {
                    gtn += arg;
                    count++;
                    if (count != args.length)
                        gtn += " ";
                }
                minigame.setGametypeName(gtn);
                sender.sendMessage(ChatColor.GRAY + "Gametype name for " + minigame + " has been set to " + gtn + ".");
            } else {
                minigame.setGametypeName(null);
                sender.sendMessage(ChatColor.GRAY + "Gametype name for " + minigame + " has been removed.");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
