package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

public class TeamSign implements MinigameSign {

    private final Minigames plugin = Minigames.getPlugin();

    @Override
    public @NotNull String getName() {
        return "Team";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.team";
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.team";
    }

    @Override
    public boolean signCreate(@NotNull SignChangeEvent event) {
        event.setLine(1, ChatColor.GREEN + "Team");
        if (event.getLine(2).equalsIgnoreCase("neutral")) {
            event.setLine(2, ChatColor.GRAY + "Neutral");
            return true;
        } else {
            TeamColor color = TeamColor.matchColor(event.getLine(2));
            if (color != null) {
                event.line(2, Component.text(WordUtils.capitalizeFully(color.toString().replace("_", " ")), color.getColor()));
                return true;
            }
        }

        MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.SIGN_ERROR_TEAM_INVALIDFORMAT);
        return false;
    }

    @Override
    public boolean signUse(@NotNull Sign sign, @NotNull MinigamePlayer mgPlayer) {
        if (mgPlayer.isInMinigame()) {
            Minigame mgm = mgPlayer.getMinigame();
            if (mgm.isTeamGame()) {
                if (mgPlayer.getTeam() != matchTeam(mgm, sign.getLine(2))) {
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
                                    MultiplayerType.switchTeam(mgm, mgPlayer, nt);
                                    MinigameMessageManager.sendMinigameMessage(mgm, MiniMessage.miniMessage().deserialize(nt.getJoinAnnounceMessage(),
                                                    Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getDisplayName(mgm.usePlayerDisplayNames())),
                                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(nt.getDisplayName(), nt.getTextColor()))),
                                            MinigameMessageType.INFO, mgPlayer);

                                    MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, MiniMessage.miniMessage().deserialize(nt.getPlayerAssignMessage(),
                                            Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(nt.getDisplayName(), nt.getTextColor()))));
                                } else {
                                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.SIGN_TEAM_ERROR_UNBALANCE);
                                }

                                mgPlayer.getPlayer().damage(mgPlayer.getPlayer().getHealth());
                            } else {
                                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.PLAYER_TEAM_ASSIGN_ERROR_FULL);
                            }
                        }
                    } else if (sign.getLine(2).equals(ChatColor.GRAY + "Neutral") || matchTeam(mgm, sign.getLine(2)) != mgPlayer.getTeam()) {
                        Team currentTeam = mgPlayer.getTeam();
                        Team nt = matchTeam(mgm, sign.getLine(2));
                        if (currentTeam != null) {
                            if (nt != null) {
                                if (nt.getPlayers().size() - currentTeam.getPlayers().size() < 2) { //todo this breaks with more then 2 teams
                                    MultiplayerType.switchTeam(mgm, mgPlayer, nt);
                                    MinigameMessageManager.sendMinigameMessage(mgm, MiniMessage.miniMessage().deserialize(nt.getJoinAnnounceMessage(),
                                                    Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getDisplayName(mgm.usePlayerDisplayNames())),
                                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(nt.getDisplayName(), nt.getTextColor()))),
                                            MinigameMessageType.INFO, mgPlayer);

                                    MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, MiniMessage.miniMessage().deserialize(nt.getPlayerAssignMessage(),
                                            Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(nt.getDisplayName(), nt.getTextColor()))));
                                } else {
                                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.SIGN_TEAM_ERROR_UNBALANCE);
                                }
                            } else {
                                mgPlayer.removeTeam();
                            }
                        } else {
                            if (nt != null) {
                                if (nt.getPlayers().size() < nt.getMaxPlayers()) { // todo this does not check balancing
                                    MultiplayerType.switchTeam(mgm, mgPlayer, nt);
                                    MinigameMessageManager.sendMinigameMessage(mgm, MiniMessage.miniMessage().deserialize(nt.getJoinAnnounceMessage(),
                                                    Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getDisplayName(mgm.usePlayerDisplayNames())),
                                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(nt.getDisplayName(), nt.getTextColor()))),
                                            MinigameMessageType.INFO, mgPlayer);

                                    MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, MiniMessage.miniMessage().deserialize(nt.getPlayerAssignMessage(),
                                            Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(nt.getDisplayName(), nt.getTextColor()))));
                                } else {
                                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.PLAYER_TEAM_ASSIGN_ERROR_FULL);
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
    public void signBreak(@NotNull Sign sign, MinigamePlayer mgPlayer) {

    }

    private Team matchTeam(Minigame mgm, String text) {
        TeamColor col = TeamColor.matchColor(ChatColor.stripColor(text).replace(" ", "_"));
        if (TeamsModule.getMinigameModule(mgm).hasTeam(col))
            return TeamsModule.getMinigameModule(mgm).getTeam(col);
        return null;
    }

}
