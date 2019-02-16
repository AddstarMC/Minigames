package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolModes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToolCommand implements ICommand {

    @Override
    public String getName() {
        return "tool";
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
        return "Spawns the Minigame tool for use in setting locations in a Minigame.";
    }

    @Override
    public String[] getParameters() {
        String[] arr = new String[ToolModes.getToolModes().size() + 4];
        for (int i = 0; i < arr.length - 4; i++)
            arr[i] = ToolModes.getToolModes().get(i).getName().toLowerCase();
        arr[arr.length - 4] = "team";
        arr[arr.length - 3] = "minigame";
        arr[arr.length - 2] = "select";
        arr[arr.length - 1] = "deselect";
        return arr;
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "/minigame tool <mode>",
                "/minigame tool team <team>",
                "/minigame tool minigame <Minigame>",
                "/minigame tool select",
                "/minigame tool deselect"
        };
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to use the Minigame Tool!";
    }

    @Override
    public String getPermission() {
        return "minigame.tool";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        MinigamePlayer player = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
        if (args == null) {
            MinigameUtils.giveMinigameTool(player);
        } else if (MinigameUtils.hasMinigameTool(player)) {
            if (args[0].equalsIgnoreCase("minigame") && args.length == 2) {
                if (Minigames.getPlugin().getMinigameManager().hasMinigame(args[1])) {
                    MinigameTool tool;
                    Minigame mg = Minigames.getPlugin().getMinigameManager().getMinigame(args[1]);
                    if (!MinigameUtils.hasMinigameTool(player))
                        tool = MinigameUtils.giveMinigameTool(player);
                    else
                        tool = MinigameUtils.getMinigameTool(player);

                    tool.setMinigame(mg);
                } else {
                    sender.sendMessage(ChatColor.RED + "No Minigame found by the name \"" + args[1] + "\"");
                }
            } else if (args[0].equalsIgnoreCase("select")) {
                MinigameTool tool;
                if (!MinigameUtils.hasMinigameTool(player))
                    tool = MinigameUtils.giveMinigameTool(player);
                else
                    tool = MinigameUtils.getMinigameTool(player);

                if (tool.getMinigame() != null && tool.getMode() != null) {
                    tool.getMode().select(player, tool.getMinigame(),
                            TeamsModule.getMinigameModule(tool.getMinigame()).getTeam(tool.getTeam()));
                } else if (tool.getMode() == null)
                    sender.sendMessage(ChatColor.RED + "You must have a mode selected to select anything!");
                else
                    sender.sendMessage(ChatColor.RED + "You must have a valid Minigame selected to use this tool!");
            } else if (args[0].equalsIgnoreCase("deselect")) {
                MinigameTool tool;
                if (!MinigameUtils.hasMinigameTool(player))
                    tool = MinigameUtils.giveMinigameTool(player);
                else
                    tool = MinigameUtils.getMinigameTool(player);

                if (tool.getMinigame() != null && tool.getMode() != null) {
                    tool.getMode().deselect(player, tool.getMinigame(),
                            TeamsModule.getMinigameModule(tool.getMinigame()).getTeam(tool.getTeam()));
                } else if (tool.getMode() == null)
                    sender.sendMessage(ChatColor.RED + "You must have a mode selected to deselect anything!");
                else
                    sender.sendMessage(ChatColor.RED + "You must have a valid Minigame selected to use this tool!");
            } else if (args[0].equalsIgnoreCase("team") && args.length == 2) {
                if (TeamColor.matchColor(args[1]) != null || args[1].equalsIgnoreCase("none")) {
                    MinigameTool tool;
                    if (!MinigameUtils.hasMinigameTool(player))
                        tool = MinigameUtils.giveMinigameTool(player);
                    else
                        tool = MinigameUtils.getMinigameTool(player);

                    if (args[1].equalsIgnoreCase("none"))
                        tool.setTeam(null);
                    else
                        tool.setTeam(TeamColor.matchColor(args[1]));
                    sender.sendMessage(ChatColor.GRAY + "Set the tools team to " + args[1]);
                } else {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not a valid team color!");
                }
            } else if (ToolModes.getToolMode(args[0]) != null) {
                MinigameTool tool;
                if (!MinigameUtils.hasMinigameTool(player))
                    tool = MinigameUtils.giveMinigameTool(player);
                else
                    tool = MinigameUtils.getMinigameTool(player);

                tool.setMode(ToolModes.getToolMode(args[0]));
                sender.sendMessage(ChatColor.GRAY + "Set the tools mode to '" + tool.getMode().getName().toLowerCase().replace("_", " ") + "'");
            } else {
                return false;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must have a Minigame Tool! Type \"/minigame tool\" to recieve one.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        List<String> ret = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
        if (args.length == 1) {
            Collections.addAll(ret, getParameters());
            return MinigameUtils.tabCompleteMatch(ret, args[0]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("team")) {
            for (TeamColor col : TeamColor.values())
                ret.add(col.toString());
            return MinigameUtils.tabCompleteMatch(ret, args[1]);
        }
        return MinigameUtils.tabCompleteMatch(ret, args[args.length - 1]);
    }

}
