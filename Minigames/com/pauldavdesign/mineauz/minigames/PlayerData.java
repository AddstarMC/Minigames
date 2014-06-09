package com.pauldavdesign.mineauz.minigames;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

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
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;

import com.pauldavdesign.mineauz.minigames.blockRecorder.RecorderData;
import com.pauldavdesign.mineauz.minigames.events.EndMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.JoinMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.QuitMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.RevertCheckpointEvent;
import com.pauldavdesign.mineauz.minigames.events.SpectateMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.StartMinigameEvent;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameTypeBase;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.scoring.ScoreType;
import com.pauldavdesign.mineauz.minigames.sql.SQLPlayer;

public class PlayerData {
	private Map<String, MinigamePlayer> minigamePlayers = new HashMap<String, MinigamePlayer>();
	
	private boolean partyMode = false;
	
	private List<String> deniedCommands = new ArrayList<String>();
	
	private static Minigames plugin = Minigames.plugin;
	private MinigameData mdata = plugin.mdata;
	
	public PlayerData(){}
	
	public void joinMinigame(MinigamePlayer player, Minigame minigame, boolean isBetting, Double betAmount){
		MinigameType type = minigame.getType();
		JoinMinigameEvent event = new JoinMinigameEvent(player, minigame);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			if((minigame.isEnabled() || player.getPlayer().hasPermission("minigame.join.disabled")) && 
					!minigame.isRegenerating() && (!minigame.isNotWaitingForPlayers() || (minigame.canLateJoin() && minigame.getMpTimer().getPlayerWaitTimeLeft() == 0)) && 
					(minigame.getStartLocations().size() > 0 || 
							(type == MinigameType.TEAMS && minigame.getStartLocationsBlue().size() > 0 && minigame.getStartLocationsRed().size() > 0)) &&
					minigame.getEndPosition() != null && minigame.getQuitPosition() != null && 
					(minigame.getType() == MinigameType.SINGLEPLAYER || minigame.getLobbyPosition() != null) &&
					((type == MinigameType.SINGLEPLAYER && !minigame.isSpMaxPlayers()) || minigame.getPlayers().size() < minigame.getMaxPlayers())){
				//Do betting stuff
				if(isBetting){
					if(minigame.getMpBets() == null && (player.getPlayer().getItemInHand().getType() != Material.AIR || betAmount != 0)){
						minigame.setMpBets(new MultiplayerBets());
					}
					MultiplayerBets pbet = minigame.getMpBets(); 
					ItemStack item = player.getPlayer().getItemInHand().clone();
					if(pbet != null && 
							((betAmount != 0 && pbet.canBet(player, betAmount) && plugin.getEconomy().getBalance(player.getName()) >= betAmount) || 
									(pbet.canBet(player, item) && item.getType() != Material.AIR && pbet.betValue(item.getType()) > 0))){
						player.sendMessage(MinigameUtils.getLang("player.bet.plyMsg"), null);
						if(betAmount == 0){
							pbet.addBet(player, item);
						}
						else{
							pbet.addBet(player, betAmount);
							plugin.getEconomy().withdrawPlayer(player.getName(), betAmount);
						}
						player.getPlayer().getInventory().removeItem(new ItemStack(item.getType(), 1));
					}
					else if(item.getType() == Material.AIR && betAmount == 0){
						player.sendMessage(MinigameUtils.getLang("player.bet.plyNoBet"), "error");
						return;
					}
					else if(betAmount != 0 && !pbet.canBet(player, betAmount)){
						player.sendMessage(MinigameUtils.getLang("player.bet.incorrectAmount"), "error");
						player.sendMessage(MinigameUtils.formStr("player.bet.incorrectAmountInfo", minigame.getMpBets().getHighestMoneyBet()), "error");
						return;
					}
					else if(betAmount != 0 && plugin.getEconomy().getBalance(player.getName()) < betAmount){
						player.sendMessage(MinigameUtils.getLang("player.bet.notEnoughMoney"), "error");
						player.sendMessage(MinigameUtils.formStr("player.bet.notEnoughMoneyInfo", minigame.getMpBets().getHighestMoneyBet()), "error");
						return;
					}
					else{
						player.sendMessage(MinigameUtils.getLang("player.bet.incorrectItem"), "error");
						player.sendMessage(MinigameUtils.formStr("player.bet.incorrectItemInfo", 1, minigame.getMpBets().highestBetName()), "error");
						return;
					}
				}
				
				//Try teleport the player to their designated area.
				boolean tpd = false;
				if(type == MinigameType.SINGLEPLAYER){
					tpd = player.teleport(minigame.getStartLocations().get(0));//TODO: Make random
					if(plugin.getConfig().getBoolean("warnings") && player.getPlayer().getWorld() != minigame.getStartLocations().get(0).getWorld() && 
							player.getPlayer().hasPermission("minigame.set.start")){
						player.sendMessage(ChatColor.RED + "WARNING: " + ChatColor.WHITE + 
								"Join location is across worlds! This may cause some server performance issues!", "error");
					}
				}
				else{
					tpd = player.teleport(minigame.getLobbyPosition());
					if(plugin.getConfig().getBoolean("warnings") && player.getPlayer().getWorld() != minigame.getLobbyPosition().getWorld() && 
							player.getPlayer().hasPermission("minigame.set.lobby")){
						player.sendMessage(ChatColor.RED + "WARNING: " + ChatColor.WHITE + 
								"Lobby location is across worlds! This may cause some server performance issues!", "error");
					}
				}
				if(!tpd){
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
					player.sendMessage(ChatColor.GREEN + "----------------------------------------------------");
				}
				
				//Prepare regeneration region for rollback.
				if(minigame.getBlockRecorder().hasRegenArea() && !minigame.getBlockRecorder().hasCreatedRegenBlocks()){
					RecorderData d = minigame.getBlockRecorder();
					d.setCreatedRegenBlocks(true);
					
					Location cur = new Location(minigame.getRegenArea1().getWorld(), 0, 0, 0);
					for(double y = d.getRegenMinY(); y <= d.getRegenMaxY(); y++){
						cur.setY(y);
						for(double x = d.getRegenMinX(); x <= d.getRegenMaxX(); x++){
							cur.setX(x);
							for(double z = d.getRegenMinZ(); z <= d.getRegenMaxZ(); z++){
								cur.setZ(z);
								d.addBlock(cur.getBlock(), null);
							}
						}
					}
				}
				else if(minigame.hasRestoreBlocks() && !minigame.getBlockRecorder().hasCreatedRegenBlocks()){
					minigame.getBlockRecorder().setCreatedRegenBlocks(true);
					for(RestoreBlock block : minigame.getRestoreBlocks().values()){
						minigame.getBlockRecorder().addBlock(block.getLocation().getBlock(), null);
					}
				}
				
				//Standardize player
				player.storePlayerData();
				player.setMinigame(minigame);
				minigame.addPlayer(player);
				player.setCheckpoint(player.getPlayer().getLocation());
				player.getPlayer().setFallDistance(0);
				player.getPlayer().setWalkSpeed(0.2f);
				player.getPlayer().setAllowFlight(false);
				player.setStartTime(Calendar.getInstance().getTimeInMillis());
				player.setGamemode(minigame.getDefaultGamemode());
				for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
					player.getPlayer().removePotionEffect(potion.getType());
				}
				
				//Hide Spectators
				for(MinigamePlayer pl : minigame.getSpectators()){
					player.getPlayer().hidePlayer(pl.getPlayer());
				}
				
				//Register regen recorder events
				if(minigame.getPlayers().size() == 1 && minigame.getBlockRecorder().hasRegenArea()){
					Bukkit.getServer().getPluginManager().registerEvents(minigame.getBlockRecorder(), plugin);
				}
				
				//Call Type specific join
				mdata.minigameType(type).joinMinigame(player, minigame);

				//Send other players the join message.
				mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.join.plyMsg", player.getName(), minigame.getName(true)), null, player);
				player.updateInventory();
			}
			else if(!minigame.isEnabled()){
				player.sendMessage(MinigameUtils.getLang("minigame.error.notEnabled"), "error");
			}
			else if(minigame.isRegenerating()){
				player.sendMessage(MinigameUtils.getLang("minigame.error.regenerating"), "error");
			}
			else if(minigame.isNotWaitingForPlayers() && !minigame.canLateJoin()){
				player.sendMessage(MinigameUtils.getLang("minigame.started"), "error");
			}
			else if(minigame.isNotWaitingForPlayers() && minigame.canLateJoin() && 
					minigame.getMpTimer().getPlayerWaitTimeLeft() == 0 && minigame.getPlayers().size() != minigame.getMaxPlayers()){
				player.sendMessage(MinigameUtils.formStr("minigame.lateJoinWait", minigame.getMpTimer().getStartWaitTimeLeft()), null);
			}
			else if(minigame.getStartLocations().size() == 0 || 
							(type == MinigameType.TEAMS && minigame.getStartLocationsBlue().size() == 0 && minigame.getStartLocationsRed().size() == 0)){
				player.sendMessage(MinigameUtils.getLang("minigame.error.noStart"), "error");
			}
			else if(minigame.getEndPosition() == null){
				player.sendMessage(MinigameUtils.getLang("minigame.error.noEnd"), "error");
			}
			else if(minigame.getQuitPosition() == null){
				player.sendMessage(MinigameUtils.getLang("minigame.error.noQuit"), "error");
			}
			else if(minigame.getLobbyPosition() == null){
				player.sendMessage(MinigameUtils.getLang("minigame.error.noLobby"), "error");
			}
			else if(minigame.getPlayers().size() >= minigame.getMaxPlayers()){
				player.sendMessage(MinigameUtils.getLang("minigame.full"), "error");
			}
		}
	}
	
	public void spectateMinigame(MinigamePlayer player, Minigame minigame) {
		SpectateMinigameEvent event = new SpectateMinigameEvent(player, minigame);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			boolean tpd = player.teleport(minigame.getStartLocations().get(0));
			if(!tpd){
				player.sendMessage(MinigameUtils.getLang("minigame.error.noTeleport"), "error");
				return;
			}
			player.storePlayerData();
			player.setMinigame(minigame);
			player.getPlayer().setGameMode(GameMode.ADVENTURE);
			
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
			player.sendMessage(MinigameUtils.formStr("player.spectate.join.plyMsg", minigame.getName(false)) + "\n" +
					MinigameUtils.formStr("player.spectate.join.plyHelp", "\"/minigame quit\""), null);
			mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.spectate.join.minigameMsg", player.getName(), minigame.getName(false)), null, player);
		}
	}
	
	public void startMPMinigame(Minigame minigame, boolean teleport){
		List<MinigamePlayer> players = new ArrayList<MinigamePlayer>();
		players.addAll(minigame.getPlayers());
		
		Collections.shuffle(players);
		
		if(minigame.getType() == MinigameType.TEAMS && ScoreType.getScoreType(minigame.getScoreType()) != null){
			ScoreType.getScoreType(minigame.getScoreType()).balanceTeam(players, minigame);
		}
		
		Location start = null;
		int pos = 0;
		int bluepos = 0;
		int redpos = 0;
		
		Bukkit.getServer().getPluginManager().callEvent(new StartMinigameEvent(players, minigame, teleport));
		
		for(MinigamePlayer ply : players){
			if(minigame.getType() != MinigameType.TEAMS){
				if(pos < minigame.getStartLocations().size()){
					ply.setStartTime(Calendar.getInstance().getTimeInMillis());
					if(teleport){
						start = minigame.getStartLocations().get(pos);
					}
				} 
				else{
					pos = 0;
					if(!minigame.getStartLocations().isEmpty()){
						if(teleport){
							start = minigame.getStartLocations().get(0);
						}
					}
					else {
						ply.sendMessage(MinigameUtils.getLang("minigame.error.incorrectStart"), "error");
						quitMinigame(ply, false);
					}
				}
				ply.setCheckpoint(start);
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
					if(pos < minigame.getStartLocations().size()){
						if(teleport){
							start = minigame.getStartLocations().get(pos);
						}
					} 
					else{
						pos = 0;
						if(!minigame.getStartLocations().isEmpty()){
							if(teleport){
								start = minigame.getStartLocations().get(0);
							}
						}
						else {
							ply.sendMessage(MinigameUtils.getLang("minigame.error.incorrectStart"), "error");
							quitMinigame(ply, false);
						}
					}
				}
				if(minigame.getLives() > 0){
					ply.sendMessage(MinigameUtils.formStr("minigame.livesLeft", minigame.getLives()), null);
				}
			}
			
			if(start != null){
				if(teleport){
					ply.teleport(start);
					ply.setCheckpoint(start);
				}
				if(minigame.getMaxScore() != 0){
					ply.sendMessage(MinigameUtils.formStr("minigame.scoreToWin", minigame.getMaxScorePerPlayer()), null);
				}
			}
			
			pos++;
			ply.getLoadout().equiptLoadout(ply);
			ply.getPlayer().setScoreboard(minigame.getScoreboardManager());
			minigame.setScore(ply, 1);
			minigame.setScore(ply, 0);
		}
	}
	
	public void revertToCheckpoint(MinigamePlayer player) {
		
		RevertCheckpointEvent event = new RevertCheckpointEvent(player);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			player.teleport(player.getCheckpoint());
			player.addRevert();
			player.sendMessage(MinigameUtils.getLang("player.checkpoint.revert"), null);
		}
	}
	
	public void quitMinigame(MinigamePlayer player, boolean forced){
		Minigame minigame = player.getMinigame();
		
		QuitMinigameEvent event = new QuitMinigameEvent(player, minigame, forced);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if(!event.isCancelled()){
			player.setEndTime(System.currentTimeMillis());
			if(!minigame.isSpectator(player)){
				//SQL stuff
				if(plugin.getSQL() != null){
					if(minigame.canSaveCheckpoint() == false){
						plugin.addSQLToStore(new SQLPlayer(minigame.getName(false), player.getName(), player.getUUID().toString(), 0, 1, 
								player.getKills(), player.getDeaths(), player.getScore(), player.getReverts(), 
								player.getEndTime() - player.getStartTime() + player.getStoredTime()));
						plugin.startSQLCompletionSaver();
					}
				}
				
				//Call Types quit.
				mdata.minigameType(minigame.getType()).quitMinigame(player, minigame, forced);
				
				//Prepare player for quit
				if(player.getPlayer().getVehicle() != null){
					Vehicle vehicle = (Vehicle) player.getPlayer().getVehicle();
					vehicle.eject();
				}
				player.getPlayer().closeInventory();
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
				
				if(!player.isDead()){
					player.restorePlayerData();
					player.teleport(minigame.getQuitPosition());
				}
				else{
					player.setQuitPos(minigame.getQuitPosition());
					player.setRequiredQuit(true);
				}
				
				if(minigame.getType() != MinigameType.SINGLEPLAYER){
					if(minigame.getPlayers().size() == 1 && minigame.isNotWaitingForPlayers() && !forced){
						List<MinigamePlayer> w = new ArrayList<MinigamePlayer>();
						w.add(minigame.getPlayers().get(0));
						List<MinigamePlayer> l = new ArrayList<MinigamePlayer>();
						endMinigame(minigame.getPlayers().get(0).getMinigame(), w, l);
						
						if(minigame.getMpBets() != null){
							minigame.setMpBets(null);
						}
					}
				}
				
				//Reset Minigame
				if(minigame.getPlayers().size() == 0){
					if(minigame.getMinigameTimer() != null){
						minigame.getMinigameTimer().stopTimer();
						minigame.setMinigameTimer(null);
					}
					
					if(minigame.getFloorDegenerator() != null){
						minigame.getFloorDegenerator().stopDegenerator();
					}
					
					if(minigame.getBlockRecorder().hasData()){
						minigame.getBlockRecorder().restoreBlocks();
						minigame.getBlockRecorder().restoreEntities();
						minigame.getBlockRecorder().setCreatedRegenBlocks(false);
					}
					minigame.getBlockRecorder().clearRestoreData();
					
					if(minigame.getMpTimer() != null){
						minigame.getMpTimer().pauseTimer();
						minigame.getMpTimer().removeTimer();
						minigame.setMpTimer(null);
					}
					
					if(minigame.getMpBets() != null){
						minigame.setMpBets(null);
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
				else{
					mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.quit.plyForcedMsg", player.getName(), minigame.getName(true)), "error", player);
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
				player.getPlayer().setFlying(false);
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
				player.removeMinigame();
				minigame.removeSpectator(player);

				for(MinigamePlayer pl : minigame.getPlayers()){
					pl.getPlayer().showPlayer(player.getPlayer());
				}
				
				player.sendMessage(MinigameUtils.formStr("player.spectate.quit.plyMsg", minigame.getName(true)), "error");
				mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.spectate.quit.minigameMsg", player.getName(), minigame.getName(true)), "error", player);
			}
		}
	}
	
	public void endMinigame(MinigamePlayer player){
		List<MinigamePlayer> w = new ArrayList<MinigamePlayer>();
		List<MinigamePlayer> l = new ArrayList<MinigamePlayer>();
		w.add(player);
		endMinigame(player.getMinigame(), w, l);
	}
	
	
	public void endMinigame(Minigame mgm, List<MinigamePlayer> winners, List<MinigamePlayer> losers){
		EndMinigameEvent event = new EndMinigameEvent(winners, losers, mgm);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if(!event.isCancelled()){
			winners = event.getWinners();
			losers = event.getLosers();
			
			//Prepare split money
			double bets = 0;
			if(mgm.getMpBets() != null){
				if(mgm.getMpBets().hasMoneyBets()){
					List<MinigamePlayer> plys = new ArrayList<MinigamePlayer>();
					plys.addAll(event.getWinners());
					
					if(!plys.isEmpty()){
						bets = mgm.getMpBets().claimMoneyBets() / (double) plys.size();
						BigDecimal roundBets = new BigDecimal(bets);
						roundBets = roundBets.setScale(2, BigDecimal.ROUND_HALF_UP);
						bets = roundBets.doubleValue();
					}
					mgm.setMpBets(null);
				}
			}
			
			//Broadcast Message
			if(plugin.getConfig().getBoolean("broadcastCompletion") && mgm.isEnabled() && mgm.isEnabled()){
				if(mgm.getType() == MinigameType.TEAMS){
					int team = 0;
					for(OfflinePlayer p : mgm.getBlueTeam()){
						if(winners.get(0).getName().equals(p.getName())){
							team = 1;
							break;
						}
					}
					if(team == 1){
						String score = "";
						if(mgm.getRedTeamScore() != 0 && mgm.getBlueTeamScore() != 0){
							score = ", " + MinigameUtils.formStr("player.end.team.score", ChatColor.BLUE.toString() + mgm.getBlueTeamScore() + ChatColor.WHITE, ChatColor.RED.toString() + mgm.getRedTeamScore());
						}
						MinigameUtils.broadcast(MinigameUtils.formStr("player.end.team.win", ChatColor.BLUE.toString() + "Blue Team" + ChatColor.WHITE, mgm.getName(true)) + score, mgm, ChatColor.GREEN);
					}
					else{
						String score = "";
						if(mgm.getRedTeamScore() != 0 && mgm.getBlueTeamScore() != 0){
							score = ", " + MinigameUtils.formStr("player.end.team.score", ChatColor.RED.toString() + mgm.getRedTeamScore() + ChatColor.WHITE, ChatColor.BLUE.toString() + mgm.getBlueTeamScore());
						}
						MinigameUtils.broadcast(MinigameUtils.formStr("player.end.team.win", ChatColor.RED + "Red Team" + ChatColor.WHITE, mgm.getName(true)) + score, mgm, ChatColor.GREEN);
					}
				}
				else{
					if(winners.size() == 1){
						String score = "";
						if(winners.get(0).getScore() != 0)
							score = MinigameUtils.formStr("player.end.broadcastScore", winners.get(0).getScore());
						MinigameUtils.broadcast(MinigameUtils.formStr("player.end.broadcastMsg", winners.get(0).getDisplayName(), mgm.getName(true)) + ". " + score, mgm, ChatColor.GREEN);
					}
					else if(winners.size() > 1){
						String win = "";
						Collections.sort(winners, new Comparator<MinigamePlayer>() {
							@Override
							public int compare(MinigamePlayer o1,
									MinigamePlayer o2) {
								return Integer.valueOf(o1.getScore()).compareTo(o2.getScore());
							}
						});
						
						for(MinigamePlayer pl : winners){
							if(winners.indexOf(pl) < 2){
								win += pl.getDisplayName();
								if(winners.indexOf(pl) + 2 >= winners.size()){
									win += " and ";
								}
								else{
									win += ", ";
								}
							}
							else{
								win += String.valueOf(winners.size() - 3) + " others";
							}
						}
						MinigameUtils.broadcast(MinigameUtils.formStr("player.end.broadcastMsg", win, mgm.getName(true)) + ". ", mgm, ChatColor.GREEN);
					}
					else{
						MinigameUtils.broadcast(MinigameUtils.formStr("player.end.broadcastNobodyWon", mgm.getName(true)), mgm, ChatColor.RED);
					}
				}
			}
			
			for(MinigamePlayer player : losers){
				quitMinigame(player, true);
			}
			
			for(MinigamePlayer player : winners){
				player.setEndTime(Calendar.getInstance().getTimeInMillis());
				
				//Group money bets
				if(bets != 0){
					plugin.getEconomy().depositPlayer(player.getName(), bets);
					player.sendMessage(MinigameUtils.formStr("player.bet.winMoney", bets), null);
				}
				
				//Restore Player
				if(player.getPlayer().getVehicle() != null){
					Vehicle vehicle = (Vehicle) player.getPlayer().getVehicle();
					vehicle.eject();
				}
				player.getPlayer().setFireTicks(0);
				player.getPlayer().setNoDamageTicks(60);
				for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
					player.getPlayer().removePotionEffect(potion.getType());
				}
				player.getPlayer().closeInventory();
				
				if(player.getPlayer().getWorld() != mgm.getEndPosition().getWorld() && player.getPlayer().hasPermission("minigame.set.end") && plugin.getConfig().getBoolean("warnings")){
					player.sendMessage(ChatColor.RED + "WARNING: " + ChatColor.WHITE + "End location is across worlds! This may cause some server performance issues!", "error");
				}
				
				if(!player.isDead()){
					player.restorePlayerData();
					player.getPlayer().teleport(mgm.getEndPosition());
				}
				else{
					player.setRequiredQuit(true);
					player.setQuitPos(mgm.getEndPosition());
				}
				
				//Reward Player
				boolean hascompleted = false;
				Configuration completion = null;
				if(plugin.getSQL() == null && mgm.isEnabled()){
					completion = mdata.getConfigurationFile("completion");
					hascompleted = completion.getStringList(mgm.getName(false)).contains(player.getUUID().toString().replace("-", "_"));
					
					if(!hascompleted){
						List<String> completionlist = completion.getStringList(mgm.getName(false));
						completionlist.add(player.getUUID().toString().replace("-", "_"));
						completion.set(mgm.getName(false), completionlist);
						MinigameSave completionsave = new MinigameSave("completion");
						completionsave.getConfig().set(mgm.getName(false), completionlist);
						completionsave.saveConfig();
					}
					
					MinigameTypeBase.issuePlayerRewards(player, mgm, hascompleted);
				}
				else if(mgm.isEnabled()){
					plugin.addSQLToStore(new SQLPlayer(mgm.getName(false), player.getName(), player.getUUID().toString(), 1, 0, player.getKills(), player.getDeaths(), player.getScore(), player.getReverts(), player.getEndTime() - player.getStartTime()));
					plugin.startSQLCompletionSaver();
				}
				player.resetAllStats();
				
				//Item Bets (for non groups)
				if(mgm.getMpBets() != null){
					if(mgm.getMpBets().hasBets()){
						player.getPlayer().getInventory().addItem(mgm.getMpBets().claimBets());
						mgm.setMpBets(null);
					}
				}
				
				for(MinigamePlayer pl : mgm.getSpectators()){
					player.getPlayer().showPlayer(pl.getPlayer());
				}
				
				//Restore Minigame
				player.removeMinigame();
				mgm.removePlayer(player);
				player.getPlayer().setFallDistance(0);
				mgm.getScoreboardManager().resetScores(player.getName());
				
				if(mgm.getMinigameTimer() != null){
					mgm.getMinigameTimer().stopTimer();
					mgm.setMinigameTimer(null);
				}
				
				if(mgm.getFloorDegenerator() != null && mgm.getPlayers().size() == 0){
					mgm.getFloorDegenerator().stopDegenerator();
				}
				
				if(mgm.getBlockRecorder().hasData()){
					if(mgm.getType() != MinigameType.SINGLEPLAYER || mgm.getPlayers().isEmpty()){
						mgm.getBlockRecorder().restoreBlocks();
						mgm.getBlockRecorder().restoreEntities();
						mgm.getBlockRecorder().setCreatedRegenBlocks(false);
					}
				}
	
				plugin.getLogger().info(MinigameUtils.formStr("player.end.consMsg", player.getName(), mgm.getName(false)));
				player.sendMessage(MinigameUtils.formStr("player.end.plyMsg", mgm.getName(true)), "win");
				
				if(mgm.getPlayers().size() == 0 && !mgm.isRegenerating()){
					HandlerList.unregisterAll(mgm.getBlockRecorder());
				}
				player.updateInventory();
			}
			
			//Call Types End.
			mdata.minigameType(mgm.getType()).endMinigame(winners, losers, mgm);
		}
	}
	
//	public void endTeamMinigame(int teamnum, Minigame mgm){
//		
//		List<MinigamePlayer> losers = new ArrayList<MinigamePlayer>();
//		List<MinigamePlayer> winners = new ArrayList<MinigamePlayer>();
//		
//		if(teamnum == 1){
//			//Blue team
//			for(OfflinePlayer ply : mgm.getRedTeam()){
//				losers.add(getMinigamePlayer(ply.getName()));
//			}
//			for(OfflinePlayer ply : mgm.getBlueTeam()){
//				winners.add(getMinigamePlayer(ply.getName()));
//			}
//		}
//		else{
//			//Red team
//			for(OfflinePlayer ply : mgm.getRedTeam()){
//				winners.add(getMinigamePlayer(ply.getName()));
//			}
//			for(OfflinePlayer ply : mgm.getBlueTeam()){
//				losers.add(getMinigamePlayer(ply.getName()));
//			}
//		}
//		
//
//		EndTeamMinigameEvent event = new EndTeamMinigameEvent(losers, winners, mgm, teamnum);
//		Bukkit.getServer().getPluginManager().callEvent(event);
//		
//		if(!event.isCancelled()){
//			if(event.getWinningTeamInt() == 1){
//				if(plugin.getConfig().getBoolean("multiplayer.broadcastwin") && mgm.isEnabled()){
//					String score = "";
//					if(mgm.getRedTeamScore() != 0 && mgm.getBlueTeamScore() != 0){
//						score = ", " + MinigameUtils.formStr("player.end.team.score", ChatColor.BLUE.toString() + mgm.getBlueTeamScore() + ChatColor.WHITE, ChatColor.RED.toString() + mgm.getRedTeamScore());
//					}
//					plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + MinigameUtils.formStr("player.end.team.win", ChatColor.BLUE.toString() + "Blue Team" + ChatColor.WHITE, mgm.getName()) + score);
//				}
//			}
//			else{
//				if(plugin.getConfig().getBoolean("multiplayer.broadcastwin") && mgm.isEnabled()){
//					String score = "";
//					if(mgm.getRedTeamScore() != 0 && mgm.getBlueTeamScore() != 0){
//						score = ", " + MinigameUtils.formStr("player.end.team.score", ChatColor.RED.toString() + mgm.getBlueTeamScore() + ChatColor.WHITE, ChatColor.BLUE.toString() + mgm.getRedTeamScore());
//					}
//					plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + MinigameUtils.formStr("player.end.team.win", ChatColor.RED + "Red Team" + ChatColor.WHITE, mgm.getName()) + score);
//				}
//			}
//			
//			mgm.setRedTeamScore(0);
//			mgm.setBlueTeamScore(0);
//			
//			mgm.getMpTimer().setStartWaitTime(0);
//			
//			List<MinigamePlayer> winplayers = new ArrayList<MinigamePlayer>();
//			winplayers.addAll(event.getWinnningPlayers());
//	
//			if(plugin.getSQL() != null && mgm.isEnabled()){
////				new SQLCompletionSaver(mgm.getName(), winplayers, mdata.minigameType(mgm.getType()), true);
//				List<SQLPlayer> sqlplayers = new ArrayList<SQLPlayer>();
//				for(MinigamePlayer ply : winplayers){
//					ply.setEndTime(Calendar.getInstance().getTimeInMillis());
//					sqlplayers.add(new SQLPlayer(mgm.getName(), ply.getName(), 1, 0, ply.getKills(), ply.getDeaths(), ply.getScore(), ply.getReverts(), ply.getEndTime() - ply.getStartTime()));
//				}
//				plugin.addSQLToStore(sqlplayers);
//				plugin.startSQLCompletionSaver();
//			}
//			
//			if(mgm.getMpBets() != null){
//				if(mgm.getMpBets().hasMoneyBets()){
//					List<MinigamePlayer> plys = new ArrayList<MinigamePlayer>();
//					plys.addAll(event.getWinnningPlayers());
//					
//					if(!plys.isEmpty()){
//						double bets = mgm.getMpBets().claimMoneyBets() / (double) plys.size();
//						BigDecimal roundBets = new BigDecimal(bets);
//						roundBets = roundBets.setScale(2, BigDecimal.ROUND_HALF_UP);
//						bets = roundBets.doubleValue();
//						for(MinigamePlayer ply : plys){
//							plugin.getEconomy().depositPlayer(ply.getName(), bets);
//							ply.sendMessage(MinigameUtils.formStr("player.bet.winMoney", bets), null);
//						}
//					}
//				}
//				mgm.setMpBets(null);
//			}
//			
//			if(!event.getLosingPlayers().isEmpty()){
//				List<MinigamePlayer> loseplayers = new ArrayList<MinigamePlayer>();
//				loseplayers.addAll(event.getLosingPlayers());
//				
//				if(plugin.getSQL() != null && mgm.isEnabled()){
////					new SQLCompletionSaver(mgm.getName(), loseplayers, mdata.minigameType(mgm.getType()), false);
//					List<SQLPlayer> sqlplayers = new ArrayList<SQLPlayer>();
//					for(MinigamePlayer ply : loseplayers){
//						ply.setEndTime(Calendar.getInstance().getTimeInMillis());
//						sqlplayers.add(new SQLPlayer(mgm.getName(), ply.getName(), 0, 1, ply.getKills(), ply.getDeaths(), ply.getScore(), ply.getReverts(), ply.getEndTime() - ply.getStartTime()));
//					}
//					plugin.addSQLToStore(sqlplayers);
//					plugin.startSQLCompletionSaver();
//				}
//				
//				for(int i = 0; i < loseplayers.size(); i++){
//					if(loseplayers.get(i) instanceof MinigamePlayer){
//						final MinigamePlayer p = loseplayers.get(i);
//						
////						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
////	
////							@Override
////							public void run() {
//								p.sendMessage(MinigameUtils.getLang("player.quit.plyBeatenMsg"), "error");
//								quitMinigame(p, true);
////							}
////						});
//					}
//					else{
//						loseplayers.remove(i);
//					}
//				}
//				mgm.setMpTimer(null);
//				for(MinigamePlayer pl : loseplayers){
//					mgm.getPlayers().remove(pl);
//				}
//			}
//			
//			for(int i = 0; i < winplayers.size(); i++){
//				if(winplayers.get(i) instanceof MinigamePlayer){
//					final MinigamePlayer p = winplayers.get(i);
//					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//						
//						@Override
//						public void run() {
//							endMinigame(p);
//						}
//					});
//				}
//				else{
//					winplayers.remove(i);
//				}
//			}
//			
//			mgm.setMpTimer(null);
//		}
//	}
	
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
}
