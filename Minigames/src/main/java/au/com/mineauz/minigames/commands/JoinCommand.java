package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class JoinCommand implements ICommand {

    @Override
    public String getName() {
        return "join";
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
        return MinigameUtils.getLang("command.join.description");
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame join <Minigame>"};
    }

    @Override
    public String getPermissionMessage() {
        return MinigameUtils.getLang("command.join.noPermission");
    }

    @Override
    public String getPermission() {
        return "minigame.join";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame, String label, String[] args) {
        Player player = (Player) sender;
        if (args != null) {
            Minigame mgm = plugin.getMinigameManager().getMinigame(args[0]);
            if (mgm != null && (!mgm.getUsePermissions() || player.hasPermission("minigame.join." + mgm.getName(false).toLowerCase()))) {
                if (!plugin.getPlayerManager().getMinigamePlayer(player).isInMinigame()) {
                    sender.sendMessage(ChatColor.GREEN + MinigameUtils.formStr("command.join.joining", mgm.getName(false)));
                    plugin.getPlayerManager().joinMinigame(plugin.getPlayerManager().getMinigamePlayer(player), mgm, false, 0.0);
                } else {
                    player.sendMessage(ChatColor.RED + MinigameUtils.getLang("command.join.alreadyPlaying"));
                }
            } else if (mgm != null && mgm.getUsePermissions()) {
                player.sendMessage(ChatColor.RED + MinigameUtils.formStr("command.join.noMinigamePermission", "minigame.join." + mgm.getName(false).toLowerCase()));
            } else {
                player.sendMessage(ChatColor.RED + MinigameUtils.getLang("minigame.error.noMinigame"));
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1) {
            List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
            return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
        }
        return null;
    }

}
