package com.pauldavdesign.mineauz.minigames;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;

import com.pauldavdesign.mineauz.minigames.events.EndMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.EndTeamMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.JoinMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.QuitMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.RevertCheckpointEvent;
import com.pauldavdesign.mineauz.minigames.events.SpectateMinigameEvent;
import com.pauldavdesign.mineauz.minigames.scoring.ScoreTypes;

public class PlayerData {
	private Map<String, MinigamePlayer> minigamePlayers = new HashMap<String, MinigamePlayer>();
	private Map<String, OfflineMinigamePlayer> offlineMinigamePlayers = new HashMap<String, OfflineMinigamePlayer>();
	
	private Map<String, StoredPlayerCheckpoints> storedPlayerCheckpoints = new HashMap<String, StoredPlayerCheckpoints>();
	
	private boolean partyMode = false;
	
	private List<String> deniedCommands = new ArrayList<String>();
	
	private static Minigames plugin = Minigames.plugin;
	private MinigameData mdata = plugin.mdata;
	MinigameSave invsave = new MinigameSave("playerinv");
	
	public PlayerData(){}
	
	public void joinMinigame(MinigamePlayer player, Minigame minigame) {
		String gametype = minigame.getType();
		
		JoinMinigameEvent event = new JoinMinigameEvent(player, minigame);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			if(mdata.getMinigameTypes().contains(gametype)){
				player.setAllowTeleport(true);
				player.getPlayer().setFallDistance(0);
				if(mdata.minigameType(gametype).joinMinigame(player, minigame)){
					plugin.getLogger().info(MinigameUtils.formStr("player.join.consMsg", player.getName(), minigame.getName()));
					mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.join.plyMsg", player.getName(), minigame.getName()), null, player);
					
					player.getPlayer().setGameMode(minigame.getDefaultGamemode());
					player.setAllowGamemodeChange(false);
					player.getPlayer().setAllowFlight(false);
					player.setAllowTeleport(false);

					
					if(hasStoredPlayerCheckpoint(player)){
						if(getPlayersStoredCheckpoints(player).hasCheckpoint(minigame.getName())){
							player.setCheckpoint(getPlayersStoredCheckpoints(player).getCheckpoint(minigame.getName()));
							if(getPlayersStoredCheckpoints(player).hasFlags(minigame.getName())){
								player.setFlags(getPlayersStoredCheckpoints(player).getFlags(minigame.getName()));
							}
							getPlayersStoredCheckpoints(player).removeCheckpoint(minigame.getName());
							getPlayersStoredCheckpoints(player).removeFlags(minigame.getName());
							if(getPlayersStoredCheckpoints(player).hasNoCheckpoints() && !getPlayersStoredCheckpoints(player).hasGlobalCheckpoint()){
								storedPlayerCheckpoints.remove(player.getName());
							}
							revertToCheckpoint(player);
						}
					}
					
					for(MinigamePlayer pl : minigame.getSpectators()){
						player.getPlayer().hidePlayer(pl.getPlayer());
					}

					for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
						player.getPlayer().removePotionEffect(potion.getType());
					}
				}
			}
			else{
				player.sendMessage(MinigameUtils.getLang("minigame.error.noGametype"), "error");
			}
		}
	}
	
	public void spectateMinigame(MinigamePlayer player, Minigame minigame) {
		SpectateMinigameEvent event = new SpectateMinigameEvent(player, minigame);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			player.storePlayerData();
			player.setMinigame(minigame);
			player.getPlayer().setGameMode(GameMode.ADVENTURE);
			
			minigame.addSpectator(player);
			minigameTeleport(player, minigame.getStartLocations().get(0));
			
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
			player.sendMessage(MinigameUtils.formStr("player.spectate.join.plyMsg", minigame.getName()) + "\n" +
					MinigameUtils.formStr("player.spectate.join.plyHelp", "\"/minigame quit\""), null);
			mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.spectate.join.minigameMsg", player.getName(), minigame.getName()), null, player);
		}
	}
	
	public void joinWithBet(MinigamePlayer player, Minigame minigame, Double money){
		
		JoinMinigameEvent event = new JoinMinigameEvent(player, minigame, true);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			if(minigame != null && minigame.isEnabled() && (minigame.getMpTimer() == null || minigame.getMpTimer().getPlayerWaitTimeLeft() != 0)){
				if(minigame.getMpBets() == null && (player.getPlayer().getItemInHand().getType() != Material.AIR || money != 0)){
					minigame.setMpBets(new MultiplayerBets());
				}
				MultiplayerBets pbet = minigame.getMpBets(); 
				ItemStack item = player.getPlayer().getItemInHand().clone();
				if(pbet != null && 
						((money != 0 && pbet.canBet(player, money) && plugin.getEconomy().getBalance(player.getName()) >= money) || 
								(pbet.canBet(player, item) && item.getType() != Material.AIR && pbet.betValue(item.getType()) > 0))){
					if(minigame.getPlayers().isEmpty() || minigame.getPlayers().size() != minigame.getMaxPlayers()){
						player.sendMessage(MinigameUtils.getLang("player.bet.plyMsg"), null);
						if(money == 0){
							pbet.addBet(player, item);
						}
						else{
							pbet.addBet(player, money);
							plugin.getEconomy().withdrawPlayer(player.getName(), money);
						}
						player.getPlayer().getInventory().removeItem(new ItemStack(item.getType(), 1));
						joinMinigame(player, minigame);
					}
					else{
						player.sendMessage(MinigameUtils.getLang("minigame.full"), "error");
					}
				}
				else if(item.getType() == Material.AIR && money == 0){
					player.sendMessage(MinigameUtils.getLang("player.bet.plyNoBet"), "error");
				}
				else if(money != 0 && !pbet.canBet(player, money)){
					player.sendMessage(MinigameUtils.getLang("player.bet.incorrectAmount"), "error");
					player.sendMessage(MinigameUtils.formStr("player.bet.incorrectAmountInfo", minigame.getMpBets().getHighestMoneyBet()), "error");
				}
				else if(money != 0 && plugin.getEconomy().getBalance(player.getName()) < money){
					player.sendMessage(MinigameUtils.getLang("player.bet.notEnoughMoney"), "error");
					player.sendMessage(MinigameUtils.formStr("player.bet.notEnoughMoneyInfo", minigame.getMpBets().getHighestMoneyBet()), "error");
				}
				else{
					player.sendMessage(MinigameUtils.getLang("player.bet.incorrectItem"), "error");
					player.sendMessage(MinigameUtils.formStr("player.bet.incorrectItemInfo", 1, minigame.getMpBets().highestBetName()), "error");
				}
			}
			else if(minigame != null && minigame.getMpTimer() != null && minigame.getMpTimer().getPlayerWaitTimeLeft() == 0){
				player.sendMessage(MinigameUtils.getLang("minigame.started"), "error");
			}
		}
	}
	
	public void startMPMinigame(Minigame minigame){
		List<MinigamePlayer> players = new ArrayList<MinigamePlayer>();
		players.addAll(minigame.getPlayers());
		
		Collections.shuffle(players);
		
		if(minigame.getType().equals("teamdm") && ScoreTypes.getScoreType(minigame.getScoreType()) != null){
			ScoreTypes.getScoreType(minigame.getScoreType()).balanceTeam(players, minigame);
		}
		
		Location start = null;
		int pos = 0;
		int bluepos = 0;
		int redpos = 0;
		
		for(MinigamePlayer ply : players){
			if(!minigame.getType().equals("teamdm")){
				if(pos < minigame.getStartLocations().size()){
					start = minigame.getStartLocations().get(pos);
					minigameTeleport(ply, start);
					ply.setCheckpoint(start);
					if(minigame.getMaxScore() != 0 && minigame.getType().equals("dm") && !minigame.getScoreType().equals("none")){
						ply.sendMessage(MinigameUtils.formStr("minigame.scoreToWin", minigame.getMaxScorePerPlayer(minigame.getPlayers().size())), null);
					}
				} 
				else{
					pos = 0;
					if(!minigame.getStartLocations().isEmpty()){
						start = minigame.getStartLocations().get(0);
						minigameTeleport(ply, start);
						ply.setCheckpoint(start);
						if(minigame.getMaxScore() != 0 && minigame.getType().equals("dm") && !minigame.getScoreType().equals("none")){
							ply.sendMessage(MinigameUtils.formStr("minigame.scoreToWin", minigame.getMaxScorePerPlayer(minigame.getPlayers().size())), null);
						}
					}
					else {
						ply.sendMessage(MinigameUtils.getLang("minigame.error.incorrectStart"), "error");
						quitMinigame(ply, false);
					}
				}
			}
			else{
				int team = -1;
				if(minigame.getBlueTeam().contains(ply.getPlayer())){
					team = 1;
				}
				else if(minigame.getRedTeam().contains(ply.getPlayer())){
					team = 0;
				}
				if(!minigame.getStartLocationsRed().isEmpty() && !minigame.getStartLocationsBlue().isEmpty()){
					if(team == 0 && redpos < minigame.getStartLocationsRed().size()){
						start = minigame.getStartLocationsRed().get(redpos);
						redpos++;
					}
					else if(team == 1 && bluepos < minigame.getStartLocationsBlue().size()){
						start = minigame.getStartLocationsBlue().get(bluepos);
						bluepos++;
					}
					else if(team == 0 && !minigame.getStartLocationsRed().isEmpty()){
						redpos = 0;
						start = minigame.getStartLocationsRed().get(redpos);
						redpos++;
					}
					else if(team == 1 && !minigame.getStartLocationsBlue().isEmpty()){
						bluepos = 0;
						start = minigame.getStartLocationsBlue().get(bluepos);
						bluepos++;
					}
					else if(minigame.getStartLocationsBlue().isEmpty() || minigame.getStartLocationsRed().isEmpty()){
						ply.sendMessage(MinigameUtils.getLang("minigame.error.incorrectStart"), "error");
						quitMinigame(ply, false);
					}
				}
				else{
					if(pos <= minigame.getStartLocations().size()){
						start = minigame.getStartLocations().get(pos);
						minigameTeleport(ply, start);
						ply.setCheckpoint(start);
					} 
					else{
						pos = 1;
						if(!minigame.getStartLocations().isEmpty()){
							start = minigame.getStartLocations().get(0);
							minigameTeleport(ply, start);
							ply.setCheckpoint(start);
						}
						else {
							ply.sendMessage(MinigameUtils.getLang("minigame.error.incorrectStart"), "error");
							quitMinigame(ply, false);
						}
					}
				}
				
				if(start != null){
					minigameTeleport(ply, start);
					ply.setCheckpoint(start);
					if(minigame.getMaxScore() != 0 && !minigame.getScoreType().equals("none")){
						ply.sendMessage(MinigameUtils.formStr("minigame.scoreToWin", minigame.getMaxScorePerPlayer(minigame.getPlayers().size())), null);
					}
				}
				
				if(minigame.getLives() > 0){
					ply.sendMessage(MinigameUtils.formStr("minigame.livesLeft", minigame.getLives()), null);
				}
			}
			pos++;
			if(!minigame.getPlayersLoadout(ply).getItems().isEmpty()){
				minigame.getPlayersLoadout(ply).equiptLoadout(ply);
			}
			ply.getPlayer().setScoreboard(minigame.getScoreboardManager());
			minigame.setScore(ply, 1);
			minigame.setScore(ply, 0);
		}
		
		if(minigame.hasPlayers()){
			if(minigame.getSpleefFloor1() != null && minigame.getSpleefFloor2() != null){
				minigame.addFloorDegenerator();
				minigame.getFloorDegenerator().startDegeneration();
			}
	
			if(minigame.hasRestoreBlocks()){
				for(RestoreBlock block : minigame.getRestoreBlocks().values()){
					minigame.getBlockRecorder().addBlock(block.getLocation().getBlock(), null);
				}
			}
			
			if(minigame.getTimer() > 0){
				minigame.setMinigameTimer(new MinigameTimer(minigame, minigame.getTimer()));
				mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("minigame.timeLeft", MinigameUtils.convertTime(minigame.getTimer())), null, null);
			}
		}
	}
	
	public void revertToCheckpoint(MinigamePlayer player) {
		
		RevertCheckpointEvent event = new RevertCheckpointEvent(player);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			minigameTeleport(player, player.getCheckpoint());
			player.sendMessage(MinigameUtils.getLang("player.checkpoint.revert"), null);
		}
	}
	
	public void quitMinigame(MinigamePlayer player, boolean forced){
		Minigame mgm = player.getMinigame();

		QuitMinigameEvent event = new QuitMinigameEvent(player, mgm, forced);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if(!event.isCancelled()){
			if(!mgm.isSpectator(player)){
				player.setAllowTeleport(true);
				
				if(player.getPlayer().getVehicle() != null){
					Vehicle vehicle = (Vehicle) player.getPlayer().getVehicle();
					vehicle.eject();
				}
				
				player.getPlayer().closeInventory();
				
				if(!forced){
					mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.quit.plyMsg", player.getName(), mgm.getName()), "error", player);
				}
				else{
					mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.quit.plyForcedMsg", player.getName(), mgm.getName()), "error", player);
				}
	
				mgm.removePlayersLoadout(player);

				final MinigamePlayer ply = player;
				if(!player.getPlayer().isDead()){
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						
						@Override
						public void run() {
							if(ply.getPlayer().isOnline() && !ply.getPlayer().isDead()){
								ply.restorePlayerData();
							}
						}
					});
				}

				player.removeMinigame();
				mgm.removePlayer(player);
				mdata.minigameType(mgm.getType()).quitMinigame(player, mgm, forced);
				
				for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
					player.getPlayer().removePotionEffect(potion.getType());
				}
				player.getPlayer().setFallDistance(0);
				
				final MinigamePlayer fplayer = player;
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						fplayer.getPlayer().setFireTicks(0);
						fplayer.getPlayer().setNoDamageTicks(60);
					}
				});
				
				player.clearFlags();
				player.resetDeaths();
				player.resetKills();
				player.resetScore();
				player.removeCheckpoint();
				
				plugin.getLogger().info(player.getName() + " quit " + mgm);
				if(mgm.getPlayers().size() == 0){
					if(mgm.getMinigameTimer() != null){
						mgm.getMinigameTimer().stopTimer();
						mgm.setMinigameTimer(null);
					}
					
					if(mgm.getFloorDegenerator() != null){
						mgm.getFloorDegenerator().stopDegenerator();
					}
					
					if(mgm.getBlockRecorder().hasData()){
						mgm.getBlockRecorder().restoreBlocks();
						mgm.getBlockRecorder().restoreEntities();
					}
					
					if(mgm.getMpBets() != null){
						mgm.setMpBets(null);
					}
				}
				mgm.getScoreboardManager().resetScores(player.getPlayer());
				
				for(MinigamePlayer pl : mgm.getSpectators()){
					player.getPlayer().showPlayer(pl.getPlayer());
				}
				
				ply.setAllowTeleport(true);
				ply.setAllowGamemodeChange(true);
			}
			else{
				if(player.getPlayer().getVehicle() != null){
					Vehicle vehicle = (Vehicle) player.getPlayer().getVehicle();
					vehicle.eject();
				}
				player.getPlayer().setFallDistance(0);
				
				player.getPlayer().closeInventory();
				final Player fplayer = player.getPlayer();
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						fplayer.setFireTicks(0);
						fplayer.setNoDamageTicks(60);
					}
				});
				
				final MinigamePlayer ply = player;
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						if(ply.getPlayer().isOnline()){
							ply.restorePlayerData();
						}
					}
				});
				
				minigameTeleport(player, mgm.getQuitPosition());
				player.removeMinigame();
				mgm.removeSpectator(player);
				
				for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
					player.getPlayer().removePotionEffect(potion.getType());
				}
				
				for(MinigamePlayer pl : mgm.getPlayers()){
					pl.getPlayer().showPlayer(player.getPlayer());
				}
				ply.getPlayer().setFlying(false);
				ply.setAllowTeleport(true);
				ply.setAllowGamemodeChange(true);
				
				player.sendMessage(MinigameUtils.formStr("player.specate.quit.plyMsg", mgm.getName()), "error");
				mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.spectate.quit.minigameMsg", player.getName(), mgm.getName()), "error", player);
			}
		}
	}
	
	public void endMinigame(final MinigamePlayer player){
		Minigame mgm = player.getMinigame();
		
		EndMinigameEvent event = new EndMinigameEvent(player, mgm);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			if(player.getPlayer().getVehicle() != null){
				Vehicle vehicle = (Vehicle) player.getPlayer().getVehicle();
				vehicle.eject();
			}
			
			player.getPlayer().closeInventory();
			if(player.getPlayer().isOnline() && !player.getPlayer().isDead()){
				player.restorePlayerData();
			}
			
			player.removeMinigame();
			mgm.removePlayer(player);
			mgm.removePlayersLoadout(player);
			player.getPlayer().setFallDistance(0);
			mdata.minigameType(mgm.getType()).endMinigame(player, mgm);
			
			for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
				player.getPlayer().removePotionEffect(potion.getType());
			}
			player.getPlayer().setFireTicks(0);
			player.getPlayer().setNoDamageTicks(60);
			
			player.clearFlags();
			
			if(plugin.getSQL() == null || plugin.getSQL().getSql() == null){
				player.resetDeaths();
				player.resetKills();
				player.resetScore();
			}
			
			if(mgm.getMinigameTimer() != null){
				mgm.getMinigameTimer().stopTimer();
				mgm.setMinigameTimer(null);
			}
			
			if(mgm.getFloorDegenerator() != null && mgm.getPlayers().size() == 0){
				mgm.getFloorDegenerator().stopDegenerator();
			}
			
			if(mgm.getMpBets() != null && mgm.getPlayers().size() == 0){
				mgm.setMpBets(null);
			}
			
			if(mgm.getBlockRecorder().hasData()){
				if(!mgm.getType().equalsIgnoreCase("sp") || mgm.getPlayers().isEmpty()){
					mgm.getBlockRecorder().restoreBlocks();
					mgm.getBlockRecorder().restoreEntities();
				}
				else if(mgm.getPlayers().isEmpty()){
					mgm.getBlockRecorder().restoreBlocks(player);
					mgm.getBlockRecorder().restoreEntities(player);
				}
			}
			
			for(MinigamePlayer pl : mgm.getSpectators()){
				player.getPlayer().showPlayer(pl.getPlayer());
			}
			
			player.setAllowTeleport(true);
			player.setAllowGamemodeChange(true);

			plugin.getLogger().info(MinigameUtils.formStr("player.end.consMsg", player.getName(), mgm.getName()));
			mgm.getScoreboardManager().resetScores(player.getPlayer());
		}
	}
	
	public void endTeamMinigame(int teamnum, Minigame mgm){
		
		List<MinigamePlayer> losers = new ArrayList<MinigamePlayer>();
		List<MinigamePlayer> winners = new ArrayList<MinigamePlayer>();
		
		if(teamnum == 1){
			//Blue team
			for(OfflinePlayer ply : mgm.getRedTeam()){
				losers.add(getMinigamePlayer(ply.getName()));
			}
			for(OfflinePlayer ply : mgm.getBlueTeam()){
				winners.add(getMinigamePlayer(ply.getName()));
			}
		}
		else{
			//Red team
			for(OfflinePlayer ply : mgm.getRedTeam()){
				winners.add(getMinigamePlayer(ply.getName()));
			}
			for(OfflinePlayer ply : mgm.getBlueTeam()){
				losers.add(getMinigamePlayer(ply.getName()));
			}
		}
		

		EndTeamMinigameEvent event = new EndTeamMinigameEvent(losers, winners, mgm, teamnum);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			if(event.getWinningTeamInt() == 1){
				if(plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
					String score = "";
					if(mgm.getRedTeamScore() != 0 && mgm.getBlueTeamScore() != 0){
						score = ", " + MinigameUtils.formStr("player.end.team.score", ChatColor.BLUE.toString() + mgm.getBlueTeamScore() + ChatColor.WHITE, ChatColor.RED.toString() + mgm.getRedTeamScore());
					}
					plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + MinigameUtils.formStr("player.end.team.win", ChatColor.BLUE.toString() + "Blue Team" + ChatColor.WHITE, mgm.getName()) + score);
				}
			}
			else{
				if(plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
					String score = "";
					if(mgm.getRedTeamScore() != 0 && mgm.getBlueTeamScore() != 0){
						score = ", " + MinigameUtils.formStr("player.end.team.score", ChatColor.RED.toString() + mgm.getBlueTeamScore() + ChatColor.WHITE, ChatColor.BLUE.toString() + mgm.getRedTeamScore());
					}
					plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + MinigameUtils.formStr("player.end.team.win", ChatColor.RED + "Red Team" + ChatColor.WHITE, mgm.getName()) + score);
				}
			}
			
			mgm.setRedTeamScore(0);
			mgm.setBlueTeamScore(0);
			
			mgm.getMpTimer().setStartWaitTime(0);
			
			List<MinigamePlayer> winplayers = new ArrayList<MinigamePlayer>();
			winplayers.addAll(event.getWinnningPlayers());
	
			if(plugin.getSQL() != null){
				new SQLCompletionSaver(mgm.getName(), winplayers, mdata.minigameType(mgm.getType()));
			}
			
			if(mgm.getMpBets() != null){
				if(mgm.getMpBets().hasMoneyBets()){
					List<MinigamePlayer> plys = new ArrayList<MinigamePlayer>();
					plys.addAll(event.getWinnningPlayers());
					
					if(!plys.isEmpty()){
						double bets = mgm.getMpBets().claimMoneyBets() / (double) plys.size();
						BigDecimal roundBets = new BigDecimal(bets);
						roundBets = roundBets.setScale(2, BigDecimal.ROUND_HALF_UP);
						bets = roundBets.doubleValue();
						for(MinigamePlayer ply : plys){
							plugin.getEconomy().depositPlayer(ply.getName(), bets);
							ply.sendMessage(MinigameUtils.formStr("player.bet.winMoney", bets), null);
						}
					}
				}
				mgm.setMpBets(null);
			}
			
			if(!event.getLosingPlayers().isEmpty()){
				List<MinigamePlayer> loseplayers = new ArrayList<MinigamePlayer>();
				loseplayers.addAll(event.getLosingPlayers());
				for(int i = 0; i < loseplayers.size(); i++){
					if(loseplayers.get(i) instanceof MinigamePlayer){
						final MinigamePlayer p = loseplayers.get(i);
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	
							@Override
							public void run() {
								p.sendMessage(MinigameUtils.getLang("player.quit.plyBeatenMsg"), "error");
								quitMinigame(p, true);
							}
						});
					}
					else{
						loseplayers.remove(i);
					}
				}
				mgm.setMpTimer(null);
				for(MinigamePlayer pl : loseplayers){
					mgm.getPlayers().remove(pl);
				}
			}
			
			for(int i = 0; i < winplayers.size(); i++){
				if(winplayers.get(i) instanceof MinigamePlayer){
					final MinigamePlayer p = winplayers.get(i);
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						
						@Override
						public void run() {
							endMinigame(p);
						}
					});
				}
				else{
					winplayers.remove(i);
				}
			}
			
			mgm.setMpTimer(null);
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
			if(playerInMinigame(player)){
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
	
	public MinigamePlayer getMinigamePlayer(String player){
		return minigamePlayers.get(player);
	}
	
	public Collection<MinigamePlayer> getAllMinigamePlayers(){
		return minigamePlayers.values();
	}
	
	public boolean hasMinigamePlayer(String name){
		return minigamePlayers.containsKey(name);
	}
	
	public void addOfflineMinigamePlayer(MinigamePlayer player){
		Location loc = null;
		if(player.getQuitPos() != null){
			loc = player.getQuitPos();
		}
		else{
			loc = player.getMinigame().getQuitPosition();
		}
		offlineMinigamePlayers.put(player.getName(), new OfflineMinigamePlayer(player.getName(), player.getStoredItems(), player.getStoredArmour(), player.getFood(), player.getHealth(), player.getSaturation(), player.getLastGamemode(), loc));
	}
	
	public void addOfflineMinigamePlayer(OfflineMinigamePlayer player){
		offlineMinigamePlayers.put(player.getPlayer(), player);
	}
	
	public OfflineMinigamePlayer getOfflineMinigamePlayer(String name){
		return offlineMinigamePlayers.get(name);
	}
	
	public boolean hasOfflineMinigamePlayer(String name){
		return offlineMinigamePlayers.containsKey(name);
	}
	
	public void removeOfflineMinigamePlayer(String name){
		offlineMinigamePlayers.remove(name);
	}
	
//	public void storePlayerInventory(String player, ItemStack[] items, ItemStack[] armour, Integer health, Integer food, Float saturation){
//		OfflineMinigamePlayer oply = new OfflineMinigamePlayer(player, items, armour, food, health, saturation, GameMode.SURVIVAL, resPos.get(player));
//		offlineMinigamePlayers.put(player, oply);
//	}
	
	public List<String> checkRequiredFlags(MinigamePlayer player, String minigame){
		List<String> checkpoints = new ArrayList<String>();
		checkpoints.addAll(mdata.getMinigame(minigame).getFlags());
		List<String> pchecks = player.getFlags();
		
		if(!pchecks.isEmpty()){
			checkpoints.removeAll(pchecks);
		}
		
		return checkpoints;
	}
	
	public Configuration getInventorySaveConfig(){
		return invsave.getConfig();
	}
	
	public void saveInventoryConfig(){
		invsave.saveConfig();
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
	
	public boolean hasStoredPlayerCheckpoint(MinigamePlayer player){
		if(storedPlayerCheckpoints.containsKey(player.getName())){
			return true;
		}
		return false;
	}
	
	public StoredPlayerCheckpoints getPlayersStoredCheckpoints(MinigamePlayer player){
		return storedPlayerCheckpoints.get(player.getName());
	}
	
	public void addStoredPlayerCheckpoint(MinigamePlayer player, String minigame, Location checkpoint){
		StoredPlayerCheckpoints spc = new StoredPlayerCheckpoints(player.getName(), minigame, checkpoint);
		storedPlayerCheckpoints.put(player.getName(), spc);
	}
	
	public void addStoredPlayerCheckpoints(String name, StoredPlayerCheckpoints spc){
		storedPlayerCheckpoints.put(name, spc);
	}
	
	public void minigameTeleport(MinigamePlayer player, Location location){
		if(player.isInMinigame()){
			player.setAllowTeleport(true);
			player.getPlayer().teleport(location);
			player.setAllowTeleport(false);
		}
		else{
			player.getPlayer().teleport(location);
		}
	}
}
