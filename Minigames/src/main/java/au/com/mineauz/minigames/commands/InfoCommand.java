package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/12/2017.
 */
public class InfoCommand implements ICommand {
    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public String getDescription() {
        return MinigameUtils.getLang("command.info.description");
    }

    @Override
    public String[] getParameters() {
        return new String[0];
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame info [<minigame>]"};
    }

    @Override
    public String getPermissionMessage() {
        return MinigameUtils.getLang("command.info.noPermission");
    }

    @Override
    public String getPermission() {
        return "minigame.info";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame, String label, String[] args) {
        if (args != null) {
            minigame = plugin.getMinigameManager().getMinigame(args[0]);
        }
        if (minigame != null) {
            List<String> output = new ArrayList<>();
            output.add(ChatColor.GREEN + MinigameUtils.formStr(MinigameUtils.getLang("command.info.output.header"), minigame.getName(true)));
            output.add(ChatColor.GOLD + "<-------------------------------------->");
            output.add(ChatColor.WHITE + MinigameUtils.formStr(MinigameUtils.getLang("command.info.output.description"), minigame.getObjective()));
            output.add(ChatColor.WHITE + MinigameUtils.formStr(MinigameUtils.getLang("command.info.output.gameType"), minigame.getType().getName()));
            if (minigame.isEnabled() && minigame.hasStarted()) {
                if (minigame.getMinigameTimer() != null && minigame.getMinigameTimer().getTimeLeft() > 0) {
                    output.add(ChatColor.WHITE + MinigameUtils.formStr(MinigameUtils.getLang("command.info.output.Timer"), minigame.getMinigameTimer().getTimeLeft()));
                }
                if (minigame.hasPlayers()) {
                    output.add(ChatColor.WHITE + MinigameUtils.formStr(MinigameUtils.getLang("command.info.out.playerHeader"), minigame.getPlayers().size(), minigame.getMaxPlayers()));
                    if (minigame.isTeamGame()) {
                        for (Team t : TeamsModule.getMinigameModule(minigame).getTeams()) {
                            String teamData = MinigameUtils.formStr(MinigameUtils.getLang("command.info.output.teamData"), t.getDisplayName(), t.getScore(), t.getChatColor().name());
                            output.add(teamData);
                            output.add(ChatColor.GOLD + "~~~~~~~~~~~~~~~~~");
                            for (MinigamePlayer ply : t.getPlayers()) {
                                Integer score = ply.getScore();
                                Integer deaths = ply.getDeaths();
                                Integer reverts = ply.getReverts();
                                Integer kills = ply.getKills();
                                String name = ply.getTeam().getChatColor() + ply.getDisplayName(minigame.usePlayerDisplayNames()) + ChatColor.GRAY;
                                String playerData = MinigameUtils.formStr(MinigameUtils.getLang("command.info.output.playerData"), name, score, deaths, reverts, kills);
                                output.add(playerData + ChatColor.GRAY);
                            }
                        }
                    } else {
                        for (MinigamePlayer ply : minigame.getPlayers()) {
                            Integer score = ply.getScore();
                            Integer deaths = ply.getDeaths();
                            Integer reverts = ply.getReverts();
                            Integer kills = ply.getKills();
                            String name = ply.getDisplayName(minigame.usePlayerDisplayNames());
                            if (minigame.isTeamGame()) {
                                name = ply.getTeam().getChatColor() + name;
                            }
                            String playerData = MinigameUtils.formStr(MinigameUtils.getLang("command.info.output.playerData"), name, score, deaths, reverts, kills);
                            output.add(ChatColor.GRAY + playerData + ChatColor.GRAY);
                        }
                    }
                } else {
                    output.add(ChatColor.RED + MinigameUtils.getLang("command.info.output.noPlayer"));
                }
            } else {
                if (minigame.isEnabled()) {
                    output.add(ChatColor.RED + MinigameUtils.getLang("command.info.output.notStarted"));
                } else {
                    output.add(ChatColor.RED + MinigameUtils.getLang("minigame.error.notEnabled"));
                }
            }
            for (String out : output) {
                sender.sendMessage(out);
            }
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + MinigameUtils.getLang("command.info.noMinigame"));
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame, String alias, String[] args) {
        if (args.length == 1) {
            List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
            return MinigameUtils.tabCompleteMatch(mgs, args[1]);
        }
        return null;
    }
}
