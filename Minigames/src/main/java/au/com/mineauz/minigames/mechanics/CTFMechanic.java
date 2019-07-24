package au.com.mineauz.minigames.mechanics;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.CTFFlag;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.events.DropFlagEvent;
import au.com.mineauz.minigames.events.FlagCaptureEvent;
import au.com.mineauz.minigames.events.TakeFlagEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.CTFModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
    public boolean checkCanStart(Minigame minigame, MinigamePlayer caller) {
        return true;
    }

    @Override
    public MinigameModule displaySettings(Minigame minigame) {
        return CTFModule.getMinigameModule(minigame);
    }

    @Override
    public void startMinigame(Minigame minigame, MinigamePlayer caller) {
    }

    @Override
    public void stopMinigame(Minigame minigame, MinigamePlayer caller) {
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
    public void takeFlag(PlayerInteractEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply == null) return;
        if (ply.isInMinigame() && !ply.getPlayer().isDead() && ply.getMinigame().hasStarted()) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (event.getClickedBlock().getType() == Material.OAK_SIGN || event.getClickedBlock().getType() == Material.OAK_WALL_SIGN) && ply.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
                Minigame mgm = ply.getMinigame();
                Sign sign = (Sign) event.getClickedBlock().getState();
                if (mgm.getMechanicName().equals("ctf") && sign.getLine(1).equals(ChatColor.GREEN + "Flag")) {
                    Team team = ply.getTeam();

                    String sloc = MinigameUtils.createLocationID(event.getClickedBlock().getLocation());

                    if ((!sign.getLine(2).equalsIgnoreCase(team.getChatColor() + team.getColor().toString()) && !sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture")) ||
                            sign.getLine(2).equalsIgnoreCase(ChatColor.GRAY + "Neutral")) {
                        if (mgm.getFlagCarrier(ply) == null) {
                            TakeFlagEvent ev = null;
                            if (!mgm.hasDroppedFlag(sloc) &&
                                    (TeamsModule.getMinigameModule(mgm).hasTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(2)))) ||
                                            sign.getLine(2).equalsIgnoreCase(ChatColor.GRAY + "Neutral"))) {
                                Team oTeam = TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(2))));
                                CTFFlag flag = new CTFFlag(event.getClickedBlock().getLocation(), oTeam, event.getPlayer(), mgm);
                                ev = new TakeFlagEvent(mgm, ply, flag);
                                Bukkit.getPluginManager().callEvent(ev);
                                if (!ev.isCancelled()) {
                                    mgm.addFlagCarrier(ply, flag);
                                    flag.removeFlag();
                                }
                            } else if (mgm.hasDroppedFlag(sloc)) {
                                CTFFlag flag = mgm.getDroppedFlag(sloc);
                                ev = new TakeFlagEvent(mgm, ply, flag);
                                Bukkit.getPluginManager().callEvent(ev);
                                if (!ev.isCancelled()) {
                                    mgm.addFlagCarrier(ply, flag);
                                    if (!flag.isAtHome()) {
                                        flag.stopTimer();
                                    }
                                    flag.removeFlag();
                                }
                            }


                            if (mgm.getFlagCarrier(ply) != null && !ev.isCancelled()) {
                                if (mgm.getFlagCarrier(ply).getTeam() != null) {
                                    Team fteam = mgm.getFlagCarrier(ply).getTeam();
                                    String message = ply.getName() + " stole " + fteam.getChatColor() + fteam.getDisplayName() + ChatColor.WHITE + "'s flag!";
                                    mdata.sendMinigameMessage(mgm, message);
                                    mgm.getFlagCarrier(ply).startCarrierParticleEffect(ply.getPlayer());
                                } else {
                                    String message = ply.getName() + " stole the " + ChatColor.GRAY + "neutral" + ChatColor.WHITE + " flag!";
                                    mdata.sendMinigameMessage(mgm, message);
                                    mgm.getFlagCarrier(ply).startCarrierParticleEffect(ply.getPlayer());
                                }
                            }
                        }

                    } else if (team == TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(2)))) && CTFModule.getMinigameModule(mgm).getUseFlagAsCapturePoint() ||
                            (team == TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(3)))) && sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture")) ||
                            (sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture") && sign.getLine(3).equalsIgnoreCase(ChatColor.GRAY + "Neutral"))) {

                        String clickID = MinigameUtils.createLocationID(event.getClickedBlock().getLocation());

                        if (mgm.getFlagCarrier(ply) != null && (!mgm.hasDroppedFlag(clickID) || mgm.getDroppedFlag(clickID).isAtHome())) {
                            CTFFlag flag = mgm.getFlagCarrier(ply);
                            FlagCaptureEvent ev = new FlagCaptureEvent(mgm, ply, flag);
                            Bukkit.getPluginManager().callEvent(ev);
                            if (!ev.isCancelled()) {
                                flag.respawnFlag();
                                String id = MinigameUtils.createLocationID(flag.getSpawnLocation());
                                mgm.addDroppedFlag(id, flag);
                                mgm.removeFlagCarrier(ply);

                                boolean end = false;

                                if (mgm.isTeamGame()) {
                                    ply.getTeam().addScore();
                                    if (mgm.getMaxScore() != 0 && ply.getTeam().getScore() >= mgm.getMaxScorePerPlayer())
                                        end = true;

                                    if (!end) {
                                        String message = MinigameUtils.formStr("player.ctf.capture",
                                                ply.getName(), ply.getTeam().getChatColor() + ply.getTeam().getDisplayName());
                                        mdata.sendMinigameMessage(mgm, message);
                                    }
                                    flag.stopCarrierParticleEffect();
                                    ply.addScore();
                                    mgm.setScore(ply, ply.getScore());

                                    if (end) {
                                        mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.captureFinal", ply.getName(),
                                                ply.getTeam().getChatColor() + ply.getTeam().getDisplayName()));
                                        List<MinigamePlayer> w = new ArrayList<>(ply.getTeam().getPlayers());
                                        List<MinigamePlayer> l = new ArrayList<>(mgm.getPlayers().size() - ply.getTeam().getPlayers().size());
                                        for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                                            if (t != ply.getTeam())
                                                l.addAll(t.getPlayers());
                                        }
                                        plugin.getPlayerManager().endMinigame(mgm, w, l);
                                        mgm.resetFlags();
                                    }
                                } else {
                                    ply.addScore();
                                    mgm.setScore(ply, ply.getScore());
                                    if (mgm.getMaxScore() != 0 && ply.getScore() >= mgm.getMaxScorePerPlayer()) {
                                        end = true;
                                    }

                                    mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.captureNeutral", ply.getName()));
                                    flag.stopCarrierParticleEffect();

                                    if (end) {
                                        mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.captureNeutralFinal", ply.getName()));

                                        pdata.endMinigame(ply);
                                        mgm.resetFlags();
                                    }
                                }
                            }
                        } else if (mgm.getFlagCarrier(ply) == null && mgm.hasDroppedFlag(clickID) && !mgm.getDroppedFlag(clickID).isAtHome()) {
                            CTFFlag flag = mgm.getDroppedFlag(sloc);
                            if (mgm.hasDroppedFlag(sloc)) {
                                mgm.removeDroppedFlag(sloc);
                                String newID = MinigameUtils.createLocationID(flag.getSpawnLocation());
                                mgm.addDroppedFlag(newID, flag);
                            }
                            flag.respawnFlag();
                            mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.returned", ply.getName(),
                                    ply.getTeam().getChatColor() + ply.getTeam().getDisplayName() + ChatColor.WHITE));
                        } else if (mgm.getFlagCarrier(ply) != null && mgm.hasDroppedFlag(clickID) && !mgm.getDroppedFlag(clickID).isAtHome()) {
                            ply.sendMessage(MinigameUtils.getLang("player.ctf.returnFail"), MinigameMessageType.LOSS);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void dropFlag(PlayerDeathEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
        if (ply == null) return;
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

                        if (team != null)
                            mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.dropped", ply.getName(),
                                    team.getChatColor() + team.getDisplayName() + ChatColor.WHITE));
                        else
                            mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.droppedNeutral", ply.getName()));
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
    public void playerAutoBalance(PlayerDeathEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
        if (ply == null) return;
        if (ply.isInMinigame() && ply.getMinigame().getType() == MinigameType.MULTIPLAYER && ply.getMinigame().isTeamGame()) {
            Minigame mgm = ply.getMinigame();
            if (mgm.getMechanicName().equals("ctf")) {
                autoBalanceonDeath(ply, mgm);
            }
        }
    }
}
