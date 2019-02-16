package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeleportCommand implements ICommand {

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"tp"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Teleports a defined player to specific coordinates, another player or a specific Minigame point. " +
                "Supports the use of ~ in coordinates to teleport them relative to where they are standing. " +
                "\n Eg: \"~ ~5 ~\" will teleport a player 5 blocks above their current position.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "/minigame teleport <Player> <x> <y> <z> [yaw] [pitch]",
                "/minigame teleport <Player> Start [id] [team]",
                "/minigame teleport <Player> Checkpoint",
                "/minigame teleport <Player> <Player>"
        };
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to teleport players!";
    }

    @Override
    public String getPermission() {
        return "minigame.teleport";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            List<Player> plys = plugin.getServer().matchPlayer(args[0]);
            MinigamePlayer ply = null;
            if (!plys.isEmpty()) {
                ply = plugin.getPlayerManager().getMinigamePlayer(plys.get(0));
            } else {
                sender.sendMessage(ChatColor.RED + "No player found by the name " + args[0] + "!");
                return true;
            }

            if (args.length >= 4 && args[1].matches("~?(-?[0-9]+(.[0-9]+)?)|~") &&
                    args[2].matches("~?(-?[0-9]+(.[0-9]+)?)|~") &&
                    args[3].matches("~?(-?[0-9]+(.[0-9]+)?)|~")) {
                double x = 0;
                double y = 0;
                double z = 0;
                float yaw = ply.getLocation().getYaw();
                float pitch = ply.getLocation().getPitch();

                if (args[1].contains("~")) {
                    if (args[1].equals("~"))
                        x = ply.getPlayer().getLocation().getX();
                    else
                        x = ply.getPlayer().getLocation().getX() + Double.parseDouble(args[1].replace("~", ""));
                } else {
                    x = Double.parseDouble(args[1]);
                }

                if (args[2].contains("~")) {
                    if (args[2].equals("~"))
                        y = ply.getPlayer().getLocation().getY();
                    else
                        y = ply.getPlayer().getLocation().getY() + Double.parseDouble(args[2].replace("~", ""));
                } else {
                    y = Double.parseDouble(args[2]);
                }

                if (args[3].contains("~")) {
                    if (args[3].equals("~"))
                        z = ply.getPlayer().getLocation().getZ();
                    else
                        z = ply.getPlayer().getLocation().getZ() + Double.parseDouble(args[3].replace("~", ""));
                } else {
                    z = Double.parseDouble(args[3]);
                }

                if (args.length == 6 && args[4].matches("~?(-?[0-9]+(.[0-9]+)?)|~") && args[5].matches("~?(-?[0-9]+(.[0-9]+)?)|~")) {
                    if (args[4].contains("~")) {
                        if (args[4].equals("~"))
                            yaw = ply.getPlayer().getLocation().getYaw();
                        else
                            yaw = ply.getPlayer().getLocation().getYaw() + Float.parseFloat(args[4].replace("~", ""));
                    } else {
                        yaw = Float.parseFloat(args[4]);
                    }

                    if (args[5].contains("~")) {
                        if (args[5].equals("~"))
                            pitch = ply.getPlayer().getLocation().getPitch();
                        else {
                            pitch = ply.getPlayer().getLocation().getPitch() + Float.parseFloat(args[5].replace("~", ""));
                        }
                    } else {
                        pitch = Float.parseFloat(args[5]);
                    }

                    if (pitch > 90) {
                        pitch = 90f;
                    } else if (pitch < -90) {
                        pitch = -90f;
                    }
                }

                sender.sendMessage(ChatColor.GRAY + "Teleported " + ply.getName() + " to assigned coordinates.");
                ply.teleport(new Location(ply.getPlayer().getWorld(), x, y, z, yaw, pitch));
                return true;
            } else if (args.length >= 2 && args[1].equalsIgnoreCase("start")) {
                if (ply.isInMinigame()) {
                    int pos = 0;
                    Team team = null;
                    if (args.length == 3)
                        team = TeamsModule.getMinigameModule(ply.getMinigame()).getTeam(TeamColor.matchColor(args[3]));
                    else if (ply.getTeam() != null)
                        team = ply.getTeam();

                    if (args.length >= 3 && args[2].matches("[0-9]+") && !args[2].equals("0"))
                        pos = Integer.parseInt(args[2]) - 1;

                    if (team == null && pos >= ply.getMinigame().getStartLocations().size())
                        pos = ply.getMinigame().getStartLocations().size() - 1;
                    else if (team != null && pos >= team.getStartLocations().size())
                        pos = team.getStartLocations().size() - 1;

                    if (team != null) {
                        ply.teleport(team.getStartLocations().get(pos));
                        sender.sendMessage(ChatColor.GRAY + "Teleported " + ply.getName() + " to " + team.getDisplayName() + " start position " + (pos + 1) + ".");
                    } else {
                        ply.teleport(ply.getMinigame().getStartLocations().get(pos));
                        sender.sendMessage(ChatColor.GRAY + "Teleported " + ply.getName() + " to start position " + (pos + 1) + ".");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + ply.getName() + " is not in a Minigame!");
                }
                return true;
            } else if (args.length == 2 && args[1].equalsIgnoreCase("checkpoint")) {
                if (ply.isInMinigame()) {
                    ply.teleport(ply.getCheckpoint());
                    sender.sendMessage(ChatColor.GRAY + "Teleported " + ply.getName() + " to their checkpoint.");
                } else {
                    sender.sendMessage(ChatColor.RED + ply.getName() + " is not in a Minigame!");
                }
                return true;
            } else if (args.length == 2) {
                plys = plugin.getServer().matchPlayer(args[1]);
                MinigamePlayer ply2 = null;

                if (!plys.isEmpty()) {
                    ply2 = plugin.getPlayerManager().getMinigamePlayer(plys.get(0));
                } else {
                    sender.sendMessage(ChatColor.RED + "No player found by the name " + args[1] + "!");
                    return true;
                }

                ply.teleport(ply2.getPlayer().getLocation());
                sender.sendMessage(ChatColor.GRAY + "Teleported " + ply.getName() + " to " + ply2.getName() + ".");
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1) {
            List<String> pl = new ArrayList<>();
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                pl.add(p.getName());
            }
            return MinigameUtils.tabCompleteMatch(pl, args[0]);
        } else if (args.length == 2) {
            List<String> pl = new ArrayList<>(plugin.getServer().getOnlinePlayers().size() + 2);
            for (Player ply : plugin.getServer().getOnlinePlayers()) {
                pl.add(ply.getName());
            }
            pl.add("Start");
            pl.add("Checkpoint");
            return MinigameUtils.tabCompleteMatch(pl, args[1]);
        }
        return null;
    }

}
