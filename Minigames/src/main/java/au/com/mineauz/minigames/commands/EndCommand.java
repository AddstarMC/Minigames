package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EndCommand implements ICommand {

    @Override
    public String getName() {
        return "end";
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
        return "Ends the game a player is currently playing. If the game is a team game, the whole team will win. " +
                "This can also be used to end a specific team from a game, an example is shown in the usage section.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame end [Player]",
                "/minigame end <TeamName> <Minigame>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to force end your Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.end";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args == null && sender instanceof Player) {
            MinigamePlayer ply = plugin.getPlayerManager().getMinigamePlayer((Player) sender);
            if (ply.isInMinigame()) {
                forceEnd(sender, ply);
            } else {
                sender.sendMessage(ChatColor.RED + "Error: You are not in a minigame!");
            }
            return true;
        } else if (args != null) {
            Player player = null;
            if (sender instanceof Player) {
                player = (Player) sender;
            }
            if (player == null || player.hasPermission("minigame.end.other")) {
                Player playertoEnd = Bukkit.getPlayerExact(args[0]);
                MinigamePlayer ply = null;
                Team team = null;
                Minigame mgm = null;
                if (args.length == 2 && plugin.getMinigameManager().hasMinigame(args[1])) {
                    TeamColor color = TeamColor.matchColor(args[0]);
                    mgm = plugin.getMinigameManager().getMinigame(args[1]);
                    if (mgm == null) {
                        sender.sendMessage(ChatColor.RED + "No minigme found by the name " + args[1]);
                        return true;
                    }

                    if (TeamsModule.getMinigameModule(mgm).hasTeam(color)) {
                        team = TeamsModule.getMinigameModule(mgm).getTeam(color);
                    } else {
                        sender.sendMessage(ChatColor.RED + "No team found by the name " + args[0] + " in " + mgm.getName(false));
                        return true;
                    }
                } else if (args.length == 2 && !plugin.getMinigameManager().hasMinigame(args[1])) {
                    sender.sendMessage(ChatColor.RED + "No Minigame found by the name " + args[1]);
                    return true;
                } else if (playertoEnd != null) {
                    ply = plugin.getPlayerManager().getMinigamePlayer(playertoEnd);
                } else {
                    sender.sendMessage(ChatColor.RED + "No player found by the name " + args[0]);
                    return true;
                }

                if (ply != null && ply.isInMinigame()) {
                    forceEnd(sender, ply);
                } else if (args.length >= 2 && team != null && mgm != null) {
                    if (mgm.hasPlayers()) {
                        List<MinigamePlayer> w = new ArrayList<>(team.getPlayers());
                        int lcount = 0;
                        for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                            if (t != team) {
                                lcount += t.getPlayers().size();
                            }
                        }
                        List<MinigamePlayer> l = new ArrayList<>(lcount);
                        for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                            if (t != team) {
                                l.addAll(t.getPlayers());
                            }
                        }
                        plugin.getPlayerManager().endMinigame(mgm, w, l);
                        sender.sendMessage(ChatColor.GRAY + "You forced " + team.getChatColor() + team.getDisplayName() + ChatColor.GRAY + " to win the Minigame.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "This Minigame has no players!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "This player is not playing a Minigame.");
                }
            } else if (player != null) {
                sender.sendMessage(ChatColor.RED + "Error: You don't have permission to force end another players Minigame!");
                sender.sendMessage(ChatColor.RED + "minigame.end.other");
            }
            return true;
        }
        return false;
    }

    private void forceEnd(CommandSender sender, MinigamePlayer ply) {
        if (ply.getMinigame().getType() != MinigameType.SINGLEPLAYER) {
            List<MinigamePlayer> w = new ArrayList<>(1);
            List<MinigamePlayer> l = new ArrayList<>(ply.getMinigame().getPlayers().size());
            w.add(ply);
            l.addAll(ply.getMinigame().getPlayers());
            l.remove(ply);

            plugin.getPlayerManager().endMinigame(ply.getMinigame(), w, l);
            sender.sendMessage(ChatColor.GRAY + "You forced " + ply.getName() + " to win the Minigame.");
        } else {
            plugin.getPlayerManager().endMinigame(ply);
            sender.sendMessage(ChatColor.GRAY + "You forced " + ply.getName() + " to win the Minigame.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1) {
            List<String> plt = new ArrayList<>(plugin.getServer().getOnlinePlayers().size() + 2);
            for (Player pl : plugin.getServer().getOnlinePlayers()) {
                plt.add(pl.getName());
            }
            for (TeamColor col : TeamColor.values()) {
                plt.add(col.toString().toLowerCase());
            }
            return MinigameUtils.tabCompleteMatch(plt, args[0]);
        } else if (args.length == 2 && TeamColor.matchColor(args[0]) != null) {
            List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
            return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
        }
        return null;
    }

}
