package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TeleportCommand extends ACommand {
    private static final Pattern COORD_PATTERN = Pattern.compile("~?(?:-?[0-9]+(?:.[0-9]+)?)|~");

    @Override
    public @NotNull String getName() {
        return "teleport";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"tp"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_TELEPORT_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_TELEPORT_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.teleport";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        if (args.length > 0) {
            List<Player> plys = PLUGIN.getServer().matchPlayer(args[0]);
            MinigamePlayer mgPlayer;
            if (!plys.isEmpty()) {
                mgPlayer = PLUGIN.getPlayerManager().getMinigamePlayer(plys.get(0));
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAD_ERROR_NOTPLAYER,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                return true;
            }

            if (args.length >= 4 && COORD_PATTERN.matcher(args[1]).matches() &&
                    COORD_PATTERN.matcher(args[2]).matches() &&
                    COORD_PATTERN.matcher(args[3]).matches()) {
                double x, y, z;
                float yaw = mgPlayer.getLocation().getYaw();
                float pitch = mgPlayer.getLocation().getPitch();

                if (args[1].contains("~")) {
                    if (args[1].equals("~"))
                        x = mgPlayer.getPlayer().getLocation().getX();
                    else
                        x = mgPlayer.getPlayer().getLocation().getX() + Double.parseDouble(args[1].replace("~", ""));
                } else {
                    x = Double.parseDouble(args[1]);
                }

                if (args[2].contains("~")) {
                    if (args[2].equals("~"))
                        y = mgPlayer.getPlayer().getLocation().getY();
                    else
                        y = mgPlayer.getPlayer().getLocation().getY() + Double.parseDouble(args[2].replace("~", ""));
                } else {
                    y = Double.parseDouble(args[2]);
                }

                if (args[3].contains("~")) {
                    if (args[3].equals("~"))
                        z = mgPlayer.getPlayer().getLocation().getZ();
                    else
                        z = mgPlayer.getPlayer().getLocation().getZ() + Double.parseDouble(args[3].replace("~", ""));
                } else {
                    z = Double.parseDouble(args[3]);
                }

                if (args.length == 6 && COORD_PATTERN.matcher(args[4]).matches() && COORD_PATTERN.matcher(args[5]).matches()) {
                    if (args[4].contains("~")) {
                        if (args[4].equals("~"))
                            yaw = mgPlayer.getPlayer().getLocation().getYaw();
                        else
                            yaw = mgPlayer.getPlayer().getLocation().getYaw() + Float.parseFloat(args[4].replace("~", ""));
                    } else {
                        yaw = Float.parseFloat(args[4]);
                    }

                    if (args[5].contains("~")) {
                        if (args[5].equals("~"))
                            pitch = mgPlayer.getPlayer().getLocation().getPitch();
                        else {
                            pitch = mgPlayer.getPlayer().getLocation().getPitch() + Float.parseFloat(args[5].replace("~", ""));
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

                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_TELEPORT_TPCOORDS,
                        Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()));
                mgPlayer.teleport(new Location(mgPlayer.getPlayer().getWorld(), x, y, z, yaw, pitch));
                return true;
            } else if (args.length >= 2 && args[1].equalsIgnoreCase("start")) {
                if (mgPlayer.isInMinigame()) {
                    int pos = 0;
                    Team team = null;
                    if (args.length == 3)
                        team = TeamsModule.getMinigameModule(mgPlayer.getMinigame()).getTeam(TeamColor.matchColor(args[2]));
                    else if (mgPlayer.getTeam() != null)
                        team = mgPlayer.getTeam();

                    if (args.length >= 3 && args[2].matches("[0-9]+") && !args[2].equals("0"))
                        pos = Integer.parseInt(args[2]) - 1;

                    if (team == null && pos >= mgPlayer.getMinigame().getStartLocations().size())
                        pos = mgPlayer.getMinigame().getStartLocations().size() - 1;
                    else if (team != null && pos >= team.getStartLocations().size())
                        pos = team.getStartLocations().size() - 1;

                    if (team != null) {
                        mgPlayer.teleport(team.getStartLocations().get(pos));
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_TELEPORT_TEAMSTARTPOS,
                                Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()),
                                Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), team.getColoredDisplayName()),
                                Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(pos + 1)));
                    } else {
                        mgPlayer.teleport(mgPlayer.getMinigame().getStartLocations().get(pos));
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_TELEPORT_STARTPOS,
                                Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()),
                                Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(pos + 1)));
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTINMINIGAME_SELF,
                            Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()));
                }
                return true;
            } else if (args.length == 2 && args[1].equalsIgnoreCase("checkpoint")) {
                if (mgPlayer.isInMinigame()) {
                    mgPlayer.teleport(mgPlayer.getCheckpoint());
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_TELEPORT_CHEKPOINT,
                            Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTINMINIGAME_PLAYER,
                            Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()));
                }
                return true;
            } else if (args.length == 2) {
                plys = PLUGIN.getServer().matchPlayer(args[1]);
                MinigamePlayer mgPlayer2;

                if (!plys.isEmpty()) {
                    mgPlayer2 = PLUGIN.getPlayerManager().getMinigamePlayer(plys.get(0));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAD_ERROR_NOTPLAYER,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                    return true;
                }

                mgPlayer.teleport(mgPlayer2.getPlayer().getLocation());
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_TELEPORT_PLAYER2PLAYER,
                        Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()),
                        Placeholder.component(MinigamePlaceHolderKey.OTHER_PLAYER.getKey(), mgPlayer2.displayName()));
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> pl = new ArrayList<>();
            for (Player p : PLUGIN.getServer().getOnlinePlayers()) {
                pl.add(p.getName());
            }
            return MinigameUtils.tabCompleteMatch(pl, args[0]);
        } else if (args.length == 2) {
            List<String> playerNames = new ArrayList<>(PLUGIN.getServer().getOnlinePlayers().size() + 2);
            for (Player player : PLUGIN.getServer().getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            playerNames.add("Start");
            playerNames.add("Checkpoint");
            return MinigameUtils.tabCompleteMatch(playerNames, args[1]);
        }
        return null;
    }

}
