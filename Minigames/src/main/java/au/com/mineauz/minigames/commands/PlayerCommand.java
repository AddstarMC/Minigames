package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PlayerCommand implements ICommand {

    @Override
    public String getName() {
        return "player";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ply", "pl"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Tells you what Minigame a player is playing and other useful information or lists all players currently playing Minigames.";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"<PlayersName>", "list"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "/minigame player <PlayerName>",
                "/minigame player list"
        };
    }

    @Override
    public String getPermissionMessage() {
        return "You don't have permission to view current Minigame players!";
    }

    @Override
    public String getPermission() {
        return "minigame.player";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args[0].equalsIgnoreCase("list")) {
                List<MinigamePlayer> pls = new ArrayList<>();
                for (MinigamePlayer pl : Minigames.getPlugin().getPlayerManager().getAllMinigamePlayers()) {
                    if (pl.isInMinigame()) {
                        pls.add(pl);
                    }
                }

                sender.sendMessage(ChatColor.AQUA + "-----------List of Players Playing Minigames-----------");
                if (!pls.isEmpty()) {
                    for (MinigamePlayer pl : pls) {
                        sender.sendMessage(ChatColor.GREEN + pl.getName() + ChatColor.GRAY + " (Playing \"" + pl.getMinigame().getName(false) + "\")");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "None");
                }
            } else {
                List<Player> plmatch = Minigames.getPlugin().getServer().matchPlayer(args[0]);
                if (!plmatch.isEmpty()) {
                    MinigamePlayer pl = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(plmatch.get(0));
                    sender.sendMessage(ChatColor.AQUA + "--------Player info on " + pl.getName() + "--------");
                    if (pl.isInMinigame()) {
                        sender.sendMessage(ChatColor.GREEN + "Minigame: " + ChatColor.GRAY + pl.getMinigame().getName(false));
                        sender.sendMessage(ChatColor.GREEN + "Score: " + ChatColor.GRAY + pl.getScore());
                        sender.sendMessage(ChatColor.GREEN + "Kills: " + ChatColor.GRAY + pl.getKills());
                        sender.sendMessage(ChatColor.GREEN + "Deaths: " + ChatColor.GRAY + pl.getDeaths());
                        sender.sendMessage(ChatColor.GREEN + "Reverts: " + ChatColor.GRAY + pl.getReverts());
                        sender.sendMessage(ChatColor.GREEN + "Play Time: " + ChatColor.GRAY +
                                MinigameUtils.convertTime((int) ((Calendar.getInstance().getTimeInMillis() - pl.getStartTime() + pl.getStoredTime()) / 1000)));
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Minigame: " + ChatColor.RED + "Not in Minigame");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Could not find a player by the name \"" + args[0] + "\"");
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1) {
            List<String> plys = new ArrayList<>(plugin.getServer().getOnlinePlayers().size() + 1);
            for (Player ply : plugin.getServer().getOnlinePlayers()) {
                plys.add(ply.getName());
            }
            plys.add("list");
            return MinigameUtils.tabCompleteMatch(plys, args[0]);
        }
        return null;
    }

}
