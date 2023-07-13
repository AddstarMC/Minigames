package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.managers.MessageManager;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpCommand implements ICommand {

    @Override
    public String getName() {
        return "help";
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
        return MessageManager.getMessage(null, "command.help.description");
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame help"};
    }

    @Override
    public String getPermissionMessage() {
        return MessageManager.getMessage(null, "command.help.noPermission");
    }

    @Override
    public String getPermission() {
        return "minigame.help";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        sender.sendMessage(ChatColor.GREEN + MessageManager.getUnformattedMessage(null, "command.info.header"));
        sender.sendMessage(ChatColor.BLUE + "/minigame");
        sender.sendMessage(ChatColor.GRAY + MessageManager.getUnformattedMessage(null, "command.info.mgm"));
        if (player == null || player.hasPermission("minigame.join")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame join <Minigame>");
            sender.sendMessage(MessageManager.getMessage(null, "command.info.join"));
        }
        if (player == null || player.hasPermission("minigame.quit")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame quit");
            sender.sendMessage(MessageManager.getMessage(null, "command.info.quit"));
            if (player == null || player.hasPermission("minigame.quit.other")) {
                sender.sendMessage(MessageManager.getMessage(null, "command.info.quitOther"));
            }
        }
        if (player == null || player.hasPermission("minigame.end")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame end [Player]");
            sender.sendMessage(MessageManager.getMessage(null, "command.info.end"));
        }
        if (player == null || player.hasPermission("minigame.revert")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame revert");
            sender.sendMessage(MessageManager.getMessage(null, "command.info.revert"));
        }
        if (player == null || player.hasPermission("minigame.delete")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame delete <Minigame>");
            sender.sendMessage(MessageManager.getMessage(null, "command.info.delete"));
        }
        if (player == null || player.hasPermission("minigame.hint")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame hint <minigame>");
            sender.sendMessage(MessageManager.getMessage(null, "command.info.hint"));
        }
        if (player == null || player.hasPermission("minigame.toggletimer")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame toggletimer <Minigame>");
            sender.sendMessage(MessageManager.getMessage(null, "command.info.timer"));
        }
        if (player == null || player.hasPermission("minigame.list")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame list");
            sender.sendMessage(MessageManager.getMessage(null, "command.info.list"));
        }
        if (player == null || player.hasPermission("minigame.reload")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame reload");
            sender.sendMessage(MessageManager.getMessage(null, "command.info.reload"));
        }

        sender.sendMessage(ChatColor.BLUE + "/minigame set <Minigame> <parameter>...");
        sender.sendMessage(MessageManager.getMessage(null, "command.info.set"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
