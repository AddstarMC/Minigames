package au.com.mineauz.minigamesregions.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.RegionModule;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static au.com.mineauz.minigamesregions.RegionMessageManager.RegionLangKey;

public class SetNodeCommand implements ICommand {

    @Override
    public String getName() {
        return "node";
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
        return "Creates and modifies customizable nodes";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> node create <name>",
                "/minigame set <Minigame> node modify",
                "/minigame set <minigame> node delete <name>"
        };
    }

    @Override
    public String getPermission() {
        return "minigame.set.node";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        if (args != null) {
            MinigamePlayer ply = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
            RegionModule rmod = RegionModule.getMinigameModule(minigame);
            if (args[0].equalsIgnoreCase("create") && args.length >= 2) {
                if (!rmod.hasNode(args[1])) {
                    rmod.addNode(args[1], new Node(args[1], ply.getLocation()));
                    sender.sendMessage(ChatColor.GRAY + RegionMessageManager.getMessage(RegionLangKey.COMMAND_NODE_ADDED, args[1], minigame.getName(true)));
                } else
                    sender.sendMessage(ChatColor.RED + RegionMessageManager.getMessage(RegionLangKey.COMMAND_NODE_EXISTS, args[1], minigame.getName(true)));
                return true;
            } else if (args[0].equalsIgnoreCase("modify")) {
                rmod.displayMenu(ply, null);
                return true;
            } else if (args[0].equalsIgnoreCase("delete") && args.length >= 2) {
                if (rmod.hasNode(args[1])) {
                    rmod.removeNode(args[1]);
                    sender.sendMessage(ChatColor.GRAY + "Removed a node called " + args[1] + " from " + minigame);
                } else
                    sender.sendMessage(ChatColor.RED + "A node by the name " + args[1] + " doesn't exists in " + minigame);
                return true;
            }
        }
        return false;
    }

    // create, modify, delete
    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1) {
            List<String> tab = new ArrayList<>();
            tab.add("create");
            tab.add("modify");
            tab.add("delete");
            return MinigameUtils.tabCompleteMatch(tab, args[0]);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("delete")) {
                List<String> tab = new ArrayList<>();
                RegionModule rmod = RegionModule.getMinigameModule(minigame);
                for (Node node : rmod.getNodes()) {
                    tab.add(node.getName());
                }
                return MinigameUtils.tabCompleteMatch(tab, args[1]);
            }
        }
        return null;
    }
}
