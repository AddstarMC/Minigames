package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SetUnlimitedAmmoCommand implements ICommand {

    @Override
    public String getName() {
        return "unlimitedammo";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"infammo"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Allows unlimited snowballs or eggs to be thrown.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> unlimitedammo <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to enable unlimited ammo!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.unlimitedammo";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);
            minigame.setUnlimitedAmmo(bool);
            if (bool) {
                sender.sendMessage(ChatColor.GRAY + "Unlimited ammo has been turned on for " + minigame);
            } else {
                sender.sendMessage(ChatColor.GRAY + "Unlimited ammo has been turned off for " + minigame);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(List.of("true", "false"), args[0]);
        return null;
    }

}
