package au.com.mineauz.minigames;

import au.com.mineauz.minigames.events.*;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.GameMechanicBase;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.GameOverModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.minigame.modules.WeatherTimeModule;
import au.com.mineauz.minigames.sounds.MGSounds;
import au.com.mineauz.minigames.sounds.PlayMGSound;
import au.com.mineauz.minigames.stats.DynamicMinigameStat;
import au.com.mineauz.minigames.stats.MinigameStats;
import au.com.mineauz.minigames.stats.StoredGameStats;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;

import java.math.BigDecimal;
import java.util.*;

public class PlayerData {
	private Map<String, MinigamePlayer> minigamePlayers = new HashMap<String, MinigamePlayer>();
	
	private boolean partyMode = false;
	Map<Team, Integer> tpos;
	private int pos;
	private List<String> deniedCommands = new ArrayList<String>();
	
	private static Minigames plugin = Minigames.plugin;
	private MinigameData mdata = plugin.mdata;
	
	public PlayerData(){}
	
	public void joinMinigame(MinigamePlayer player, Minigame minigame, boolean isBetting, Double betAmount){
		MinigameType type = minigame.getType();
		JoinMinigameEvent event = new JoinMinigameEvent(player, minigame);
		Bukkit.getServer().getPluginManager().callEvent(event);
		boolean canStart = minigame.getMechanic().checkCanStart(minigame, player);

		if(!event.isCancelled()){
            if(!minigame.isEnabled() && !player.getPlayer().hasPermission("minigame.join.disabled")){
                player.sendMessage(MinigameUtils.getLang("minigame.error.notEnabled"), "error");
                return;
            }
            else if(!canStart){
                player.sendMessage(MinigameUtils.getLang("minigame.error.mechanicStartFail"), "error");
                return;
            }
            else if(minigame.getState() == MinigameState.REGENERATING){
                player.sendMessage(MinigameUtils.getLang("minigame.error.regenerating"), "error");
                return;
            }
            else if(minigame.getState() == MinigameState.STARTED && !minigame.canLateJoin()){
                player.sendMessage(MinigameUtils.getLang("minigame.started"), "error");
                return;
            }
            else if(minigame.getEndPosition() == null){
                player.sendMessage(MinigameUtils.getLang("minigame.error.noEnd"), "error");
                return;
            }
            else if(minigame.getQuitPosition() == null){
                player.sendMessage(MinigameUtils.getLang("minigame.error.noQuit"), "error");
                return;
            }
            else if(mdata.minigameType(minigame.getType()).cannotStart(minigame,player)){ //type specific reasons we cannot start.
				player.sendMessage(MinigameUtils.getLang("minigame.error.noQuit"), "error");
				return;
            }
            else if(!minigame.getMechanic().validTypes().contains(minigame.getType())){
                player.sendMessage(MinigameUtils.getLang("minigame.error.invalidType"), "error");
                return;
            } else if(minigame.getStartLocations().size() <= 0 ||
                    (minigame.isTeamGame() && !TeamsModule.getMinigameModule(minigame).hasTeamStartLocations())){
                player.sendMessage(MinigameUtils.getLang("minigame.error.noStart"), "error");
                return;
            }
			if((minigame.getState() == MinigameState.IDLE ||
					minigame.getState() == MinigameState.OCCUPIED ||
					minigame.getState() == MinigameState.WAITING ||
					(minigame.getState() == MinigameState.STARTED || minigame.getState() == MinigameState.STARTING && minigame.canLateJoin())
					) && minigame.getMechanic().validTypes().contains(minigame.getType())){
				//Do betting stuff
				if(isBetting)handleBets(minigame,player,betAmount);
				//Try teleport the player to their designated area.
                boolean success = mdata.minigameType(minigame.getType()).teleportOnJoin(player,minigame);
                if(!success){
                    player.sendMessage(MinigameUtils.getLang("minigame.error.noTeleport"), "error");
                    return;
                }
				//Give them the game type name
				if(minigame.getGametypeName() == null)
					player.sendMessage(MinigameUtils.formStr("player.join.plyInfo", minigame.getType().getName()), "win");
				else
					player.sendMessage(MinigameUtils.formStr("player.join.plyInfo", minigame.getGametypeName()), "win");
				
				//Give them the objective
				if(minigame.getObjective() != null){
					player.sendMessage(ChatColor.GREEN + "----------------------------------------------------");
					player.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + MinigameUtils.formStr("player.join.objective", 
							ChatColor.RESET.toString() + ChatColor.WHITE + minigame.getObjective()));
					player.sendMessage(ChatColor.GREEN  + "----------------------------------------------------");
				}
				
				//Prepare regeneration region for rollback.
				if(minigame.getBlockRecorder().hasRegenArea() && !minigame.getBlockRecorder().hasCreatedRegenBlocks()){
                    mdata.addBlockRecorderData(minigame);
                }
				
				//Standardize player
				player.storePlayerData();
				player.setMinigame(minigame);
				minigame.addPlayer(player);
				WeatherTimeModule.getMinigameModule(minigame).applyCustomTime(player);
				WeatherTimeModule.getMinigameModule(minigame).applyCustomWeather(player);
				player.setCheckpoint(player.getPlayer().getLocation());
				player.getPlayer().setFallDistance(0);
				player.getPlayer().setWalkSpeed(0.2f);
				player.setStartTime(Calendar.getInstance().getTimeInMillis());
				player.setGamemode(minigame.getDefaultGamemode());
                player.getPlayer().setAllowFlight(false);

				for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
					player.getPlayer().removePotionEffect(potion.getType());
				}
				
				//Hide Spectators
				for(MinigamePlayer pl : minigame.getSpectators()){
					player.getPlayer().hidePlayer(pl.getPlayer());
				}
				
				if(minigame.getPlayers().size() == 1){
					//Register regen recorder events
					if(minigame.getBlockRecorder().hasRegenArea())
						Bukkit.getServer().getPluginManager().registerEvents(minigame.getBlockRecorder(), plugin);
					WeatherTimeModule.getMinigameModule(minigame).startTimeLoop();
				}
				
				//Call Type specific join
				mdata.minigameType(type).joinMinigame(player, minigame);
				
				//Call Mechanic specific join
				minigame.getMechanic().joinMinigame(minigame, player);

				//Send other players the join message.
				mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.join.plyMsg", player.getName(), minigame.getName(true)), null, player);
				player.updateInventory();
				
				if(minigame.canDisplayScoreboard()){
					player.getPlayer().setScoreboard(minigame.getScoreboardManager());
					minigame.setScore(player, 1);
					minigame.setScore(player, 0);
				}
				if(minigame.getState() == MinigameState.STARTING && minigame.canLateJoin()){
					player.sendMessage(MinigameUtils.formStr("minigame.lateJoinWait", minigame.getMpTimer().getStartWaitTimeLeft()), null);
				}
			}else{
				player.sendMessage(MinigameUtils.formStr("minigame.error.unspecified"," pdata.join failed"), "error");
			}

		}else{
			Minigames.log.info("Start Event was cancelled..: "+ event.toString());
		}
	}

	private void handleBets(Minigame minigame, MinigamePlayer player, Double betAmount){

        if(minigame.getMpBets() == null && (player.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR || betAmount != 0)){
            minigame.setMpBets(new MultiplayerBets());
        }
        MultiplayerBets pbet = minigame.getMpBets();
        ItemStack item = player.getPlayer().getInventory().getItemInMainHand().clone();
        if(pbet != null &&
                ((betAmount != 0 && pbet.canBet(player, betAmount) && plugin.getEconomy().getBalance(player.getPlayer().getPlayer()) >= betAmount) ||
                        (pbet.canBet(player, item) && item.getType() != Material.AIR && pbet.betValue(item.getType()) > 0))){
            player.sendMessage(MinigameUtils.getLang("player.bet.plyMsg"), null);
            if(betAmount == 0){
                pbet.addBet(player, item);
            }
            else{
                pbet.addBet(player, betAmount);
                plugin.getEconomy().withdrawPlayer(player.getPlayer().getPlayer(), betAmount);
            }
            player.getPlayer().getInventory().removeItem(new ItemStack(item.getType(), 1));
        }
        else if(item.getType() == Material.AIR && betAmount == 0){
            player.sendMessage(MinigameUtils.getLang("player.bet.plyNoBet"), "error");
            return;
        }
        else if(betAmount != 0 && !pbet.canBet(player, betAmount)){
            player.sendMessage(MinigameUtils.getLang("player.bet.incorrectAmount"), "error");
            player.sendMessage(MinigameUtils.formStr("player.bet.incorrectAmountInfo", Minigames.plugin.getEconomy().format(minigame.getMpBets().getHighestMoneyBet())), "error");
            return;
        }
        else if(betAmount != 0 && plugin.getEconomy().getBalance(player.getPlayer().getPlayer()) < betAmount){
            player.sendMessage(MinigameUtils.getLang("player.bet.notEnoughMoney"), "error");
            player.sendMessage(MinigameUtils.formStr("player.bet.notEnoughMoneyInfo", Minigames.plugin.getEconomy().format(minigame.getMpBets().getHighestMoneyBet())), "error");
            return;
        }
        else{
            player.sendMessage(MinigameUtils.getLang("player.bet.incorrectItem"), "error");
            player.sendMessage(MinigameUtils.formStr("player.bet.incorrectItemInfo", 1, minigame.getMpBets().highestBetName()), "error");
            return;
        }
    }
	
	public void spectateMinigame(MinigamePlayer player, Minigame minigame) {
		SpectateMinigameEvent event = new SpectateMinigameEvent(player, minigame);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			boolean tpd = false;
			if(minigame.getSpectatorLocation() != null)
				tpd = player.teleport(minigame.getSpectatorLocation());
			else{
				player.sendMessage(MinigameUtils.getLang("minigame.error.noSpectatePos"), "error");
				return;
			}
			if(!tpd){
				player.sendMessage(MinigameUtils.getLang("minigame.error.noTeleport"), "error");
				return;
			}
			player.storePlayerData();
			player.setMinigame(minigame);
			player.setGamemode(GameMode.ADVENTURE);
			
			minigame.addSpectator(player);
			
			if(minigame.canSpectateFly()){
				player.getPlayer().setAllowFlight(true);
			}
			for(MinigamePlayer pl : minigame.getPlayers()){
				pl.getPlayer().hidePlayer(player.getPlayer());
			}
			
			player.getPlayer().setScoreboard(minigame.getScoreboardManager());
			
			for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
				player.getPlayer().removePotionEffect(potion.getType());
			}
			player.sendMessage(MinigameUtils.formStr("player.spectate.join.plyMsg", minigame.getName(true)) + "\n" +
					MinigameUtils.formStr("player.spectate.join.plyHelp", "\"/minigame quit\""), null);
			mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.spectate.join.minigameMsg", player.getName(), minigame.getName(true)), null, player);
		}
	}
	public void startMPMinigame(Minigame minigame){
	    startMPMinigame(minigame, false);
    }

	
	public void startMPMinigame(Minigame minigame, boolean teleport){
		List<MinigamePlayer> players = new ArrayList<>();
		players.addAll(minigame.getPlayers());
		for(MinigamePlayer ply : players){
			if (minigame.getMaxScore() != 0)
				ply.sendMessage(MinigameUtils.formStr("minigame.scoreToWin", minigame.getMaxScorePerPlayer()), null);
			if (minigame.isAllowedFlight()) ply.setCanFly(true);
			if (minigame.isFlightEnabled() && ply.canFly()) ply.getPlayer().setFlying(true);
			ply.getLoadout().equiptLoadout(ply);

			if(!minigame.isTeamGame()){
				if(minigame.getLives() > 0){
					ply.sendMessage(MinigameUtils.formStr("minigame.livesLeft", minigame.getLives()), null);
				}
				ply.setStartTime(Calendar.getInstance().getTimeInMillis());
			}else {
				List<MinigamePlayer> moved = balanceGame(minigame);
				if (moved != null && moved.size() > 0) {
					getStartLocations(minigame.getPlayers(),minigame);
				}
			}

			PlayMGSound.playSound(ply, MGSounds.getSound("gameStart"));
		}


        Bukkit.getServer().getPluginManager().callEvent(new StartMinigameEvent(players, minigame, teleport));
		minigame.setState(MinigameState.STARTED);
	}

	List<MinigamePlayer> balanceGame(Minigame game) {
		List<MinigamePlayer> result = null;
		if (game.isTeamGame()) {
			GameMechanicBase mech = GameMechanics.getGameMechanic(game.getMechanicName());
			if (mech != null) {
				List<MinigamePlayer> players = new ArrayList<>();
				players.addAll(game.getPlayers());
				result = mech.balanceTeam(players, game);
			}
		}
		return result;
	}


    void teleportToStart(Minigame minigame) {
        List<MinigamePlayer> findStart = new ArrayList<>();
        for (MinigamePlayer ply : minigame.getPlayers()) {
            if(ply.getStartPos() == null){
                findStart.add(ply);
            }
        }
        if(findStart.size() != 0) {
                getStartLocations(findStart,minigame);
        }

        for (MinigamePlayer ply : minigame.getPlayers()) {
            ply.teleport(ply.getStartPos());
        }
    }

	void getStartLocations(List<MinigamePlayer> players, Minigame game) {
        Collections.shuffle(players);
        int pos = 0;
        Map<Team, Integer> tpos = new HashMap<>();
        for (Team t : TeamsModule.getMinigameModule(game).getTeams()) {
            tpos.put(t, 0);
        }
        for(MinigamePlayer player: players) {
            Location result = null;
            if (!game.isTeamGame()) {
                if (pos < game.getStartLocations().size()) {
                    player.setStartTime(Calendar.getInstance().getTimeInMillis());
                    result = game.getStartLocations().get(pos);
                    pos++;
                } else {
                    pos = 0;
                    if (!game.getStartLocations().isEmpty()) {
                        result = game.getStartLocations().get(0);
                    }
                }
            } else {
                Team team = player.getTeam();
                if (team != null) {
                    if (TeamsModule.getMinigameModule(game).hasTeamStartLocations()) {
                        if (tpos.get(team) >= team.getStartLocations().size()) {
                            tpos.put(team, 0);
                        }
                        result = team.getStartLocations().get(tpos.get(team));
                        tpos.put(team, tpos.get(team) + 1);
                    } else {
                        if (pos < game.getStartLocations().size()) {
                            result = game.getStartLocations().get(pos);
                            pos++;
                        } else {
                            pos = 0;
                            if (!game.getStartLocations().isEmpty()) {
                                result = game.getStartLocations().get(0);
                            }
                        }
                    }
                } else {
                    player.sendMessage(MinigameUtils.getLang("minigame.error.noTeam"), "error");
                }
            }
            player.setStartPos(result);
            player.setCheckpoint(result);
            if (result == null) {
                player.sendMessage(MinigameUtils.getLang("minigame.error.incorrectStart"), "error");
                quitMinigame(player, false);
            }
        }
	}
	
	public void revertToCheckpoint(MinigamePlayer player) {
		
		RevertCheckpointEvent event = new RevertCheckpointEvent(player);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			player.teleport(player.getCheckpoint());
			player.addRevert();
			player.sendMessage(MinigameUtils.getLang("player.checkpoint.revert"), null);

			// Reset the player's health and extinguish flames when they revert
			Player p = player.getPlayer();
			if ((p != null) && (p.isOnline())) {
				p.setFireTicks(0);
                p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                p.setFoodLevel(20);
				p.setSaturation(20f);
				p.setRemainingAir(p.getMaximumAir());
			}
		}
	}
	
	public void quitMinigame(MinigamePlayer player, boolean forced){
		Minigame minigame = player.getMinigame();
		
		boolean isWinner = false;
		if(GameOverModule.getMinigameModule(minigame).getWinners().contains(player))
			isWinner = true;
		
		QuitMinigameEvent event = new QuitMinigameEvent(player, minigame, forced, isWinner);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if(!event.isCancelled()){
			if(!minigame.isSpectator(player)){
				if(player.getEndTime() == 0)
					player.setEndTime(System.currentTimeMillis());
				
				if(isWinner)
					GameOverModule.getMinigameModule(minigame).getWinners().remove(player);
				else
					GameOverModule.getMinigameModule(minigame).getLosers().remove(player);
				
				if(!isWinner){
					if(!minigame.canSaveCheckpoint() && minigame.isEnabled()){
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
				mdata.minigameType(minigame.getType()).quitMinigame(player, minigame, forced);
				
				//Call Mechanic quit.
				minigame.getMechanic().quitMinigame(minigame, player, forced);
				
				//Prepare player for quit
				if(player.getPlayer().getVehicle() != null){
					Vehicle vehicle = (Vehicle) player.getPlayer().getVehicle();
					vehicle.eject();
				}
				player.getPlayer().closeInventory();
				if (player.getLoadout() != null) {
					player.getLoadout().removeLoadout(player);
				}
				player.removeMinigame();
				minigame.removePlayer(player);
				for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
					player.getPlayer().removePotionEffect(potion.getType());
				}
				
				player.getPlayer().setFallDistance(0);
				player.getPlayer().setNoDamageTicks(60);
				final MinigamePlayer fplayer = player;
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						fplayer.getPlayer().setFireTicks(0);
					}
				});
				player.resetAllStats();
				player.setStartPos(null);
				if(!player.isDead()){
					player.restorePlayerData();
					if(!isWinner)
						player.teleport(minigame.getQuitPosition());
					else
						player.teleport(minigame.getEndPosition());
				}
				else{
					if(!isWinner)
						player.setQuitPos(minigame.getQuitPosition());
					else
						player.setQuitPos(minigame.getEndPosition());
					player.setRequiredQuit(true);
				}
				player.setStartPos(null);
				//Reward Player
				if(isWinner){
					player.claimTempRewardItems();
				}
				player.claimRewards();
				
				//Reset Minigame
				if(minigame.getPlayers().size() == 0){
					if(minigame.getMinigameTimer() != null){
						minigame.getMinigameTimer().stopTimer();
						minigame.setMinigameTimer(null);
					}
					
					if(minigame.getFloorDegenerator() != null){
						minigame.getFloorDegenerator().stopDegenerator();
					}
					
					minigame.setState(MinigameState.IDLE);
					
					if(minigame.getBlockRecorder().hasData()){
						minigame.getBlockRecorder().restoreBlocks();
						minigame.getBlockRecorder().restoreEntities();
						minigame.getBlockRecorder().setCreatedRegenBlocks(false);
					}
					
					if(minigame.getMpTimer() != null){
						minigame.getMpTimer().pauseTimer();
						minigame.getMpTimer().removeTimer();
						minigame.setMpTimer(null);
					}
					
					if(minigame.getMpBets() != null){
						minigame.setMpBets(null);
					}
					
					mdata.clearClaimedScore(minigame);
					WeatherTimeModule.getMinigameModule(minigame).stopTimeLoop();
					GameOverModule.getMinigameModule(minigame).stopEndGameTimer();
					for(Team team : TeamsModule.getMinigameModule(minigame).getTeams()){
						team.setScore(0);
					}
				}
				
				minigame.getScoreboardManager().resetScores(player.getName());
				
				for(MinigamePlayer pl : minigame.getSpectators()){
					player.getPlayer().showPlayer(pl.getPlayer());
				}
				
				if(minigame.getPlayers().size() == 0 && !minigame.isRegenerating()){
					HandlerList.unregisterAll(minigame.getBlockRecorder());
				}
				
				//Send out messages
				if(!forced){
					mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.quit.plyMsg", player.getName(), minigame.getName(true)), "error", player);
				}
				plugin.getLogger().info(player.getName() + " quit " + minigame);
				player.updateInventory();
			}
			else{
				if(player.getPlayer().getVehicle() != null){
					Vehicle vehicle = (Vehicle) player.getPlayer().getVehicle();
					vehicle.eject();
				}
				player.getPlayer().setFallDistance(0);
				player.getPlayer().setNoDamageTicks(60);
				final Player fplayer = player.getPlayer();
				for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
					player.getPlayer().removePotionEffect(potion.getType());
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						fplayer.setFireTicks(0);
					}
				});
				
				player.getPlayer().closeInventory();
				if(!player.isDead()){
					player.restorePlayerData();
				}
				player.teleport(minigame.getQuitPosition());
				player.setStartPos(null);
				player.removeMinigame();
				minigame.removeSpectator(player);

				for(MinigamePlayer pl : minigame.getPlayers()){
					pl.getPlayer().showPlayer(player.getPlayer());
				}
				
				player.sendMessage(MinigameUtils.formStr("player.spectate.quit.plyMsg", minigame.getName(true)), "error");
				mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.spectate.quit.minigameMsg", player.getName(), minigame.getName(true)), "error", player);
			}
			
			if(player.getPlayer().getGameMode() != GameMode.CREATIVE)
				player.setCanFly(false);
			
			if (!forced) {
				minigame.getScoreboardData().reload();
			}
		}
	}
	
	public void endMinigame(MinigamePlayer player){
		List<MinigamePlayer> w = new ArrayList<MinigamePlayer>();
		List<MinigamePlayer> l = new ArrayList<MinigamePlayer>();
		w.add(player);
		endMinigame(player.getMinigame(), w, l);
	}
	
	
	public void endMinigame(Minigame minigame, List<MinigamePlayer> winners, List<MinigamePlayer> losers){
		EndMinigameEvent event = new EndMinigameEvent(winners, losers, minigame);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
                winners = event.getWinners();
                losers = event.getLosers();
                //Call Mechanics End
                minigame.getMechanic().endMinigame(minigame, winners, losers);

                //Prepare split money
                double bets = 0;
                if (minigame.getMpBets() != null) {
                    if (minigame.getMpBets().hasMoneyBets()) {
                        List<MinigamePlayer> plys = new ArrayList<MinigamePlayer>();
                        plys.addAll(event.getWinners());

                        if (!plys.isEmpty()) {
                            bets = minigame.getMpBets().claimMoneyBets() / (double) plys.size();
                            BigDecimal roundBets = new BigDecimal(bets);
                            roundBets = roundBets.setScale(2, BigDecimal.ROUND_HALF_UP);
                            bets = roundBets.doubleValue();
                        }
                        minigame.setMpBets(null);
                    }
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
                        player.sendMessage(MinigameUtils.formStr("player.bet.winMoney", Minigames.plugin.getEconomy().format(bets)), null);
                    }

                    // Record player completion and give rewards
                    if (minigame.isEnabled()) {
                        plugin.queueStatSave(saveData, true);
                    } else {
                        MinigameUtils.debugMessage("Skipping SQL data save for " + saveData + "; minigame is disabled");
                    }

                    //Item Bets (for non groups)
                    if (minigame.getMpBets() != null) {
                        if (minigame.getMpBets().hasBets()) {
                            if (!player.isInMinigame())
                                player.getPlayer().getInventory().addItem(minigame.getMpBets().claimBets());
                            else {
                                for (ItemStack i : minigame.getMpBets().claimBets()) {
                                    player.addTempRewardItem(i);
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

                mdata.clearClaimedScore(minigame);

                //Call Types End.
                mdata.minigameType(minigame.getType()).endMinigame(winners, losers, minigame);
                minigame.getScoreboardData().reload();
			}
	}

	public void broadcastEndGame(List<MinigamePlayer> winners, Minigame minigame) {
		if (plugin.getConfig().getBoolean("broadcastCompletion") && minigame.isEnabled()) {
			if (minigame.isTeamGame()) {
				if (winners.size() > 0 || ((TeamsModule) minigame.getModule("Teams")).getDefaultWinner() != null) {
					Team team;
					if (winners.size() > 0)
						team = winners.get(0).getTeam();
					else
						team = ((TeamsModule) minigame.getModule("Teams")).getTeam(((TeamsModule) minigame.getModule("Teams")).getDefaultWinner());
					String score = "";
					List<Team> teams = TeamsModule.getMinigameModule(minigame).getTeams();
					for (Team t : teams) {
						score += t.getColor().getColor().toString() + t.getScore();
						if (t != teams.get(teams.size() - 1)) {
							score += ChatColor.WHITE + " : ";
						}
					}
					String nscore = ", " + MinigameUtils.formStr("player.end.team.score", score);
					if (team.getScore() > 0) {
						MinigameUtils.broadcast(MinigameUtils.formStr("player.end.team.win",
								team.getChatColor() + team.getDisplayName() + ChatColor.WHITE, minigame.getName(true)) + nscore, minigame, ChatColor.GREEN);
					} else {
						MinigameUtils.broadcast(MinigameUtils.formStr("player.end.team.win",
								team.getChatColor() + team.getDisplayName() + ChatColor.WHITE, minigame.getName(true)), minigame, ChatColor.GREEN);
					}
				} else {
					MinigameUtils.broadcast(MinigameUtils.formStr("player.end.broadcastNobodyWon", minigame.getName(true)), minigame, ChatColor.RED);
				}
			} else {
				if (winners.size() == 1) {
					String score = "";
					if (winners.get(0).getScore() != 0)
						score = MinigameUtils.formStr("player.end.broadcastScore", winners.get(0).getScore());
					MinigameUtils.broadcast(MinigameUtils.formStr("player.end.broadcastMsg", winners.get(0).getDisplayName(minigame.usePlayerDisplayNames()), minigame.getName(true)) + ". " + score, minigame, ChatColor.GREEN);
				} else if (winners.size() > 1) {
					String win = "";
					Collections.sort(winners, new Comparator<MinigamePlayer>() {
						@Override
						public int compare(MinigamePlayer o1,
										   MinigamePlayer o2) {
							return Integer.valueOf(o1.getScore()).compareTo(o2.getScore());
						}
					});

					for (MinigamePlayer pl : winners) {
						if (winners.indexOf(pl) < 2) {
							win += pl.getDisplayName(minigame.usePlayerDisplayNames());
							if (winners.indexOf(pl) + 2 >= winners.size()) {
								win += " and ";
							} else {
								win += ", ";
							}
						} else {
							win += String.valueOf(winners.size() - 3) + " others";
						}
					}
					MinigameUtils.broadcast(MinigameUtils.formStr("player.end.broadcastMsg", win, minigame.getName(true)) + ". ", minigame, ChatColor.GREEN);
				} else {
					MinigameUtils.broadcast(MinigameUtils.formStr("player.end.broadcastNobodyWon", minigame.getName(true)), minigame, ChatColor.RED);
				}
			}
		}
	}

	
	@Deprecated
	public boolean playerInMinigame(Player player){
		return minigamePlayers.get(player.getName()).isInMinigame();
	}
	
	@Deprecated
	public List<Player> playersInMinigame(){
		List<Player> players = new ArrayList<Player>();
		for(Player player : plugin.getServer().getOnlinePlayers()){
			if(hasMinigamePlayer(player.getUniqueId())){
				players.add(player);
			}
		}
		return players;
	}
	
	public void addMinigamePlayer(Player player){
		minigamePlayers.put(player.getName(), new MinigamePlayer(player));
	}
	
	public void removeMinigamePlayer(Player player){
		if(minigamePlayers.containsKey(player.getName())){
			minigamePlayers.remove(player.getName());
		}
	}
	
	public MinigamePlayer getMinigamePlayer(Player player){
		return minigamePlayers.get(player.getName());
	}
	
	public MinigamePlayer getMinigamePlayer(UUID uuid){
		for(MinigamePlayer p : minigamePlayers.values()){
			if(p.getUUID() == uuid)
				return p;
		}
		return null;
	}
	
	public MinigamePlayer getMinigamePlayer(String player){
		return minigamePlayers.get(player);
	}
	
	public Collection<MinigamePlayer> getAllMinigamePlayers(){
		return minigamePlayers.values();
	}
	
	public boolean hasMinigamePlayer(String name){
		return minigamePlayers.containsKey(name);
	}
	
	public boolean hasMinigamePlayer(UUID uuid){
		for(MinigamePlayer p : minigamePlayers.values()){
			if(p.getUUID() == uuid)
				return true;
		}
		return false;
	}
	
	public List<String> checkRequiredFlags(MinigamePlayer player, String minigame){
		List<String> checkpoints = new ArrayList<String>();
		checkpoints.addAll(mdata.getMinigame(minigame).getFlags());
		List<String> pchecks = player.getFlags();
		
		if(!pchecks.isEmpty()){
			checkpoints.removeAll(pchecks);
		}
		
		return checkpoints;
	}
	
	public boolean onPartyMode(){
		return partyMode;
	}
	
	public void setPartyMode(boolean mode){
		partyMode = mode;
	}
	
	public void partyMode(MinigamePlayer player){
		if(onPartyMode()){
			Location loc = player.getPlayer().getLocation();
			Firework firework = (Firework) player.getPlayer().getWorld().spawnEntity(loc, EntityType.FIREWORK);
			FireworkMeta fwm = firework.getFireworkMeta();
			
			Random chance = new Random();
			Type type = Type.BALL_LARGE;
			if(chance.nextInt(100) < 50){
				type = Type.BALL;
			}
			
			Color col = Color.fromRGB(chance.nextInt(255), chance.nextInt(255), chance.nextInt(255));
			
			FireworkEffect effect = FireworkEffect.builder().with(type).withColor(col).flicker(chance.nextBoolean()).trail(chance.nextBoolean()).build();
			fwm.addEffect(effect);
			fwm.setPower(1);
			firework.setFireworkMeta(fwm);
		}
	}
	
	public void partyMode(MinigamePlayer player, int amount, long delay){
		if(!onPartyMode()) return;
		final int fcount = amount;
		final MinigamePlayer fplayer = player;
		final long fdelay = delay;
		partyMode(fplayer);
		if(amount == 1) return;
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				if(fplayer != null){
					partyMode(fplayer, fcount - 1, fdelay);
				}
				
			}
		}, delay);
	}

	public List<String> getDeniedCommands() {
		return deniedCommands;
	}

	public void setDeniedCommands(List<String> deniedCommands) {
		this.deniedCommands = deniedCommands;
	}
	
	public void addDeniedCommand(String command){
		deniedCommands.add(command);
	}
	
	public void removeDeniedCommand(String command){
		deniedCommands.remove(command);
	}
	
	public void saveDeniedCommands(){
		plugin.getConfig().set("disabledCommands", deniedCommands);
		plugin.saveConfig();
	}
	
	public void loadDeniedCommands(){
		setDeniedCommands(plugin.getConfig().getStringList("disabledCommands"));
	}

	private class PlayerLocation {
		Location location;
		Integer position;

		public PlayerLocation(int pos) {
			location = null;
			this.position = pos;
		}
	}

}
