package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetDisplayNameCommand implements ICommand {

    @Override
    public String getName() {
        return "displayname";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"dispname", "dname"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Sets the visible name for the Mingiame, unlike the actual name, this can include spaces and special characters. " +
                "(setting it to \"null\" will remove the display name.)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> displayname <Some Displayname Here>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the display name of a Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.displayname";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            String name = "";
            for (String arg : args) {
                name += arg;
                if (!arg.equals(args[args.length - 1])) {
                    name += " ";
                }
            }
            if (name.equalsIgnoreCase("null")) {
                minigame.setDisplayName(null);
                sender.sendMessage(ChatColor.GRAY + "Removed " + minigame + "'s display name");
            } else {
                minigame.setDisplayName(name);
                sender.sendMessage(ChatColor.GRAY + "Set " + minigame + "'s display name to \"" + name + "\"");
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
