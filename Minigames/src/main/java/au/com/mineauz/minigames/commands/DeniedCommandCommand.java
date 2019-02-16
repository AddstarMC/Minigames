package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeniedCommandCommand implements ICommand {

    @Override
    public String getName() {
        return "deniedcommand";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"deniedcomd", "deniedcom"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Sets commands to be disabled when playing a Minigame. (eg: home or spawn)";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"add", "remove", "list"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame deniedcommand add <Command>", "/minigame deniedcommand remove <Command>", "/minigame deniedcommand list"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set denied commands!";
    }

    @Override
    public String getPermission() {
        return "minigame.deniedcommands";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args[0].equalsIgnoreCase("add") && args.length >= 2) {
                plugin.getPlayerManager().addDeniedCommand(args[1]);
                sender.sendMessage(ChatColor.GRAY + "Added \"" + args[1] + "\" to the denied command list.");
                return true;
            } else if (args[0].equalsIgnoreCase("remove") && args.length >= 2) {
                plugin.getPlayerManager().removeDeniedCommand(args[1]);
                sender.sendMessage(ChatColor.GRAY + "Removed \"" + args[1] + "\" from the denied command list.");
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                String coms = "";
                boolean switchColour = false;
                for (String par : plugin.getPlayerManager().getDeniedCommands()) {
                    if (switchColour) {
                        coms += ChatColor.WHITE + par;
                        if (!par.equalsIgnoreCase(plugin.getPlayerManager().getDeniedCommands().get(plugin.getPlayerManager().getDeniedCommands().size() - 1))) {
                            coms += ChatColor.WHITE + ", ";
                        }
                        switchColour = false;
                    } else {
                        coms += ChatColor.GRAY + par;
                        if (!par.equalsIgnoreCase(plugin.getPlayerManager().getDeniedCommands().get(plugin.getPlayerManager().getDeniedCommands().size() - 1))) {
                            coms += ChatColor.WHITE + ", ";
                        }
                        switchColour = true;
                    }
                }
                sender.sendMessage(ChatColor.GRAY + "Disabled Commands: " + coms);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1) {
            List<String> ls = new ArrayList<>();
            Collections.addAll(ls, getParameters());
            return MinigameUtils.tabCompleteMatch(ls, args[0]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            return MinigameUtils.tabCompleteMatch(plugin.getPlayerManager().getDeniedCommands(), args[1]);
        }
        return null;
    }

}
