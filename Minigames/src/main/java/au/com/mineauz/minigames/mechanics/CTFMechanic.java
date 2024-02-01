package au.com.mineauz.minigames.mechanics;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.events.DropFlagEvent;
import au.com.mineauz.minigames.events.FlagCaptureEvent;
import au.com.mineauz.minigames.events.TakeFlagEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.CTFModule;
import au.com.mineauz.minigames.minigame.modules.MgModules;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.CTFFlag;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class CTFMechanic extends GameMechanicBase {

    @Override
    public String getMechanic() {
        return "ctf";
    }

    @Override
    public EnumSet<MinigameType> validTypes() {
        return EnumSet.of(MinigameType.MULTIPLAYER);
    }

    @Override
    public boolean checkCanStart(@NotNull Minigame minigame, @Nullable MinigamePlayer caller) {
        return true;
    }

    @Override
    public MinigameModule displaySettings(Minigame minigame) {
        return minigame.getModule(MgModules.CAPTURE_THE_FLAG.getName());
    }

    @Override
    public void startMinigame(Minigame minigame, MinigamePlayer caller) {
    }

    @Override
    public void stopMinigame(Minigame minigame) {
    }

    @Override
    public void onJoinMinigame(Minigame minigame, MinigamePlayer player) {
    }

    @Override
    public void quitMinigame(Minigame minigame, MinigamePlayer player,
                             boolean forced) {
        if (minigame.isFlagCarrier(player)) {
            minigame.getFlagCarrier(player).stopCarrierParticleEffect();
            minigame.getFlagCarrier(player).respawnFlag();
            minigame.removeFlagCarrier(player);
        }
        if (minigame.getPlayers().size() == 1) {
            minigame.resetFlags();
        }
    }

    /**
     * Sending a ctf relevant message
     *
     * @param minigame The minigame in which this message shall be sent
     * @param message  The message
     */
    private static void sendCTFMessage(final @NotNull Minigame minigame, final @NotNull Component message) {
        if (!minigame.getShowCTFBroadcasts()) {
            return;
        }
        MinigameMessageManager.sendBroadcastMessageUnchecked(minigame, message, MinigameMessageType.INFO, null);
    }

    @Override
    public void endMinigame(Minigame minigame, List<MinigamePlayer> winners,
                            List<MinigamePlayer> losers) {
        for (MinigamePlayer pl : winners) {
            if (minigame.isFlagCarrier(pl)) {
                minigame.getFlagCarrier(pl).stopCarrierParticleEffect();
                minigame.getFlagCarrier(pl).respawnFlag();
                minigame.removeFlagCarrier(pl);
            }
        }
        if (minigame.getPlayers().size() == 1) {
            minigame.resetFlags();
        }
    }

    @EventHandler
    private void takeFlag(@NotNull PlayerInteractEvent event) { //todo better system of getting type of sign --> should be a getter in sign base
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        if (mgPlayer.isInMinigame() && !mgPlayer.getPlayer().isDead() && mgPlayer.getMinigame().hasStarted()) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (event.getClickedBlock().getState() instanceof Sign sign) && mgPlayer.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
                Minigame mgm = mgPlayer.getMinigame();
                if (mgm.getMechanicName().equals("ctf") && sign.getLine(1).equals(ChatColor.GREEN + "Flag")) {
                    Team team = mgPlayer.getTeam();

                    String sloc = MinigameUtils.createLocationID(event.getClickedBlock().getLocation());

                    if (sign.getLine(2).equalsIgnoreCase(team.getTextColor() + team.getColor().toString()) &&
                            mgm.hasDroppedFlag(sloc) &&
                            !(sloc.equals(MinigameUtils.createLocationID(mgm.getDroppedFlag(sloc).getSpawnLocation())))) {
                        if (CTFModule.getMinigameModule(mgm).getBringFlagBackManual()) {
                            CTFFlag flag = mgm.getDroppedFlag(sloc);
                            flag.stopTimer();
                            mgm.removeDroppedFlag(sloc);
                            String newID = MinigameUtils.createLocationID(flag.getSpawnLocation());
                            mgm.addDroppedFlag(newID, flag);
                            flag.respawnFlag();

                            MinigameMessageManager.sendMinigameMessage(mgm, MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_FLAG_RETURNEDTEAM,
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(team.getDisplayName(), team.getTextColor()))), MinigameMessageType.INFO);
                        }
                    } else if ((!sign.getLine(2).equalsIgnoreCase(team.getTextColor() + team.getColor().toString()) && !sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture")) ||
                            sign.getLine(2).equalsIgnoreCase(ChatColor.GRAY + "Neutral")) {
                        if (mgm.getFlagCarrier(mgPlayer) == null) {
                            TakeFlagEvent ev = null;
                            if (!mgm.hasDroppedFlag(sloc) &&
                                    (TeamsModule.getMinigameModule(mgm).hasTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(2)))) ||
                                            sign.getLine(2).equalsIgnoreCase(ChatColor.GRAY + "Neutral"))) {
                                Team oTeam = TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(2))));
                                CTFFlag flag = new CTFFlag(sign, oTeam, mgm);
                                ev = new TakeFlagEvent(mgm, mgPlayer, flag);
                                Bukkit.getPluginManager().callEvent(ev);
                                if (!ev.isCancelled()) {
                                    mgm.addFlagCarrier(mgPlayer, flag);
                                    flag.removeFlag();
                                }
                            } else if (mgm.hasDroppedFlag(sloc)) {
                                CTFFlag flag = mgm.getDroppedFlag(sloc);
                                ev = new TakeFlagEvent(mgm, mgPlayer, flag);
                                Bukkit.getPluginManager().callEvent(ev);
                                if (!ev.isCancelled()) {
                                    mgm.addFlagCarrier(mgPlayer, flag);
                                    if (!flag.isAtHome()) {
                                        flag.stopTimer();
                                    }
                                    flag.removeFlag();
                                }
                            }

                            if (mgm.getFlagCarrier(mgPlayer) != null && !ev.isCancelled()) {
                                if (mgm.getFlagCarrier(mgPlayer).getTeam() != null) {
                                    Team flagTeam = mgm.getFlagCarrier(mgPlayer).getTeam();
                                    sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_CTF_STOLE,
                                                    Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName()),
                                            Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(flagTeam.getDisplayName(), flagTeam.getTextColor())))
                                    );
                                    mgm.getFlagCarrier(mgPlayer).startCarrierParticleEffect(mgPlayer.getPlayer());
                                } else {
                                    sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_CTF_NEUTRAL_STOLE,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName()))
                                    );
                                    mgm.getFlagCarrier(mgPlayer).startCarrierParticleEffect(mgPlayer.getPlayer());
                                }
                            }
                        }

                    } else if (team == TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(2)))) && CTFModule.getMinigameModule(mgm).getUseFlagAsCapturePoint() ||
                            (team == TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(3)))) && sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture")) ||
                            (sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture") && sign.getLine(3).equalsIgnoreCase(ChatColor.GRAY + "Neutral"))) {

                        String clickID = MinigameUtils.createLocationID(event.getClickedBlock().getLocation());

                        if (mgm.getFlagCarrier(mgPlayer) != null && (!mgm.hasDroppedFlag(clickID) || mgm.getDroppedFlag(clickID).isAtHome())) {
                            CTFFlag flag = mgm.getFlagCarrier(mgPlayer);
                            FlagCaptureEvent ev = new FlagCaptureEvent(mgm, mgPlayer, flag);
                            Bukkit.getPluginManager().callEvent(ev);
                            if (!ev.isCancelled()) {
                                flag.respawnFlag();
                                String id = MinigameUtils.createLocationID(flag.getSpawnLocation());
                                mgm.addDroppedFlag(id, flag);
                                mgm.removeFlagCarrier(mgPlayer);

                                boolean end = false;

                                if (mgm.isTeamGame()) {
                                    mgPlayer.getTeam().addScore();
                                    if (mgm.getMaxScore() != 0 && mgPlayer.getTeam().getScore() >= mgm.getMaxScorePerPlayer()) {
                                        end = true;
                                    }

                                    if (!end) {
                                        sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_CTF_CAPTURE,
                                                Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName()),
                                                Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(mgPlayer.getTeam().getDisplayName(), mgPlayer.getTeam().getTextColor()))
                                        ));
                                    }
                                    flag.stopCarrierParticleEffect();
                                    mgPlayer.addScore();
                                    mgm.setScore(mgPlayer, mgPlayer.getScore());

                                    if (end) {
                                        sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_CTF_CAPTUREFINAL,
                                                        Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName()),
                                                Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(mgPlayer.getTeam().getDisplayName(), mgPlayer.getTeam().getTextColor())))
                                        );
                                        List<MinigamePlayer> w = new ArrayList<>(mgPlayer.getTeam().getPlayers());
                                        List<MinigamePlayer> l = new ArrayList<>(mgm.getPlayers().size() - mgPlayer.getTeam().getPlayers().size());
                                        for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                                            if (t != mgPlayer.getTeam())
                                                l.addAll(t.getPlayers());
                                        }
                                        plugin.getPlayerManager().endMinigame(mgm, w, l);
                                        mgm.resetFlags();
                                    }
                                } else {
                                    mgPlayer.addScore();
                                    mgm.setScore(mgPlayer, mgPlayer.getScore());
                                    if (mgm.getMaxScore() != 0 && mgPlayer.getScore() >= mgm.getMaxScorePerPlayer()) {
                                        end = true;
                                    }

                                    sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_CTF_NEUTRAL_CAPTURE,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName())));
                                    flag.stopCarrierParticleEffect();

                                    if (end) {
                                        sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_CTF_NEUTRAL_CAPTUREFINAL,
                                                Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName())));

                                        pdata.endMinigame(mgPlayer);
                                        mgm.resetFlags();
                                    }
                                }
                            }
                        } else if (mgm.getFlagCarrier(mgPlayer) == null && mgm.hasDroppedFlag(clickID) && !mgm.getDroppedFlag(clickID).isAtHome()) {
                            CTFFlag flag = mgm.getDroppedFlag(sloc);
                            if (mgm.hasDroppedFlag(sloc)) {
                                mgm.removeDroppedFlag(sloc);
                                String newID = MinigameUtils.createLocationID(flag.getSpawnLocation());
                                mgm.addDroppedFlag(newID, flag);
                            }
                            flag.respawnFlag();

                            sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_CTF_RETURNED,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName()),
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(mgPlayer.getTeam().getDisplayName(), mgPlayer.getTeam().getTextColor())))
                            );
                        } else if (mgm.getFlagCarrier(mgPlayer) != null && mgm.hasDroppedFlag(clickID) && !mgm.getDroppedFlag(clickID).isAtHome()) {
                            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.LOSS, MinigameLangKey.PLAYER_CTF_RETURNFAIL);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private void dropFlag(@NotNull PlayerDeathEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
        if (ply.isInMinigame()) {
            Minigame mgm = ply.getMinigame();
            if (mgm.isFlagCarrier(ply)) {
                CTFFlag flag = mgm.getFlagCarrier(ply);
                Location loc = flag.spawnFlag(ply.getPlayer().getLocation());
                if (loc != null) {
                    DropFlagEvent ev = new DropFlagEvent(mgm, flag, ply);
                    Bukkit.getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        String id = MinigameUtils.createLocationID(loc);
                        Team team = mgm.getFlagCarrier(ply).getTeam();
                        mgm.addDroppedFlag(id, flag);
                        mgm.removeFlagCarrier(ply);

                        if (team != null) {
                            sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_CTF_DROPPED,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), ply.getName()),
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(team.getDisplayName(), team.getTextColor())))
                            );
                        } else {
                            sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_CTF_NEUTRAL_DROPPED,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), ply.getName()))
                            );
                        }
                        flag.stopCarrierParticleEffect();
                        flag.startReturnTimer();
                    }
                } else {
                    flag.respawnFlag();
                    mgm.removeFlagCarrier(ply);
                    flag.stopCarrierParticleEffect();
                }
            }
        }
    }

    @EventHandler
    private void playerAutoBalance(@NotNull PlayerDeathEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
        if (ply.isInMinigame() && ply.getMinigame().getType() == MinigameType.MULTIPLAYER && ply.getMinigame().isTeamGame()) {
            Minigame mgm = ply.getMinigame();
            if (mgm.getMechanicName().equals("ctf")) { // todo mechanic manager
                autoBalanceOnDeath(ply, mgm);
            }
        }
    }
}
