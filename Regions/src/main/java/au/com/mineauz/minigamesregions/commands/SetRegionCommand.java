package au.com.mineauz.minigamesregions.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionModule;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetRegionCommand implements ICommand {

    @Override
    public String getName() {
        return "region";
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
        return "Creates, edits and removes Minigame regions";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"select", "create", "remove", "modify"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "/minigame set <Minigame> region select <1/2>",
                "/minigame set <Minigame> region create <name>",
                "/minigame set <Minigame> region remove <name>",
                "/minigame set <Minigame> region modify"
        };
    }

    @Override
    public String getPermissionMessage() {
        return "You don't have permission to modify Minigame regions";
    }

    @Override
    public String getPermission() {
        return "minigame.set.region";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            MinigamePlayer ply = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
            RegionModule rmod = RegionModule.getMinigameModule(minigame);
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (ply.hasSelection()) {
                        String name = args[1];
                        rmod.addRegion(name, new Region(name, minigame, ply.getSelectionPoints()[0], ply.getSelectionPoints()[1]));
                        ply.clearSelection();

                        ply.sendInfoMessage(ChatColor.GRAY + "Created new region for " + minigame.getName(false) + " named " + name);
                    } else {
                        ply.sendInfoMessage(ChatColor.RED + "You have not made a selection!");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (rmod.hasRegion(args[1])) {
                        rmod.removeRegion(args[1]);
                        ply.sendInfoMessage(ChatColor.GRAY + "Removed the region named " + args[1] + " from " + minigame.getName(false));
                    } else {
                        ply.sendInfoMessage(ChatColor.GRAY + "No region by the name " + args[1] + " was found in " + minigame.getName(false));
                    }
                    return true;
                }

            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("modify")) {
                    rmod.displayMenu(ply, null);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {

        if (args.length == 1) {
            List<String> tab = new ArrayList<>();
            tab.add("create");
            tab.add("modify");
            tab.add("remove");
            return MinigameUtils.tabCompleteMatch(tab, args[0]);
        } else if (args.length == 2) {
            List<String> tab = new ArrayList<>();
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("remove")) {
                RegionModule rmod = RegionModule.getMinigameModule(minigame);
                for (Region reg : rmod.getRegions()) {
                    tab.add(reg.getName());
                }
            }
            return MinigameUtils.tabCompleteMatch(tab, args[1]);
        }
        return null;
    }

}
