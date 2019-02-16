package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.MultiplayerTimer;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StartCommand implements ICommand {

    @Override
    public String getName() {
        return "start";
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
        return "Starts a Global Minigame. If the game isn't Global, it will force start a game begin countdown without waiting for players.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame start <Minigame>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to start a Minigame";
    }

    @Override
    public String getPermission() {
        return "minigame.start";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            Minigame mgm = plugin.getMinigameManager().getMinigame(args[0]);

            if (mgm != null) {
                if (!mgm.isEnabled() && mgm.getType() == MinigameType.GLOBAL) {
                    MinigamePlayer caller = null;
                    if (sender instanceof Player)
                        caller = plugin.getPlayerManager().getMinigamePlayer((Player) sender);
                    plugin.getMinigameManager().startGlobalMinigame(mgm, caller);
                } else if (mgm.getType() != MinigameType.GLOBAL && mgm.getType() != MinigameType.SINGLEPLAYER && mgm.hasPlayers()) {
                    if (mgm.getMpTimer() == null || mgm.getMpTimer().getPlayerWaitTimeLeft() != 0) {
                        if (mgm.getMpTimer() == null) {
                            mgm.setMpTimer(new MultiplayerTimer(mgm));
                        }
                        mgm.getMpTimer().setCurrentLobbyWaitTime(0);
                        mgm.getMpTimer().startTimer();
                    } else
                        sender.sendMessage(ChatColor.RED + mgm.getName(false) + " has already started.");
                } else if (mgm.isEnabled() && mgm.getType() == MinigameType.GLOBAL) {
                    sender.sendMessage(ChatColor.RED + mgm.getName(false) + " is already running!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "No Global or Multiplayer Minigame found by the name " + args[0]);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
        return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
    }

}
