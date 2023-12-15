package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.apache.commons.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public class TeamSign implements MinigameSign {

    private final Minigames plugin = Minigames.getPlugin();

    @Override
    public String getName() {
        return "Team";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.team";
    }

    @Override
    public String getCreatePermissionMessage() {
        return MinigameMessageManager.getMessage(null, "sign.team.createPermission");
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.team";
    }

    @Override
    public String getUsePermissionMessage() {
        return MinigameMessageManager.getMessage(null, "sign.team.usePermission");
    }

    @Override
    public boolean signCreate(SignChangeEvent event) {
        event.setLine(1, ChatColor.GREEN + "Team");
        if (TeamColor.matchColor(event.getLine(2)) != null ||
                event.getLine(2).equalsIgnoreCase("neutral")) {
            if (event.getLine(2).equalsIgnoreCase("neutral")) {
                event.setLine(2, ChatColor.GRAY + "Neutral");
            } else {
                TeamColor col = TeamColor.matchColor(event.getLine(2));
                event.setLine(2, col.getColor() + WordUtils.capitalize(col.toString().replace("_", " ")));
            }
            return true;
        }
        event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameMessageManager.getMessage(null, "sign.team.invalidFormat", "\"red\", \"blue\" or \"neutral\""));
        return false;
    }

    @Override
    public boolean signUse(Sign sign, MinigamePlayer player) {
        if (player.isInMinigame()) {
            Minigame mgm = player.getMinigame();
            if (mgm.isTeamGame()) {
                if (player.getTeam() != matchTeam(mgm, sign.getLine(2))) {
                    if (!mgm.isWaitingForPlayers() && !sign.getLine(2).equals(ChatColor.GRAY + "Neutral")) {
                        Team sm = null;
                        Team nt = matchTeam(mgm, sign.getLine(2));
                        if (nt != null) {
                            if (nt.hasRoom()) {
                                for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                                    if (sm == null || t.getPlayers().size() < sm.getPlayers().size())
                                        sm = t;
                                }
                                if (nt.getPlayers().size() - sm.getPlayers().size() < 1) {
                                    MultiplayerType.switchTeam(mgm, player, nt);
                                    plugin.getMinigameManager().sendMinigameMessage(mgm, String.format(nt.getGameAssignMessage(), player.getDisplayName(mgm.usePlayerDisplayNames()), nt.getTextColor() + nt.getDisplayName()), null, player);
                                    player.sendInfoMessage(String.format(nt.getUnformattedAssignMessage(), nt.getTextColor() + nt.getDisplayName()));
                                } else {
                                    player.sendInfoMessage(MinigameMessageManager.getMessage(null, "sign.team.noUnbalance"));
                                }

                                player.getPlayer().damage(player.getPlayer().getHealth());
                            } else {
                                player.sendMessage(MinigameMessageManager.getMessage(null, "player.team.assign.full"), MinigameMessageType.ERROR);
                            }
                        }
                    } else if (sign.getLine(2).equals(ChatColor.GRAY + "Neutral") || matchTeam(mgm, sign.getLine(2)) != player.getTeam()) {
                        Team cur = player.getTeam();
                        if (cur != null) {
                            Team nt = matchTeam(mgm, sign.getLine(2));
                            if (nt != null) {
                                if (nt.getPlayers().size() - cur.getPlayers().size() < 2) {
                                    MultiplayerType.switchTeam(mgm, player, nt);
                                    plugin.getMinigameManager().sendMinigameMessage(mgm, String.format(nt.getGameAssignMessage(), player.getName(), nt.getTextColor() + nt.getDisplayName()), null, player);
                                    player.sendInfoMessage(String.format(nt.getUnformattedAssignMessage(), nt.getTextColor() + nt.getDisplayName()));
                                } else {
                                    player.sendInfoMessage(MinigameMessageManager.getMessage(null, "sign.team.noUnbalance"));
                                }
                            } else {
                                player.removeTeam();
                            }
                        } else {
                            Team nt = matchTeam(mgm, sign.getLine(2));
                            if (nt != null) {
                                if (nt.getPlayers().size() < nt.getMaxPlayers()) {
                                    MultiplayerType.switchTeam(mgm, player, nt);
                                    plugin.getMinigameManager().sendMinigameMessage(mgm, String.format(nt.getGameAssignMessage(), player.getName(), nt.getTextColor() + nt.getDisplayName()), null, player);
                                    player.sendMessage(String.format(nt.getUnformattedAssignMessage(), nt.getTextColor() + nt.getDisplayName()), MinigameMessageType.INFO);
                                } else {
                                    player.sendInfoMessage(MinigameMessageManager.getMessage(null, "player.team.assign.full"));
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void signBreak(Sign sign, MinigamePlayer player) {

    }

    private Team matchTeam(Minigame mgm, String text) {
        TeamColor col = TeamColor.matchColor(ChatColor.stripColor(text).replace(" ", "_"));
        if (TeamsModule.getMinigameModule(mgm).hasTeam(col))
            return TeamsModule.getMinigameModule(mgm).getTeam(col);
        return null;
    }

}
