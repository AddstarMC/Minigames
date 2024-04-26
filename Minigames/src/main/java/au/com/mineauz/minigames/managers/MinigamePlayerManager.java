package au.com.mineauz.minigames.managers;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.MultiplayerBets;
import au.com.mineauz.minigames.events.*;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.GameMechanicBase;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.ResourcePack;
import au.com.mineauz.minigames.recorder.RegenRecorder;
import au.com.mineauz.minigames.sounds.MGSounds;
import au.com.mineauz.minigames.sounds.PlayMGSound;
import au.com.mineauz.minigames.stats.DynamicMinigameStat;
import au.com.mineauz.minigames.stats.MinigameStats;
import au.com.mineauz.minigames.stats.StoredGameStats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manager Class of all players playing Minigames.
 **/
public class MinigamePlayerManager {
    private static final @NotNull Minigames plugin = Minigames.getPlugin();
    private final @NotNull Map<@NotNull UUID, @NotNull MinigamePlayer> minigamePlayers = new HashMap<>();
    private final @NotNull List<@NotNull MinigamePlayer> applyingPack = new ArrayList<>();
    private final @NotNull MinigameManager mgManager = plugin.getMinigameManager();
    private boolean partyMode = false;
    private @NotNull List<@NotNull String> deniedCommands = new ArrayList<>();

    public MinigamePlayerManager() {
    }

    public @NotNull List<@NotNull MinigamePlayer> getApplyingPack() {
        return applyingPack;
    }

    public void needsResourcePack(@NotNull MinigamePlayer player) {
        applyingPack.add(player);
    }

    public void joinMinigame(@NotNull Minigame minigame, @NotNull MinigamePlayer player, boolean isBetting, double betAmount) {
        MinigameType type = minigame.getType();
        JoinMinigameEvent event = new JoinMinigameEvent(player, minigame);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            Minigames.log.info("Start Event was cancelled..: " + event);
            return;
        }
        if (!mgManager.minigameStartStateCheck(minigame, player)) return;
        //Do betting stuff
        if (isBetting && !handleMoneyBet(minigame, player, betAmount)) {
            return;
        }
        //Try teleport the player to their designated area.
        ResourcePack pack = getResourcePack(minigame);
        if (pack != null && pack.isValid()) {
            if (player.applyResourcePack(pack)) {
                player.sendInfoMessage(MinigameUtils.getLang("minigame.resourcepack.apply"));
            }
        }
        //Check if Minigame is full
        if (minigame.isGameFull()) {
            player.sendMessage(MinigameUtils.getLang("minigame.full"), MinigameMessageType.ERROR);
            return;
        }
        //Check if Minigame has a lobby and teleport them there
        if (!mgManager.teleportPlayerOnJoin(minigame, player)) {
            player.sendMessage(MinigameUtils.getLang("minigame.error.noLobby"), MinigameMessageType.ERROR);
            return;
        }
        //Give them the game type name
        if (minigame.getGameTypeName() == null) {
            player.sendMessage(MessageManager.getMinigamesMessage("player.join.plyInfo", minigame.getType().getName()), MinigameMessageType.WIN);
        } else {
            player.sendMessage(MessageManager.getMinigamesMessage("player.join.plyInfo", minigame.getGameTypeName()), MinigameMessageType.WIN);
        }

        //Give them the objective
        if (minigame.getObjective() != null) {
            player.sendUnprefixedMessage(Component.text("----------------------------------------------------", NamedTextColor.GREEN));
            player.sendInfoMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + MessageManager.getMinigamesMessage("player.join.objective",
                    ChatColor.RESET.toString() + ChatColor.WHITE + minigame.getObjective()));
            player.sendUnprefixedMessage(Component.text("----------------------------------------------------", NamedTextColor.GREEN));
        }
        //Prepare regeneration region for rollback.
        mgManager.addRegenDataToRecorder(minigame);
        //Standardize player
        player.storePlayerData();
        player.setMinigame(minigame);
        minigame.addPlayer(player);
        WeatherTimeModule mod = WeatherTimeModule.getMinigameModule(minigame);
        if (mod != null) {
            mod.applyCustomTime(player);
            mod.applyCustomWeather(player);
        }
        player.setCheckpoint(player.getPlayer().getLocation());
        player.getPlayer().setFallDistance(0);
        player.getPlayer().setWalkSpeed(0.2f);
        player.setStartTime(Calendar.getInstance().getTimeInMillis());
        player.setGamemode(minigame.getDefaultGamemode());
        player.getPlayer().setAllowFlight(false);
        for (PotionEffect potion : player.getPlayer().getActivePotionEffects()) {
            player.getPlayer().removePotionEffect(potion.getType());
        }
        //Hide Spectators
        for (MinigamePlayer pl : minigame.getSpectators()) {
            player.getPlayer().hidePlayer(plugin, pl.getPlayer());
        }

        if (minigame.getPlayers().size() == 1) {
            //Register regen recorder events
            if (minigame.hasRegenArea())
                Bukkit.getServer().getPluginManager().registerEvents(new RegenRecorder(minigame), plugin);
            if (mod != null) mod.startTimeLoop();
        }
        //Call Type specific join
        mgManager.minigameType(type).joinMinigame(player, minigame);

        //Call Mechanic specific join
        minigame.getMechanic().onJoinMinigame(minigame, player);

        //Send other players the join message.
        mgManager.sendMinigameMessage(minigame, MessageManager.getMinigamesMessage("player.join.plyMsg", player.getName(), minigame.getName(true)), null, player);
        player.updateInventory();

        if (minigame.canDisplayScoreboard()) {
            player.getPlayer().setScoreboard(minigame.getScoreboardManager());
            minigame.setScore(player, 1);
            minigame.setScore(player, 0);
        }
        if (minigame.getState() == MinigameState.STARTING && minigame.canLateJoin()) {
            player.sendInfoMessage(MessageManager.getMinigamesMessage("minigame.lateJoinWait", minigame.getMpTimer().getStartWaitTimeLeft()));
        }
    }

    /**
     * @param minigame  the minigame to bet on
     * @param player    the player who was betting
     * @param betAmount the amount in economy money. might be 0, if the player was betting an item
     * @return true if the player could successfully bet
     */
    private boolean handleMoneyBet(@NotNull Minigame minigame, @NotNull MinigamePlayer player, double betAmount) {
        if (minigame.getMpBets() == null && (player.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR || betAmount != 0)) {
            minigame.setMpBets(new MultiplayerBets());
        }

        MultiplayerBets mpBets = minigame.getMpBets();
        ItemStack item = player.getPlayer().getInventory().getItemInMainHand().clone();

        if (mpBets != null) {
            if (!mpBets.hasAlreadyBet(player)) {
                //has the player not already bet and are they the highest better?
                if (mpBets.isHighestBetter(betAmount, item)) {
                    if (betAmount >= 0) {
                        if (plugin.getEconomy().getBalance(player.getPlayer().getPlayer()) >= betAmount) {
                            player.sendInfoMessage(Component.text(MinigameUtils.getLang("player.bet.plyMsg")));

                            mpBets.addBet(player, betAmount);
                            plugin.getEconomy().withdrawPlayer(player.getPlayer().getPlayer(), betAmount);

                            return true;
                        } else {
                            //not enough money
                            player.sendMessage(Component.text(MinigameUtils.getLang("player.bet.notEnoughMoney")), MinigameMessageType.ERROR);
                            player.sendMessage(Component.text(MessageManager.getMinigamesMessage("player.bet.notEnoughMoneyInfo", Minigames.getPlugin().getEconomy().format(minigame.getMpBets().getHighestMoneyBet()))), MinigameMessageType.ERROR);
                        }
                    }

                    if (item.getType() != Material.AIR) {
                        player.sendInfoMessage(Component.text(MinigameUtils.getLang("player.bet.plyMsg")));
                        player.getPlayer().getInventory().remove(item);

                        mpBets.addBet(player, item);

                        return true;
                    } else {
                        //no item to bet, and betAmount == 0
                        player.sendMessage(Component.text(MinigameUtils.getLang("player.bet.plyNoBet")), MinigameMessageType.ERROR);
                        return false; //maybe? or better true in this case?
                    }
                } else {
                    if (mpBets.getHighestMoneyBet() > 0) {
                        player.sendMessage(Component.text(
                                MessageManager.getMinigamesMessage("player.bet.incorrectMoneyAmountInfo",
                                        Minigames.getPlugin().getEconomy().format(mpBets.getHighestMoneyBet()))
                        ), MinigameMessageType.ERROR);
                    }
                    //todo connect both messages with an "or"
                    if (mpBets.getHighestItemBet().getType() != Material.AIR) {
                        player.sendMessage(Component.text(
                                MessageManager.getMinigamesMessage("player.bet.incorrectItemAmountInfo",
                                        mpBets.getHighestItemBet().getAmount(),
                                        mpBets.getHighestItemBet().getType().name()) //todo use translation
                        ), MinigameMessageType.ERROR);
                    }
                    return false;
                }
            } else { //todo figure out why
                //already bet once.
                //todo feedback
                return false;
            }
        } else {
            // no bets where made already, no amount and no item in hand
            //todo feedback
            return false;
        }
    }

    public void spectateMinigame(@NotNull Minigame minigame, @NotNull MinigamePlayer player) {
        SpectateMinigameEvent event = new SpectateMinigameEvent(player, minigame);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            boolean tpd;
            if (minigame.getSpectatorLocation() != null)
                tpd = player.teleport(minigame.getSpectatorLocation());
            else {
                player.sendMessage(MinigameUtils.getLang("minigame.error.noSpectatePos"), MinigameMessageType.ERROR);
                return;
            }
            if (!tpd) {
                player.sendMessage(MinigameUtils.getLang("minigame.error.noTeleport"), MinigameMessageType.ERROR);
                return;
            }
            player.storePlayerData();
            player.setMinigame(minigame);
            player.setGamemode(GameMode.ADVENTURE);

            minigame.addSpectator(player);

            if (minigame.canSpectateFly()) {
                player.getPlayer().setAllowFlight(true);
            }
            for (MinigamePlayer pl : minigame.getPlayers()) {
                pl.getPlayer().hidePlayer(plugin, player.getPlayer());
            }

            if (minigame.canDisplayScoreboard()) {
                player.getPlayer().setScoreboard(minigame.getScoreboardManager());
            }

            for (PotionEffect potion : player.getPlayer().getActivePotionEffects()) {
                player.getPlayer().removePotionEffect(potion.getType());
            }
            player.sendInfoMessage(MessageManager.getMinigamesMessage("player.spectate.join.plyMsg", minigame.getName(true)) + "\n" +
                    MessageManager.getMinigamesMessage("player.spectate.join.plyHelp", "\"/minigame quit\""));
            mgManager.sendMinigameMessage(minigame, MessageManager.getMinigamesMessage("player.spectate.join.minigameMsg", player.getName(), minigame.getName(true)), null, player);
        }
    }

    public void startMPMinigame(@NotNull Minigame minigame) {
        startMPMinigame(minigame, LobbySettingsModule.getMinigameModule(minigame).isTeleportOnStart());
    }

    public void startMPMinigame(@NotNull Minigame minigame, boolean teleport) {
        List<MinigamePlayer> players = new ArrayList<>(minigame.getPlayers());
        for (MinigamePlayer ply : players) {
            if (minigame.getMaxScore() != 0)
                ply.sendInfoMessage(MessageManager.getMinigamesMessage("minigame.scoreToWin", minigame.getMaxScorePerPlayer()));
            if (minigame.isAllowedFlight()) ply.setCanFly(true);
            if (minigame.isFlightEnabled() && ply.canFly()) ply.getPlayer().setFlying(true);
            ply.getLoadout().equiptLoadout(ply);

            if (!minigame.isTeamGame()) {
                if (minigame.getLives() > 0) {
                    ply.sendInfoMessage(MessageManager.getMinigamesMessage("minigame.livesLeft", minigame.getLives()));
                }
                ply.setStartTime(Calendar.getInstance().getTimeInMillis());
                if (!minigame.isPlayersAtStart()) {
                    if (teleport) {
                        teleportToStart(minigame);
                    }
                }
            } else {
                List<MinigamePlayer> moved = balanceGame(minigame);
                if (moved != null && moved.size() > 0) {
                    getStartLocations(minigame.getPlayers(), minigame);
                }
                if (!minigame.isPlayersAtStart()) {
                    if (teleport) {
                        teleportToStart(minigame);
                    }
                }

                PlayMGSound.playSound(ply, MGSounds.getSound("gameStart"));
            }
        }


        Bukkit.getServer().getPluginManager().callEvent(new StartMinigameEvent(players, minigame, teleport));
        minigame.setState(MinigameState.STARTED);
    }

    public List<MinigamePlayer> balanceGame(@NotNull Minigame game) {
        List<MinigamePlayer> result = null;
        if (game.isTeamGame()) {
            GameMechanicBase mech = GameMechanics.getGameMechanic(game.getMechanicName());
            if (mech != null) {
                List<MinigamePlayer> players = new ArrayList<>(game.getPlayers());
                result = mech.balanceTeam(players, game);
            }
        }
        return result;
    }

    public void teleportToStart(@NotNull Minigame minigame) {
        List<MinigamePlayer> findStart = new ArrayList<>();
        for (MinigamePlayer ply : minigame.getPlayers()) {
            if (ply.getStartPos() == null) {
                findStart.add(ply);
            }
        }
        if (!findStart.isEmpty()) {
            getStartLocations(findStart, minigame);
        }

        for (MinigamePlayer ply : minigame.getPlayers()) {
            ply.teleport(ply.getStartPos());
        }
        minigame.setPlayersAtStart(true);
    }

    public ResourcePack getResourcePack(@NotNull Minigame game) {
        ResourcePackModule module = (ResourcePackModule) game.getModule("ResourcePack");
        if (module != null && module.isEnabled()) {
            ResourcePack pack = plugin.getResourceManager().getResourcePack(module.getResourcePackName());
            if (pack != null && pack.isValid()) {
                return pack;
            } else {
                return null;
            }
        }
        return null;
    }

    public void clearResourcePack(@NotNull Minigame game) {
        ResourcePack pack = plugin.getResourceManager().getResourcePack("empty");
        if (pack != null && pack.isValid()) {
            for (MinigamePlayer player : game.getPlayers()) {
                player.applyResourcePack(pack);
            }
        }

    }

    public void getStartLocations(@NotNull List<@NotNull MinigamePlayer> players, @NotNull Minigame game) {
        mgManager.sendMinigameMessage(game, MinigameUtils.getLang("minigame.startRandomized"), MinigameMessageType.INFO, (List<MinigamePlayer>) null);
        Collections.shuffle(players);
        int pos = 0;
        Map<Team, Integer> tpos = new HashMap<>();
        for (Team t : TeamsModule.getMinigameModule(game).getTeams()) {
            tpos.put(t, 0);
        }
        if (game.isRandomizeStart()) {
            if (game.isTeamGame()) {
                MinigameUtils.debugMessage("Setting Starts for Team game");
                TeamsModule mod = TeamsModule.getMinigameModule(game);
                if (mod.hasTeamStartLocations()) {
                    for (Team team : mod.getTeams()) {
                        MinigameUtils.debugMessage("Team" + team.getDisplayName() + " is randomized");
                        Collections.shuffle(team.getStartLocations());
                    }
                } else {
                    MinigameUtils.debugMessage("Team game using global starts randomized");
                    Collections.shuffle(game.getStartLocations());
                }
            } else {
                MinigameUtils.debugMessage("Setting Starts for MP game randomized");
                Collections.shuffle(game.getStartLocations());
            }
        } else {
            if (game.isTeamGame()) {
                MinigameUtils.debugMessage("Setting Starts for Team game");
            } else {
                MinigameUtils.debugMessage("MP game using global starts");
            }
        }
        for (MinigamePlayer player : players) {
            Location result = null;
            if (!game.isTeamGame()) {
                if (pos < game.getStartLocations().size()) {
                    player.setStartTime(Calendar.getInstance().getTimeInMillis());
                    result = game.getStartLocations().get(pos);
                } else {
                    MinigameUtils.debugMessage("StartLocations filled - recycling from start");
                    if (!game.getStartLocations().isEmpty()) {
                        pos = 0;
                        result = game.getStartLocations().get(pos);
                    }
                }
            } else {
                Team team = player.getTeam();
                if (team != null) {
                    if (TeamsModule.getMinigameModule(game).hasTeamStartLocations()) {
                        if (tpos.get(team) >= team.getStartLocations().size()) {
                            MinigameUtils.debugMessage("Team Starts for " + team.getDisplayName() + " filled - recylcing from start");
                            tpos.put(team, 0);
                        }
                        result = team.getStartLocations().get(tpos.get(team));
                        tpos.put(team, tpos.get(team) + 1);
                    } else {
                        if (pos < game.getStartLocations().size()) {
                            result = game.getStartLocations().get(pos);
                        } else {
                            MinigameUtils.debugMessage("StartLocations filled - recycling from start");
                            pos = 0;
                            if (!game.getStartLocations().isEmpty()) {
                                result = game.getStartLocations().get(pos);
                            }
                        }
                    }
                } else {
                    player.sendMessage(MinigameUtils.getLang("minigame.error.noTeam"), MinigameMessageType.ERROR);
                }
            }

            if (result == null) {
                player.sendMessage(MinigameUtils.getLang("minigame.error.incorrectStart"), MinigameMessageType.ERROR);
                quitMinigame(player, false);
            } else {
                player.setStartPos(result);
                player.setCheckpoint(result);
                pos++;
            }
        }
    }

    public void revertToCheckpoint(@NotNull MinigamePlayer player) {
        RevertCheckpointEvent event = new RevertCheckpointEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            player.teleport(player.getCheckpoint());
            player.addRevert();
            player.sendInfoMessage(MinigameUtils.getLang("player.checkpoint.revert"));

            // Reset the player's health and extinguish flames when they revert
            Player p = player.getPlayer();
            if ((p != null) && (p.isOnline())) {
                p.setFireTicks(0);
                AttributeInstance maxHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (maxHealth != null) {
                    p.setHealth(maxHealth.getValue());
                }
                p.setFoodLevel(20);
                p.setSaturation(20f);
                p.setRemainingAir(p.getMaximumAir());
            }
        }
    }

    public void quitMinigame(@NotNull MinigamePlayer player, boolean forced) {
        Minigame minigame = player.getMinigame();

        boolean isWinner = GameOverModule.getMinigameModule(minigame).getWinners().contains(player);

        QuitMinigameEvent event = new QuitMinigameEvent(player, minigame, forced, isWinner);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            if (minigame.isSpectator(player)) {
                if (player.getPlayer().getVehicle() != null) {
                    Vehicle vehicle = (Vehicle) player.getPlayer().getVehicle();
                    vehicle.eject();
                }
                player.getPlayer().setFallDistance(0);
                player.getPlayer().setNoDamageTicks(60);
                final Player fplayer = player.getPlayer();
                for (PotionEffect potion : player.getPlayer().getActivePotionEffects()) {
                    player.getPlayer().removePotionEffect(potion.getType());
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> fplayer.setFireTicks(0));

                player.getPlayer().closeInventory();
                if (player.isLiving()) {
                    player.restorePlayerData();
                }

                Location loc;
                if (minigame.getEndLocation() != null) {
                    loc = minigame.getEndLocation();
                } else {
                    loc = minigame.getQuitLocation();
                }

                if (loc != null) {
                    player.teleport(loc);
                } else {
                    Minigames.log.warning("Minigame " + minigame.getName(true) + " has no end location set! (Player: " + player.getName() + ")");
                }

                player.setStartPos(null);
                player.removeMinigame();
                minigame.removeSpectator(player);

                for (MinigamePlayer pl : minigame.getPlayers()) {
                    pl.getPlayer().showPlayer(plugin, player.getPlayer());
                }

                player.sendMessage(MessageManager.getMinigamesMessage("player.spectate.quit.plyMsg", minigame.getName(true)), MinigameMessageType.ERROR);
                mgManager.sendMinigameMessage(minigame, MessageManager.getMinigamesMessage("player.spectate.quit.minigameMsg", player.getName(), minigame.getName(true)), MinigameMessageType.ERROR, player);
            } else {
                if (player.getEndTime() == 0) {
                    player.setEndTime(System.currentTimeMillis());
                }

                if (isWinner) {
                    GameOverModule.getMinigameModule(minigame).getWinners().remove(player);

                    if (minigame.getShowCompletionTime()) {
                        player.setCompleteTime(player.getEndTime() - player.getStartTime() + player.getStoredTime());
                    }

                } else {
                    GameOverModule.getMinigameModule(minigame).getLosers().remove(player);
                }

                if (!isWinner) {
                    if (!minigame.canSaveCheckpoint() && minigame.isEnabled()) {
                        StoredGameStats saveData = new StoredGameStats(minigame, player);
                        saveData.addStat(MinigameStats.Attempts, 1);

                        for (DynamicMinigameStat stat : MinigameStats.getDynamicStats()) {
                            if (stat.doesApply(minigame, player, false)) {
                                saveData.addStat(stat, stat.getValue(minigame, player, false));
                            }
                        }

                        saveData.applySettings(minigame.getStatSettings(saveData));

                        plugin.queueStatSave(saveData, false);
                    }
                }

                //Call Types quit.
                mgManager.minigameType(minigame.getType()).quitMinigame(player, minigame, forced);

                //Call Mechanic quit.
                minigame.getMechanic().quitMinigame(minigame, player, forced);

                //Prepare player for quit
                if (player.getPlayer().getVehicle() != null) {
                    Vehicle vehicle = (Vehicle) player.getPlayer().getVehicle();
                    vehicle.eject();
                }
                player.getPlayer().closeInventory();
                if (player.getLoadout() != null) {
                    player.getLoadout().removeLoadout(player);
                }
                player.removeMinigame();
                minigame.removePlayer(player);
                for (PotionEffect potion : player.getPlayer().getActivePotionEffects()) {
                    player.getPlayer().removePotionEffect(potion.getType());
                }

                player.getPlayer().setFallDistance(0);
                player.getPlayer().setNoDamageTicks(60);
                final MinigamePlayer fplayer = player;
                Bukkit.getScheduler().runTaskLater(plugin, () -> fplayer.getPlayer().setFireTicks(0), 0L);
                player.resetAllStats();
                player.setStartPos(null);
                if (player.isLiving()) {
                    player.restorePlayerData();
                    Location loc;
                    if (!isWinner) {
                        if (minigame.getQuitLocation() != null) {
                            loc = minigame.getQuitLocation();
                        } else {
                            loc = minigame.getEndLocation();
                        }
                    } else {
                        if (minigame.getEndLocation() != null) {
                            loc = minigame.getEndLocation();
                        } else {
                            loc = minigame.getQuitLocation();
                        }
                    }
                    if (loc != null) {
                        player.teleport(loc);
                    } else {
                        Minigames.log.warning("Minigame " + minigame.getName(true) + " has no end location set! (Player: " + player.getName() + ")");
                    }
                } else {
                    if (!isWinner) {
                        player.setQuitPos(minigame.getQuitLocation());
                    } else {
                        player.setQuitPos(minigame.getEndLocation());
                    }
                    player.setRequiredQuit(true);
                }
                player.setStartPos(null);

                //Reward Player
                if (isWinner) {
                    player.claimTempRewardItems();
                }
                player.claimRewards();

                //Reset Minigame
                if (minigame.getPlayers().isEmpty()) {
                    //call event about this minigame has come to an end (and therefor is past an optional end phase)
                    Bukkit.getServer().getPluginManager().callEvent(new EndedMinigameEvent(minigame));

                    if (minigame.getMinigameTimer() != null) {
                        minigame.getMinigameTimer().stopTimer();
                        minigame.setMinigameTimer(null);
                    }

                    if (minigame.getFloorDegenerator() != null) {
                        minigame.getFloorDegenerator().stopDegenerator();
                    }

                    minigame.setState(MinigameState.IDLE);
                    minigame.setPlayersAtStart(false);

                    if (minigame.getRecorderData().hasData()) {
                        minigame.getRecorderData().restoreBlocks();
                        minigame.getRecorderData().restoreEntities();
                        minigame.getRecorderData().setCreatedRegenBlocks(false);
                    }

                    if (minigame.getMpTimer() != null) {
                        minigame.getMpTimer().pauseTimer();
                        minigame.getMpTimer().removeTimer();
                        minigame.setMpTimer(null);
                    }

                    if (minigame.getMpBets() != null) {
                        minigame.setMpBets(null);
                    }

                    mgManager.clearClaimedScore(minigame);

                    WeatherTimeModule mod = WeatherTimeModule.getMinigameModule(minigame);
                    if (mod != null) {
                        mod.stopTimeLoop();
                    }

                    GameOverModule.getMinigameModule(minigame).stopEndGameTimer();

                    for (Team team : TeamsModule.getMinigameModule(minigame).getTeams()) {
                        team.setScore(0);
                    }
                }

                minigame.getScoreboardManager().resetScores(player.getName());

                for (MinigamePlayer pl : minigame.getSpectators()) {
                    player.getPlayer().showPlayer(plugin, pl.getPlayer());
                }

                if (minigame.getPlayers().isEmpty() && !minigame.isRegenerating()) {
                    HandlerList.unregisterAll(minigame.getRecorderData());
                }

                //Send out messages
                if (!forced) {
                    mgManager.sendMinigameMessage(minigame, MessageManager.getMinigamesMessage("player.quit.plyMsg", player.getName(), minigame.getName(true)), MinigameMessageType.ERROR, player);
                }
                plugin.getLogger().info(player.getName() + " quit " + minigame);
                player.updateInventory();
            }
            if (ResourcePackModule.getMinigameModule(minigame).isEnabled()) {
                if (player.applyResourcePack(plugin.getResourceManager().getResourcePack("empty"))) {
                    Minigames.log().warning("Could not apply empty resource pack to " + player.getDisplayName());
                } else {
                    player.sendInfoMessage(MinigameUtils.getLang("minigames.resourcepack.remove"));
                }
            }
            if (player.getPlayer().getGameMode() != GameMode.CREATIVE)
                player.setCanFly(false);

            if (!forced) {
                minigame.getScoreboardData().reload();
            }
        }
    }

    public void endMinigame(@NotNull MinigamePlayer player) {
        List<MinigamePlayer> w = new ArrayList<>();
        List<MinigamePlayer> l = new ArrayList<>();
        w.add(player);
        endMinigame(player.getMinigame(), w, l);
    }

    public void endMinigame(@NotNull Minigame minigame, List<@NotNull MinigamePlayer> winners, List<@NotNull MinigamePlayer> losers) {
        //When the minigame ends, the flag for recognizing the start teleportation needs to be resetted
        minigame.setPlayersAtStart(false);
        EndPhaseMinigameEvent event = new EndPhaseMinigameEvent(winners, losers, minigame);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            winners = event.getWinners();
            losers = event.getLosers();
            //Call Mechanics End
            minigame.getMechanic().endMinigame(minigame, winners, losers);

            //Prepare split bet rewards
            double bets = 0;
            HashSet<ItemStack> betItems = new HashSet<>();
            if (minigame.getMpBets() != null && !winners.isEmpty()) {
                if (minigame.getMpBets().hasMoneyBets()) {
                    bets = Math.round(minigame.getMpBets().claimMoneyBets() / (double) winners.size());
                }

                //todo this  multiplies items, if the rest is over 0.5 and deletes items, if the rest is under it, but not 0
                // for items that are in the division rest me might want to give them to random winners.
                if (minigame.getMpBets().hasItemBets()) {
                    betItems = minigame.getMpBets().claimItemBets();

                    final List<MinigamePlayer> finalWinners = winners;
                    betItems.forEach(item -> item.setAmount((int) Math.round(item.getAmount() / (double) finalWinners.size())));
                }

                minigame.setMpBets(null);
            }

            //Broadcast Message
            broadcastEndGame(winners, minigame);

            GameOverModule gom = GameOverModule.getMinigameModule(minigame);
            boolean usedTimer = false;

            gom.setWinners(winners);
            gom.setLosers(losers);

            if (gom.getTimer() > 0 && minigame.getType() == MinigameType.MULTIPLAYER) {
                gom.startEndGameTimer();
                usedTimer = true;
            }

            for (MinigamePlayer player : losers) {
                player.setEndTime(System.currentTimeMillis());
                if (!usedTimer)
                    quitMinigame(player, true);
                PlayMGSound.playSound(player, MGSounds.getSound("lose"));
            }

            if (minigame.getEndLocation() == null) {
                plugin.getLogger().warning(MessageManager.getUnformattedMessage(null, "minigame.error.noEnd")  + " - " + minigame.getName(false));
            }
            for (MinigamePlayer player : winners) {
                player.setEndTime(System.currentTimeMillis());

                StoredGameStats saveData = new StoredGameStats(minigame, player);
                saveData.addStat(MinigameStats.Attempts, 1);
                saveData.addStat(MinigameStats.Wins, 1);

                saveData.addStat(MinigameStats.Kills, player.getKills());
                saveData.addStat(MinigameStats.Deaths, player.getDeaths());
                saveData.addStat(MinigameStats.Score, player.getScore());
                saveData.addStat(MinigameStats.Reverts, player.getReverts());
                saveData.addStat(MinigameStats.CompletionTime, player.getEndTime() - player.getStartTime() + player.getStoredTime());

                if (minigame.getShowCompletionTime()) {
                    player.sendInfoMessage("Completion time: " + (double) (winners.get(0).getEndTime() - winners.get(0).getStartTime() + winners.get(0).getStoredTime()) / 1000 + " seconds");
                }

                for (DynamicMinigameStat stat : MinigameStats.getDynamicStats()) {
                    if (stat.doesApply(minigame, player, true)) {
                        saveData.addStat(stat, stat.getValue(minigame, player, true));
                    }
                }

                saveData.applySettings(minigame.getStatSettings(saveData));

                if (!usedTimer)
                    quitMinigame(player, true);

                //Group money bets
                if (bets != 0) {
                    plugin.getEconomy().depositPlayer(player.getPlayer().getPlayer(), bets);
                    player.sendInfoMessage(MessageManager.getMinigamesMessage("player.bet.winMoney", Minigames.getPlugin().getEconomy().format(bets)));
                }

                // Record player completion and give rewards
                if (minigame.isEnabled()) {
                    plugin.queueStatSave(saveData, true);
                } else {
                    MinigameUtils.debugMessage("Skipping SQL data save for " + saveData + "; minigame is disabled");
                }

                //Item Bets (for non groups)
                if (minigame.getMpBets() != null) {
                    if (minigame.getMpBets().hasItemBets()) {
                        if (player.isInMinigame()) {
                            for (ItemStack i : betItems) {
                                player.addTempRewardItem(i);
                            }
                        } else {
                            for (ItemStack i : betItems) {
                                for (ItemStack notAdded : player.getPlayer().getInventory().addItem(i).values()) {
                                    //drop items the player had no room for
                                    player.getPlayer().getWorld().dropItemNaturally(player.getLocation(), notAdded);
                                }

                            }
                        }
                        minigame.setMpBets(null);
                    }
                }

                PlayMGSound.playSound(player, MGSounds.getSound("win"));
            }

            if (!usedTimer) {
                gom.clearLosers();
                gom.clearWinners();
            }

            mgManager.clearClaimedScore(minigame);

            //Call Types End.
            mgManager.minigameType(minigame.getType()).endMinigame(winners, losers, minigame);
            minigame.getScoreboardData().reload();
        }
    }

    public void broadcastEndGame(@NotNull List<@NotNull MinigamePlayer> winners, @NotNull Minigame minigame) {
        if (plugin.getConfig().getBoolean("broadcastCompletion") && minigame.isEnabled()) {
            if (minigame.isTeamGame()) {
                if (!winners.isEmpty() || ((TeamsModule) minigame.getModule("Teams")).getDefaultWinner() != null) {
                    Team team;
                    if (!winners.isEmpty())
                        team = winners.get(0).getTeam();
                    else
                        team = ((TeamsModule) minigame.getModule("Teams")).getTeam(((TeamsModule) minigame.getModule("Teams")).getDefaultWinner());
                    StringBuilder score = new StringBuilder();
                    List<Team> teams = TeamsModule.getMinigameModule(minigame).getTeams();
                    for (Team t : teams) {
                        score.append(t.getColor().getColor().toString()).append(t.getScore());
                        if (t != teams.get(teams.size() - 1)) {
                            score.append(ChatColor.WHITE).append(" : ");
                        }
                    }
                    String nscore = ", " + MessageManager.getMinigamesMessage("player.end.team.score", score.toString());
                    if (team.getScore() > 0) {
                        MinigameUtils.broadcast(MessageManager.getMinigamesMessage("player.end.team.win",
                                team.getChatColor() + team.getDisplayName() + ChatColor.WHITE, minigame.getName(true)) + nscore, minigame, ChatColor.GREEN);
                    } else {
                        MinigameUtils.broadcast(MessageManager.getMinigamesMessage("player.end.team.win",
                                team.getChatColor() + team.getDisplayName() + ChatColor.WHITE, minigame.getName(true)), minigame, ChatColor.GREEN);
                    }
                } else {
                    MinigameUtils.broadcast(MessageManager.getMinigamesMessage("player.end.broadcastNobodyWon", minigame.getName(true)), minigame, ChatColor.RED);
                }
            } else {
                if (winners.size() == 1) {
                    String score = "";
                    MinigamePlayer winner = winners.get(0);
                    if (winner.getScore() != 0) {
                        score = MessageManager.getMinigamesMessage("player.end.broadcastScore", winner.getScore());
                    }
                    MinigameUtils.broadcast(MessageManager.getMinigamesMessage("player.end.broadcastMsg", winner.getDisplayName(minigame.usePlayerDisplayNames()), minigame.getName(true)) + ". " + score, minigame, ChatColor.GREEN);
                } else if (winners.size() > 1) {
                    StringBuilder win = new StringBuilder();
                    winners.sort(Comparator.comparingInt(MinigamePlayer::getScore));

                    for (MinigamePlayer pl : winners) {
                        if (winners.indexOf(pl) < 2) {
                            win.append(pl.getDisplayName(minigame.usePlayerDisplayNames()));
                            if (winners.indexOf(pl) + 2 >= winners.size()) {
                                win.append(" and ");
                            } else {
                                win.append(", ");
                            }
                        } else {
                            win.append((winners.size() - 3)).append(" others");
                        }
                    }
                    MinigameUtils.broadcast(MessageManager.getMinigamesMessage("player.end.broadcastMsg", win.toString(), minigame.getName(true)) + ". ", minigame, ChatColor.GREEN);
                } else {
                    MinigameUtils.broadcast(MessageManager.getMinigamesMessage("player.end.broadcastNobodyWon", minigame.getName(true)), minigame, ChatColor.RED);
                }
            }
        }
    }

    @Deprecated
    public @NotNull List<@NotNull Player> playersInMinigame() {
        List<Player> players = new ArrayList<>();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (hasMinigamePlayer(player.getUniqueId())) {
                players.add(player);
            }
        }
        return players;
    }

    public void addMinigamePlayer(@NotNull Player player) {
        minigamePlayers.put(player.getUniqueId(), new MinigamePlayer(player));
    }

    public void removeMinigamePlayer(@NotNull Player player) {
        minigamePlayers.remove(player.getUniqueId());
    }

    /**
     * @return null, if the given player was null, the respecting MinigamePlayer object otherwise
     */
    @Contract("null -> null; !null -> !null")
    public @Nullable MinigamePlayer getMinigamePlayer(@Nullable Player player) {
        if (player == null) {
            return null;
        }

        if (!minigamePlayers.containsKey(player.getUniqueId())) {
            addMinigamePlayer(player);
        }

        return minigamePlayers.get(player.getUniqueId());
    }

    public @Nullable MinigamePlayer getMinigamePlayer(@NotNull UUID uuid) {
        return minigamePlayers.get(uuid);
    }

    /**
     * @see #getMinigamePlayer(UUID)
     */
    public @Nullable MinigamePlayer getMinigamePlayer(@NotNull String playerName) {
        return minigamePlayers.values().stream().filter(mgPlayer -> mgPlayer.getPlayer().getName().equals(playerName)).findAny().orElse(null);
    }

    public @NotNull Collection<@NotNull MinigamePlayer> getAllMinigamePlayers() {
        return minigamePlayers.values();
    }

    /**
     * @see #hasMinigamePlayer(UUID)
     */
    public boolean hasMinigamePlayer(@NotNull String name) {
        return minigamePlayers.values().stream().anyMatch(mgPlayer -> mgPlayer.getPlayer().getName().equals(name));
    }

    public boolean hasMinigamePlayer(UUID uuid) {
        return minigamePlayers.containsKey(uuid);
    }

    public List<String> checkRequiredFlags(@NotNull MinigamePlayer player, @NotNull String minigame) {
        List<String> checkpoints = new ArrayList<>(mgManager.getMinigame(minigame).getFlags());
        List<String> pchecks = player.getFlags();

        if (!pchecks.isEmpty()) {
            checkpoints.removeAll(pchecks);
        }

        return checkpoints;
    }

    public boolean onPartyMode() {
        return partyMode;
    }

    public void setPartyMode(boolean mode) {
        partyMode = mode;
    }

    public void partyMode(@NotNull MinigamePlayer player) {
        if (onPartyMode()) {
            Location loc = player.getPlayer().getLocation();
            Firework firework = (Firework) player.getPlayer().getWorld().spawnEntity(loc, EntityType.FIREWORK);
            FireworkMeta fwm = firework.getFireworkMeta();

            Random chance = new Random();
            Type type = Type.BALL_LARGE;
            if (chance.nextInt(100) < 50) {
                type = Type.BALL;
            }

            Color col = Color.fromRGB(chance.nextInt(255), chance.nextInt(255), chance.nextInt(255));

            FireworkEffect effect = FireworkEffect.builder().with(type).withColor(col).flicker(chance.nextBoolean()).trail(chance.nextBoolean()).build();
            fwm.addEffect(effect);
            fwm.setPower(1);
            firework.setFireworkMeta(fwm);
        }
    }

    public void partyMode(final @NotNull MinigamePlayer player, final int amount, final long delay) {
        if (!onPartyMode()) {
            return;
        }
        partyMode(player);
        if (amount == 1) return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> partyMode(player, amount - 1, delay), delay);
    }

    public @NotNull List<@NotNull String> getDeniedCommands() {
        return deniedCommands;
    }

    public void setDeniedCommands(@NotNull List<@NotNull String> deniedCommands) {
        this.deniedCommands = deniedCommands;
    }

    public void addDeniedCommand(@NotNull String command) {
        deniedCommands.add(command);
    }

    public void removeDeniedCommand(@NotNull String command) {
        deniedCommands.remove(command);
    }

    public void saveDeniedCommands() {
        plugin.getConfig().set("disabledCommands", deniedCommands);
        plugin.saveConfig();
    }

    public void loadDeniedCommands() {
        setDeniedCommands(plugin.getConfig().getStringList("disabledCommands"));
    }
}
