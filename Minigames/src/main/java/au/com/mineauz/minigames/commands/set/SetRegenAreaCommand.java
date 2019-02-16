package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetRegenAreaCommand implements ICommand {

    @Override
    public String getName() {
        return "regenarea";
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
        return "Sets the regeneration area for a Minigame. This only needs to be used for Minigames that have things like leaf decay, fire, tnt etc." +
                " If the Minigame has anything that the player doesn't directly interract with that breaks, this should be used.";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"1", "2", "clear"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> regenarea <parameters>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the regen area of a Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.regenarea";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            Player ply = (Player) sender;
            if (args[0].equals("1")) {
                Location loc = ply.getLocation();
                loc.setY(loc.getY() - 1);
                loc = loc.getBlock().getLocation();
                if (minigame.getRegenArea2() == null || minigame.getRegenArea2().getWorld() == loc.getWorld()) {
                    minigame.setRegenArea1(loc);
                    ply.sendMessage(ChatColor.GRAY + "The first point of the regeneration area in " + minigame + " has been set to the block below your position.");
                } else {
                    ply.sendMessage(ChatColor.RED + "The first point of the regeneration area must be within the same world as the second point!");
                }
                return true;
            } else if (args[0].equals("2")) {
                Location loc = ply.getLocation();
                loc.setY(loc.getY() - 1);
                loc = loc.getBlock().getLocation();
                if (minigame.getRegenArea1() == null || minigame.getRegenArea1().getWorld() == loc.getWorld()) {
                    minigame.setRegenArea2(loc);
                    ply.sendMessage(ChatColor.GRAY + "The second point of the regeneration area in " + minigame + " has been set to the block below your position.");
                } else {
                    ply.sendMessage(ChatColor.RED + "The second point of the regeneration area must be within the same world as the first point!");
                }
                return true;
            } else if (args[0].equalsIgnoreCase("clear")) {
                minigame.setRegenArea1(null);
                minigame.setRegenArea2(null);

                ply.sendMessage(ChatColor.GRAY + "The regeneration area has been cleared for " + minigame);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("1;2;clear"), args[0]);
        return null;
    }

}
