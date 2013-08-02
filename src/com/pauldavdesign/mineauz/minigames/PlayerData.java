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
	
	private Map<String, Location> resPos = new HashMap<String, Location>();
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
				if(mdata.minigameType(gametype).joinMinigame(player, minigame)){
					plugin.getLogger().info(player.getName() + " started " + minigame.getName());
					mdata.sendMinigameMessage(minigame, player.getName() + " has joined " + minigame.getName(), null, player);
					
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
				player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "That gametype doesn't exist!");
			}
		}
	}
	
	public void spectateMinigame(MinigamePlayer player, Minigame minigame) {
		SpectateMinigameEvent event = new SpectateMinigameEvent(player, minigame);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			player.storePlayerData();
			player.setMinigame(minigame);
			
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
			player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You have started spectating " + minigame + ".\n" +
					"Type \"/minigame quit\" to leave spectator mode.");
			mdata.sendMinigameMessage(minigame, player.getName() + " is now spectating " + minigame, null, player);
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
						player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You've placed your bet! Good Luck!");
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
						player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Sorry, this minigame is full.");
					}
				}
				else if(item.getType() == Material.AIR && money == 0){
					player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You can not bet nothing!");
				}
				else if(money != 0 && !pbet.canBet(player, money)){
					player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You haven't placed the correct bet amount for this round!");
					player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You must bet $" + minigame.getMpBets().getHighestMoneyBet() + ".");
				}
				else if(money != 0 && plugin.getEconomy().getBalance(player.getName()) < money){
					player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You haven't got enough money!");
					player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You must have $" + minigame.getMpBets().getHighestMoneyBet() + ".");
				}
				else{
					player.sendMessage(ChatColor.RED + "You haven't bet the correct item for this round!");
					player.sendMessage(ChatColor.RED + "You must bet a " + minigame.getMpBets().highestBetName() + ".");
				}
			}
			else if(minigame != null && minigame.getMpTimer() != null && minigame.getMpTimer().getPlayerWaitTimeLeft() == 0){
				player.sendMessage(ChatColor.RED + "The game has already started. Please try again later.");
			}
		}
	}
	
	public void startMPMinigame(String minigame){
		List<MinigamePlayer> players = new ArrayList<MinigamePlayer>();
		players.addAll(mdata.getMinigame(minigame).getPlayers());
		
		Collections.shuffle(players);
		
		Minigame mgm = mdata.getMinigame(minigame);
		if(mgm.getType().equals("teamdm") && ScoreTypes.getScoreType(mgm.getScoreType()) != null){
			ScoreTypes.getScoreType(mgm.getScoreType()).balanceTeam(players, mgm);
		}
		
		Location start = null;
		int pos = 0;
		int bluepos = 0;
		int redpos = 0;
		
		for(MinigamePlayer ply : players){
			if(!mgm.getType().equals("teamdm")){
				if(pos < mgm.getStartLocations().size()){
					start = mgm.getStartLocations().get(pos);
					minigameTeleport(ply, start);
					ply.setCheckpoint(start);
					if(mgm.getMaxScore() != 0 && mgm.getType().equals("dm") && !mgm.getScoreType().equals("none")){
						ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Score to win: " + mgm.getMaxScorePerPlayer(mgm.getPlayers().size()));
					}
				} 
				else{
					pos = 0;
					if(!mgm.getStartLocations().isEmpty()){
						start = mgm.getStartLocations().get(0);
						minigameTeleport(ply, start);
						ply.setCheckpoint(start);
						if(mgm.getMaxScore() != 0 && mgm.getType().equals("dm") && !mgm.getScoreType().equals("none")){
							ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Score to win: " + mgm.getMaxScorePerPlayer(mgm.getPlayers().size()));
						}
					}
					else {
						ply.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Starting positions are incorrectly configured!");
						quitMinigame(ply, false);
					}
				}
			}
			else{
				int team = -1;
				if(mgm.getBlueTeam().contains(ply.getPlayer())){
					team = 1;
				}
				else if(mgm.getRedTeam().contains(ply.getPlayer())){
					team = 0;
				}
				if(!mgm.getStartLocationsRed().isEmpty() && !mgm.getStartLocationsBlue().isEmpty()){
					if(team == 0 && redpos < mgm.getStartLocationsRed().size()){
						start = mgm.getStartLocationsRed().get(redpos);
						redpos++;
					}
					else if(team == 1 && bluepos < mgm.getStartLocationsBlue().size()){
						start = mgm.getStartLocationsBlue().get(bluepos);
						bluepos++;
					}
					else if(team == 0 && !mgm.getStartLocationsRed().isEmpty()){
						redpos = 0;
						start = mgm.getStartLocationsRed().get(redpos);
						redpos++;
					}
					else if(team == 1 && !mgm.getStartLocationsBlue().isEmpty()){
						bluepos = 0;
						start = mgm.getStartLocationsBlue().get(bluepos);
						bluepos++;
					}
					else if(mgm.getStartLocationsBlue().isEmpty() || mgm.getStartLocationsRed().isEmpty()){
						ply.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Starting positions are incorrectly configured!");
						quitMinigame(ply, false);
					}
				}
				else{
					if(pos <= mgm.getStartLocations().size()){
						start = mgm.getStartLocations().get(pos);
						minigameTeleport(ply, start);
						ply.setCheckpoint(start);
					} 
					else{
						pos = 1;
						if(!mgm.getStartLocations().isEmpty()){
							start = mgm.getStartLocations().get(0);
							minigameTeleport(ply, start);
							ply.setCheckpoint(start);
						}
						else {
							ply.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Starting positions are incorrectly configured!");
							quitMinigame(ply, false);
						}
					}
				}
				
				if(start != null){
					minigameTeleport(ply, start);
					ply.setCheckpoint(start);
					if(mgm.getMaxScore() != 0 && !mgm.getScoreType().equals("none")){
						ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Score to win: " + mgm.getMaxScorePerPlayer(mgm.getPlayers().size()));
					}
				}
				
				if(mgm.getLives() > 0){
					ply.sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "Lives left: " + mgm.getLives());
				}
			}
			pos++;
			if(!mgm.getPlayersLoadout(ply).getItems().isEmpty()){
				mgm.getPlayersLoadout(ply).equiptLoadout(ply);
			}
			ply.getPlayer().setScoreboard(mgm.getScoreboardManager());
			mgm.setScore(ply, 1);
			mgm.setScore(ply, 0);
		}
		
		if(mgm.hasPlayers()){
			if(mgm.getSpleefFloor1() != null && mgm.getSpleefFloor2() != null){
				mgm.addFloorDegenerator();
				mgm.getFloorDegenerator().startDegeneration();
			}
	
			if(mgm.hasRestoreBlocks()){
				for(RestoreBlock block : mgm.getRestoreBlocks().values()){
					mgm.getBlockRecorder().addBlock(block.getLocation().getBlock(), null);
				}
			}
			
			if(mgm.getTimer() > 0){
				mgm.setMinigameTimer(new MinigameTimer(mgm, mgm.getTimer()));
				mdata.sendMinigameMessage(mgm, MinigameUtils.convertTime(mgm.getTimer()) + " left.", null, null);
			}
		}
	}
	
	public void revertToCheckpoint(MinigamePlayer player) {
		
		RevertCheckpointEvent event = new RevertCheckpointEvent(player);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			minigameTeleport(player, player.getCheckpoint());
			player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You have been reverted to the checkpoint.");
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
					mdata.sendMinigameMessage(mgm, player.getName() + " has left " + mgm, "error", player);
				}
				else{
					mdata.sendMinigameMessage(mgm, player.getName() + " was removed from " + mgm, "error", player);
				}
	
				mgm.removePlayersLoadout(player);
				final MinigamePlayer ply = player;
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						if(ply.getPlayer().isOnline() && !ply.getPlayer().isDead()){
							ply.restorePlayerData();
						}
					}
				});

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
				
				ply.setAllowTeleport(true);
				ply.setAllowGamemodeChange(true);
				
				player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You quit spectator mode in " + mgm);
				mdata.sendMinigameMessage(mgm, player.getName() + " is no longer spectating " + mgm, "error", player);
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
			mdata.minigameType(mgm.getType()).endMinigame(player, mgm);
			
			for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
				player.getPlayer().removePotionEffect(potion.getType());
			}
			player.getPlayer().setFallDistance(0);
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

			plugin.getLogger().info(player.getName() + " completed " + mgm);
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
						score = ", " + ChatColor.BLUE + mgm.getBlueTeamScore() + ChatColor.WHITE + " to " + ChatColor.RED + mgm.getRedTeamScore();
					}
					plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.BLUE + "Blue Team" + ChatColor.WHITE + " won " + mgm.getName() + score);
				}
			}
			else{
				if(plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
					String score = "";
					if(mgm.getRedTeamScore() != 0 && mgm.getBlueTeamScore() != 0){
						score = ", " + ChatColor.RED + mgm.getRedTeamScore() + ChatColor.WHITE + " to " + ChatColor.BLUE + mgm.getBlueTeamScore();
					}
					plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.RED + "Red Team" + ChatColor.WHITE + " won " + mgm.getName() + score);
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
							ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You won $" + bets);
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
								p.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You have been beaten! Bad luck!");
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
		offlineMinigamePlayers.put(player.getName(), new OfflineMinigamePlayer(player.getName(), player.getStoredItems(), player.getStoredArmour(), player.getFood(), player.getHealth(), player.getSaturation(), player.getLastGamemode(), player.getMinigame().getQuitPosition()));
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
	
	public void storePlayerInventory(String player, ItemStack[] items, ItemStack[] armour, Integer health, Integer food, Float saturation){
		OfflineMinigamePlayer oply = new OfflineMinigamePlayer(player, items, armour, food, health, saturation, GameMode.SURVIVAL, resPos.get(player));
		offlineMinigamePlayers.put(player, oply);
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
			fwm.setPower(0);
			firework.setFireworkMeta(fwm);
		}
	}
	
	public void addRespawnPosition(Player player, Location location){
		resPos.put(player.getName(), location);
	}
	
	public void addRespawnPosition(String player, Location location){
		resPos.put(player, location);
	}
	
	public Location getRespawnPosition(Player player){
		return resPos.get(player.getName());
	}
	
	public boolean hasRespawnPosition(Player player){
		return resPos.containsKey(player.getName());
	}
	
	public void removeRespawnPosition(Player player){
		resPos.remove(player.getName());
	}
	
	public void saveDCPlayers(){
		MinigameSave save = new MinigameSave("dcPlayers");
		for(String player : resPos.keySet()){
			mdata.minigameSetLocations(player, resPos.get(player), "rejoin", save.getConfig());
		}
		save.saveConfig();
	}
	
	public void loadDCPlayers(){
		MinigameSave save = new MinigameSave("dcPlayers");
		for(String player : save.getConfig().getKeys(false)){
			addRespawnPosition(player, mdata.minigameLocations(player, "rejoin", save.getConfig()));
			save.getConfig().set(player, null);
		}
		save.saveConfig();
		save.deleteFile();
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
