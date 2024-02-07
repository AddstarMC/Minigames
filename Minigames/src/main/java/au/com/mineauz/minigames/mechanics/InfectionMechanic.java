package au.com.mineauz.minigames.mechanics;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.InfectionModule;
import au.com.mineauz.minigames.minigame.modules.MgModules;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class InfectionMechanic extends GameMechanicBase {

    @Override
    public String getMechanic() {
        return "infection";
    }

    @Override
    public EnumSet<MinigameType> validTypes() {
        return EnumSet.of(MinigameType.MULTIPLAYER);
    }

    @Override
    public boolean checkCanStart(@NotNull Minigame minigame, @Nullable MinigamePlayer caller) {
        TeamsModule teamsModule = TeamsModule.getMinigameModule(minigame);
        InfectionModule infectionModule = InfectionModule.getMinigameModule(minigame);
        if (!minigame.isTeamGame() ||
                teamsModule.getTeams().size() != 2 ||
                !teamsModule.hasTeam(infectionModule.getInfectedTeam()) ||
                !teamsModule.hasTeam(infectionModule.getSurvivorTeam())) {
            if (caller != null) {
                MinigameMessageManager.sendMgMessage(caller, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOINFECTION);
            } else {
                Minigames.getCmpnntLogger().warn("The Infection Minigame \"" + minigame.getName(false) + "\"is not properly configured! Visit the wiki for help configuring an Infection Minigame.");
            }
            return false;
        }
        return true;
    }

    @Override
    public List<MinigamePlayer> balanceTeam(@NotNull List<MinigamePlayer> mgPlayers, @NotNull Minigame minigame) {
        List<MinigamePlayer> result = new ArrayList<>();
        Collections.shuffle(mgPlayers);
        for (MinigamePlayer mgPlayer : mgPlayers) {
            TeamsModule teamsModule = TeamsModule.getMinigameModule(minigame);
            InfectionModule infectionModule = InfectionModule.getMinigameModule(minigame);
            Team infectedTeam = teamsModule.getTeam(infectionModule.getInfectedTeam());
            Team survivorTeam = teamsModule.getTeam(infectionModule.getSurvivorTeam());
            Team team = mgPlayer.getTeam();
            double percent = ((Integer) infectionModule.getInfectedPercent()).doubleValue() / 100d;
            if (team == survivorTeam) {
                if (infectedTeam.getPlayers().size() < Math.ceil(mgPlayers.size() * percent) && infectedTeam.hasRoom()) {
                    MultiplayerType.switchTeam(minigame, mgPlayer, infectedTeam);
                    result.add(mgPlayer);
                    MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, MiniMessage.miniMessage().deserialize(infectedTeam.getPlayerAssignMessage(),
                            Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(infectedTeam.getDisplayName(), infectedTeam.getTextColor()))));
                    MinigameMessageManager.sendMinigameMessage(minigame, MiniMessage.miniMessage().deserialize(infectedTeam.getJoinAnnounceMessage(),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName()),
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(infectedTeam.getDisplayName(), infectedTeam.getTextColor()))),
                            MinigameMessageType.INFO, mgPlayer);
                }
            } else if (team == null) {
                if (infectedTeam.getPlayers().size() < Math.ceil(mgPlayers.size() * percent) && infectedTeam.hasRoom()) {
                    infectedTeam.addPlayer(mgPlayer);
                    result.add(mgPlayer);
                    MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, MiniMessage.miniMessage().deserialize(infectedTeam.getPlayerAssignMessage(),
                            Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(infectedTeam.getDisplayName(), infectedTeam.getTextColor()))));
                    MinigameMessageManager.sendMinigameMessage(minigame, MiniMessage.miniMessage().deserialize(infectedTeam.getJoinAnnounceMessage(),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName()),
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(infectedTeam.getDisplayName(), infectedTeam.getTextColor()))),
                            MinigameMessageType.INFO, mgPlayer);
                } else if (survivorTeam.hasRoom()) {
                    survivorTeam.addPlayer(mgPlayer);
                    result.add(mgPlayer);
                    MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, MiniMessage.miniMessage().deserialize(survivorTeam.getPlayerAssignMessage(),
                            Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(survivorTeam.getDisplayName(), survivorTeam.getTextColor()))));
                    MinigameMessageManager.sendMinigameMessage(minigame, MiniMessage.miniMessage().deserialize(survivorTeam.getJoinAnnounceMessage(),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName()),
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(survivorTeam.getDisplayName(), survivorTeam.getTextColor()))),
                            MinigameMessageType.INFO, mgPlayer);
                } else {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_FULL);
                    pdata.quitMinigame(mgPlayer, false);
                }
            }
        }
        return result;
    }

    @Override
    public MinigameModule displaySettings(Minigame minigame) {
        return minigame.getModule(MgModules.INFECTION.getName());
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
        InfectionModule infectionModule = InfectionModule.getMinigameModule(minigame);
        if (infectionModule.isInfectedPlayer(player)) {
            infectionModule.removeInfectedPlayer(player);
        }
    }

    @Override
    public void endMinigame(Minigame minigame, List<MinigamePlayer> winners,
                            List<MinigamePlayer> losers) {
        List<MinigamePlayer> wins = new ArrayList<>(winners);
        InfectionModule infectionModule = InfectionModule.getMinigameModule(minigame);
        for (MinigamePlayer mgPlayer : wins) {
            if (infectionModule.isInfectedPlayer(mgPlayer)) {
                winners.remove(mgPlayer);
                losers.add(mgPlayer);
                infectionModule.removeInfectedPlayer(mgPlayer);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playerDeath(@NotNull PlayerDeathEvent event) {
        MinigamePlayer player = pdata.getMinigamePlayer(event.getEntity());
        if (player.isInMinigame()) {
            Minigame mgm = player.getMinigame();
            if (mgm.isTeamGame() && mgm.getMechanicName().equals("infection")) {
                TeamsModule teamsModule = TeamsModule.getMinigameModule(mgm);
                InfectionModule infectionModule = InfectionModule.getMinigameModule(mgm);

                Team survivorTeam = teamsModule.getTeam(infectionModule.getSurvivorTeam());
                Team infectedTeam = teamsModule.getTeam(infectionModule.getInfectedTeam());
                if (survivorTeam.getPlayers().contains(player)) {
                    if (!infectedTeam.hasRoom()) {
                        MultiplayerType.switchTeam(mgm, player, infectedTeam);
                        infectionModule.addInfectedPlayer(player);
                        if (event.getEntity().getKiller() != null) {
                            MinigamePlayer killer = pdata.getMinigamePlayer(event.getEntity().getKiller());
                            killer.addScore();
                            mgm.setScore(killer, killer.getScore());
                        }
                        player.resetScore();
                        mgm.setScore(player, player.getScore());

                        if (mgm.getLives() != player.getDeaths()) {
                            MinigameMessageManager.sendMinigameMessage(mgm, MiniMessage.miniMessage().deserialize(infectedTeam.getJoinAnnounceMessage(),
                                            Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), player.getName()),
                                            Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(infectedTeam.getDisplayName(), infectedTeam.getTextColor()))),
                                    MinigameMessageType.ERROR);
                        }
                        if (survivorTeam.getPlayers().isEmpty()) {
                            List<MinigamePlayer> w;
                            List<MinigamePlayer> l;
                            w = new ArrayList<>(infectedTeam.getPlayers());
                            l = new ArrayList<>();
                            pdata.endMinigame(mgm, w, l);
                        }
                    } else {
                        pdata.quitMinigame(player, false);
                    }
                } else {
                    if (event.getEntity().getKiller() != null) {
                        MinigamePlayer killer = pdata.getMinigamePlayer(event.getEntity().getKiller());
                        killer.addScore();
                        mgm.setScore(killer, killer.getScore());
                    }
                }
            }
        }
    }
}
