package au.com.mineauz.minigames.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;

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
        return MinigameUtils.getLang("command.help.description");
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
        return MinigameUtils.getLang("command.help.noPermission");
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
        sender.sendMessage(ChatColor.GREEN + "List of Minigame commands");
        sender.sendMessage(ChatColor.BLUE + "/minigame");
        sender.sendMessage(ChatColor.GRAY + "The default command (alias /mgm)");
        if (player == null || player.hasPermission("minigame.join")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame join <minigame>");
            sender.sendMessage(MinigameUtils.getLang("command.help.info.join"));
        }
        if (player == null || player.hasPermission("minigame.quit")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame quit");
            sender.sendMessage(MinigameUtils.getLang("command.help.info.quit"));
            if (player == null || player.hasPermission("minigame.quit.other")) {
                sender.sendMessage(MinigameUtils.getLang("command.help.info.quitOther"));
            }
        }
        if (player == null || player.hasPermission("minigame.end")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame end [Player]");
            sender.sendMessage(MinigameUtils.getLang("command.help.info.end"));
        }
        if (player == null || player.hasPermission("minigame.revert")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame revert");
            sender.sendMessage(MinigameUtils.getLang("command.help.info.revert"));
        }
        if (player == null || player.hasPermission("minigame.delete")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame delete <Minigame>");
            sender.sendMessage(MinigameUtils.getLang("command.help.info.delete"));
        }
        if (player == null || player.hasPermission("minigame.hint")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame hint <minigame>");
            sender.sendMessage(MinigameUtils.getLang("command.help.info.hint"));
        }
        if (player == null || player.hasPermission("minigame.toggletimer")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame toggletimer <Minigame>");
            sender.sendMessage(MinigameUtils.getLang("command.help.info.timer"));
        }
        if (player == null || player.hasPermission("minigame.list")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame list");
            sender.sendMessage(MinigameUtils.getLang("command.help.info.list"));
        }
        if (player == null || player.hasPermission("minigame.reload")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame reload");
            sender.sendMessage(MinigameUtils.getLang("command.help.info.reload"));
        }

        sender.sendMessage(ChatColor.BLUE + "/minigame set <Minigame> <parameter>...");
        sender.sendMessage(MinigameUtils.getLang("command.help.info.set"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
