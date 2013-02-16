package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
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
import com.pauldavdesign.mineauz.minigames.events.JoinMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.QuitMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.RevertCheckpointEvent;

public class PlayerData {
	private Map<String, String> minigamePlayers = new HashMap<String, String>();
	private Map<String, Location> playerCheckpoints = new HashMap<String, Location>();
	private Map<String, StoredPlayerCheckpoints> storedPlayerCheckpoints = new HashMap<String, StoredPlayerCheckpoints>();
	private Map<String, ItemStack[]> itemStore = new HashMap<String, ItemStack[]>();
	private Map<String, ItemStack[]> armourStore = new HashMap<String, ItemStack[]>();
	private Map<String, List<String>> playerFlags = new HashMap<String, List<String>>();
	private Map<String, Integer> playerFood = new HashMap<String, Integer>();
	private Map<String, Integer> playerHealth = new HashMap<String, Integer>();
	private Map<String, Float> playerSaturation = new HashMap<String, Float>();
	private Map<String, Boolean> allowTP = new HashMap<String, Boolean>();
	private Map<String, Boolean> allowGMChange = new HashMap<String, Boolean>();
	private Map<String, GameMode> lastGM = new HashMap<String, GameMode>();
	
	private boolean partyMode = false;
	
	private Map<String, Location> dcPlayers = new HashMap<String, Location>();
	private List<String> deniedCommands = new ArrayList<String>();
	
	//Stats
	private Map<String, Integer> plyDeaths = new HashMap<String, Integer>();
	private Map<String, Integer> plyKills = new HashMap<String, Integer>();
	
	private static Minigames plugin = Minigames.plugin;
	private MinigameData mdata = plugin.mdata;
	MinigameSave invsave = new MinigameSave("playerinv");
	
	public PlayerData(){}
	
	public void joinMinigame(Player player, Minigame minigame) {
		String gametype = minigame.getType();
		
		JoinMinigameEvent event = new JoinMinigameEvent(player, minigame);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			if(mdata.getMinigameTypes().contains(gametype)){
				if(mdata.minigameType(gametype).joinMinigame(player, minigame)){
					addPlayerMinigame(player, minigame.getName());
					setAllowTP(player, false);
					setAllowGMChange(player, false);
					
					if(hasStoredPlayerCheckpoint(player)){
						if(getPlayersStoredCheckpoints(player).hasCheckpoint(minigame.getName())){
							playerCheckpoints.put(player.getName(), getPlayersStoredCheckpoints(player).getCheckpoint(minigame.getName()));
							getPlayersStoredCheckpoints(player).removeCheckpoint(minigame.getName());
							if(getPlayersStoredCheckpoints(player).hasNoCheckpoints()){
								storedPlayerCheckpoints.remove(player.getName());
							}
							revertToCheckpoint(player);
						}
					}
				}
			}
			else{
				player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "That gametype doesn't exist!");
			}
		}
	}
	
	public void joinWithBet(Player player, Minigame minigame, Double money){
		
		JoinMinigameEvent event = new JoinMinigameEvent(player, minigame, true);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			if(minigame != null && minigame.bettingEnabled() && minigame.isEnabled() && (minigame.getMpTimer() == null || minigame.getMpTimer().getPlayerWaitTimeLeft() != 0)){
				if(minigame.getMpBets() == null && (player.getItemInHand().getType() != Material.AIR || money != 0)){
					minigame.setMpBets(new MultiplayerBets());
				}
				MultiplayerBets pbet = minigame.getMpBets(); 
				ItemStack item = player.getItemInHand().clone();
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
						player.getInventory().removeItem(new ItemStack(item.getType(), 1));
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
					player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You haven't placed a high enough bet!");
					player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You must bet $" + minigame.getMpBets().getHighestMoneyBet() + " or better.");
				}
				else if(money != 0 && plugin.getEconomy().getBalance(player.getName()) < money){
					player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You haven't got enough money!");
					player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You must have $" + minigame.getMpBets().getHighestMoneyBet() + ".");
				}
				else{
					player.sendMessage(ChatColor.RED + "You haven't placed a high enough bet.");
					player.sendMessage(ChatColor.RED + "You must bet a " + minigame.getMpBets().highestBetName() + " or better.");
				}
			}
			else if(!minigame.bettingEnabled() && player.getItemInHand().getType() != Material.AIR){
				player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Bets are not enabled in this minigame.");
				player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Your hand must be empty to join this minigame!");
			}
			else if(minigame.getMpTimer() != null && minigame.getMpTimer().getPlayerWaitTimeLeft() == 0){
				player.sendMessage(ChatColor.RED + "The game has already started. Please try again later.");
			}
			else{
				player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Bets are not enabled in this minigame.");
				joinMinigame(player, minigame);
			}
		}
	}
	
	public void startMPMinigame(String minigame){
		List<Player> players = mdata.getMinigame(minigame).getPlayers();
		for(Player pl : players){
			setAllowTP(pl, true);
		}
		Location start = null;
		int pos = 0;
		int bluepos = 0;
		int redpos = 0;
		
		Minigame mgm = mdata.getMinigame(minigame);
		
		for(int i = 0; i < players.size(); i++){
			if((!mgm.getBlueTeam().contains(players.get(i)) && !mgm.getRedTeam().contains(players.get(i))) || (mgm.getStartLocationsRed().isEmpty() || mgm.getStartLocationsBlue().isEmpty())){
				pos += 1;
				if(pos <= mgm.getStartLocations().size()){
					start = mgm.getStartLocations().get(i);
					players.get(i).teleport(start);
					setPlayerCheckpoints(players.get(pos - 1), start);
					if(mgm.getMaxScore() != 0 && mgm.getType().equals("dm") && !mgm.getScoreType().equals("none")){
						players.get(i).sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Score to win: " + mgm.getMaxScorePerPlayer(mgm.getPlayers().size()));
					}
				} 
				else{
					pos = 1;
					if(!mgm.getStartLocations().isEmpty()){
						start = mgm.getStartLocations().get(0);
						players.get(i).teleport(start);
					}
					else {
						players.get(i).sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Starting positions are incorrectly configured!");
						quitMinigame(players.get(i), false);
					}
				}
			}
			else{
				int team = 0;
				if(mgm.getBlueTeam().contains(players.get(i))){
					team = 1;
				}
				
				if(team == 1){
					if(mgm.getRedTeam().size() < mgm.getBlueTeam().size() - 1){
						mgm.getBlueTeam().remove(players.get(i));
						mgm.addRedTeamPlayer(players.get(i));
						team = 0;
						players.get(i).sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "You have been auto balanced to " + ChatColor.RED + "Red Team");
					}
				}
				else{
					if(mgm.getBlueTeam().size() < mgm.getRedTeam().size() - 1){
						mgm.getRedTeam().remove(players.get(i));
						mgm.addBlueTeamPlayer(players.get(i));
						team = 1;
						players.get(i).sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "You have been auto balanced to " + ChatColor.BLUE + "Blue Team");
					}
				}
				
				pos += 1;
				//if(pos <= mgm.getStartLocations().size()){
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
					players.get(i).sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Starting positions are incorrectly configured!");
					quitMinigame(players.get(i), false);
				}
				
				if(start != null){
					players.get(i).teleport(start);
					setPlayerCheckpoints(players.get(pos - 1), start);
					if(mgm.getMaxScore() != 0 && !mgm.getScoreType().equals("none")){
						players.get(i).sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Score to win: " + mgm.getMaxScorePerPlayer(mgm.getPlayers().size()));
					}
				}
			}
			
			if(mgm.hasDefaultLoadout() || mgm.hasLoadouts()){
				mgm.getLoadout(mgm.getPlayersLoadout(players.get(i))).equiptLoadout(players.get(i));
			}
		}

		if(mgm.getSpleefFloor1() != null && mgm.getSpleefFloor2() != null){
			mgm.addFloorDegenerator();
			mgm.getFloorDegenerator().start();
		}

		if(mgm.hasRestoreBlocks()){
			for(RestoreBlock block : mgm.getRestoreBlocks().values()){
				mgm.getBlockRecorder().addBlock(block.getLocation().getBlock(), null);
			}
		}
		
		for(Player pl : players){
			setAllowTP(pl, false);
		}
		
		if(mgm.getTimer() > 0){
			mgm.setMinigameTimer(new MinigameTimer(mgm, mgm.getTimer()));
			for(Player pl : players){
				pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.convertTime(mgm.getTimer()) + " left.");
			}
		}
	}
	
	public void revertToCheckpoint(Player player) {
		
		RevertCheckpointEvent event = new RevertCheckpointEvent(player);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			setAllowTP(player, true);
			Location loc = getPlayerCheckpoint(player);
			player.teleport(loc);
			player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You have been reverted to the checkpoint.");
			setAllowTP(player, false);
		}
	}
	
	public void quitMinigame(Player player, boolean forced){
		String minigame = getPlayersMinigame(player);
		final Minigame mgm = mdata.getMinigame(minigame);

		QuitMinigameEvent event = new QuitMinigameEvent(player, mgm, forced);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if(!event.isCancelled()){
			setAllowTP(player, true);
			
			if(player.getVehicle() != null){
				Vehicle vehicle = (Vehicle) player.getVehicle();
				vehicle.eject();
			}
			
			player.closeInventory();
			
			List<Player> plys = mdata.getMinigame(minigame).getPlayers();
			for(Player ply : plys){
				if(!ply.getName().equals(player.getName())){
					if(!forced){
						ply.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + player.getName() + " has left " + minigame);
					}
					else{
						ply.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + player.getName() + " was removed from " + minigame);
					}
				}
			}

			mgm.removePlayersLoadout(player);
			
			final Player ply = player;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					restorePlayerData(ply);
				}
			});
			
			mdata.minigameType(mgm.getType()).quitMinigame(player, mgm, forced);
			removePlayerMinigame(player);
			
			for(PotionEffect potion : player.getActivePotionEffects()){
				player.removePotionEffect(potion.getType());
			}
			player.setFireTicks(0);
			player.setNoDamageTicks(60);
			
			removeAllPlayerFlags(player);
			
			removePlayerDeath(player);
			removePlayerKills(player);
			
			if(mgm.getMinigameTimer() != null && mgm.getPlayers().size() == 0){
				mgm.getMinigameTimer().stopTimer();
				mgm.setMinigameTimer(null);
			}
			
			if(mgm.getPlayers().size() == 0 && mgm.getBlockRecorder().hasData()){
				mgm.getBlockRecorder().restoreBlocks();
				mgm.getBlockRecorder().restoreEntities();
			}
			
			removeAllowTP(player);
			removeAllowGMChange(player);
		}
	}
	
	public void endMinigame(final Player player){
		String minigame = getPlayersMinigame(player);
		final Minigame mgm = mdata.getMinigame(minigame);
		
		EndMinigameEvent event = new EndMinigameEvent(player, mgm);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			setAllowTP(player, true);
			
			if(player.getVehicle() != null){
				Vehicle vehicle = (Vehicle) player.getVehicle();
				vehicle.eject();
			}
			
			player.closeInventory();
			restorePlayerData(player);
			
			mdata.minigameType(mgm.getType()).endMinigame(player, mgm);
			
			removePlayerMinigame(player);
			mgm.removePlayersLoadout(player);
			
			for(PotionEffect potion : player.getActivePotionEffects()){
				player.removePotionEffect(potion.getType());
			}
			player.setFireTicks(0);
			player.setNoDamageTicks(60);
			
			removeAllPlayerFlags(player);
			
			if(plugin.getSQL() == null || !plugin.getSQL().checkConnection()){
				removePlayerDeath(player);
				removePlayerKills(player);
			}
			
//			if(mgm.hasRestoreBlocks()){
//				mdata.restoreMinigameBlocks(mgm);
//			}
			
			if(mgm.getMinigameTimer() != null){
				mgm.getMinigameTimer().stopTimer();
				mgm.setMinigameTimer(null);
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
			
			removeAllowTP(player);
			removeAllowGMChange(player);
		}
	}
	
	public void endTeamMinigame(int teamnum, Minigame mgm){
		
		List<Player> losers = null;
		List<Player> winners = null;
		
		if(teamnum == 1){
			//Blue team
			losers = mgm.getRedTeam();
			winners = mgm.getBlueTeam();
			if(plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
				plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.BLUE + "Blue Team" + ChatColor.WHITE + " won " + mgm.getName() + ", " + ChatColor.BLUE + mgm.getBlueTeamScore() + ChatColor.WHITE + " to " + ChatColor.RED + mgm.getRedTeamScore());
			}
		}
		else{
			//Red team
			losers = mgm.getBlueTeam();
			winners = mgm.getRedTeam();
			if(plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
				plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.RED + "Red Team" + ChatColor.WHITE + " won " + mgm.getName() + ", " + ChatColor.RED + mgm.getRedTeamScore() + ChatColor.WHITE + " to " + ChatColor.BLUE + mgm.getBlueTeamScore());
			}
		}
		
		mgm.setRedTeamScore(0);
		mgm.setBlueTeamScore(0);
		
		mgm.getMpTimer().setStartWaitTime(0);
		
		List<Player> winplayers = new ArrayList<Player>();
		winplayers.addAll(winners);

		if(plugin.getSQL() != null){
			new SQLCompletionSaver(mgm.getName(), winplayers, mdata.minigameType(mgm.getType()));
		}
		
		if(mgm.getMpBets() != null){
			if(mgm.getMpBets().hasMoneyBets()){
				List<Player> plys = null;
				if(teamnum == 0){
					plys = mgm.getRedTeam();
				}
				else{
					plys = mgm.getBlueTeam();
				}
				double bets = mgm.getMpBets().claimMoneyBets() / (double) plys.size();
				for(Player ply : plys){
					plugin.getEconomy().depositPlayer(ply.getName(), bets);
					ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You won $" + bets);
				}
			}
			mgm.setMpBets(null);
		}
		
		if(!losers.isEmpty()){
			List<Player> loseplayers = new ArrayList<Player>();
			loseplayers.addAll(losers);
			for(int i = 0; i < loseplayers.size(); i++){
				if(loseplayers.get(i) instanceof Player){
					final Player p = loseplayers.get(i);
					
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
			for(Player pl : loseplayers){
				mgm.getPlayers().remove(pl);
			}
		}
		
		for(int i = 0; i < winplayers.size(); i++){
			if(winplayers.get(i) instanceof Player){
				final Player p = winplayers.get(i);
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
	
	public boolean playerInMinigame(Player player){
		return minigamePlayers.containsKey(player.getName());
	}
	
	public List<Player> playersInMinigame(){
		List<Player> players = new ArrayList<Player>();
		for(Player player : plugin.getServer().getOnlinePlayers()){
			if(playerInMinigame(player)){
				players.add(player);
			}
		}
		return players;
	}
	
	public void addPlayerMinigame(Player player, String minigame){
		minigamePlayers.put(player.getName(), minigame);
	}
	
	public void removePlayerMinigame(Player player){
		if(minigamePlayers.containsKey(player.getName())){
			minigamePlayers.remove(player.getName());
		}
	}
	
	public String getPlayersMinigame(Player player){
		return minigamePlayers.get(player.getName());
	}
	
	@SuppressWarnings("deprecation")
	public void storePlayerData(Player player, GameMode gm){
		ItemStack[] items = player.getInventory().getContents();
		ItemStack[] armour = player.getInventory().getArmorContents();
		itemStore.put(player.getName(), items);
		armourStore.put(player.getName(), armour);
		playerFood.put(player.getName(), player.getFoodLevel());
		playerHealth.put(player.getName(), player.getHealth());
		playerSaturation.put(player.getName(), player.getSaturation());
		
		//lastGM.put(player, player.getGameMode());
		setPlayersLastGameMode(player, player.getGameMode());
		player.setGameMode(gm);
		
		player.setSaturation(15);
		player.setFoodLevel(20);
		player.setHealth(player.getMaxHealth());
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		
		saveItems(player);
		player.updateInventory();
	}
	
	public void storePlayerInventory(String player, ItemStack[] items, ItemStack[] armour, Integer health, Integer food, Float saturation){
		itemStore.put(player, items);
		armourStore.put(player, armour);
		playerHealth.put(player, health);
		playerFood.put(player, food);
		playerSaturation.put(player, saturation);
	}
	
	@SuppressWarnings("deprecation")
	public void restorePlayerData(final Player player){
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		
		ItemStack[] items = itemStore.get(player.getName());
		ItemStack[] armour = armourStore.get(player.getName());
		
		player.getInventory().setContents(items);
		player.getInventory().setArmorContents(armour);
		player.setFoodLevel(playerFood.get(player.getName()));
		player.setHealth(playerHealth.get(player.getName()));
		player.setSaturation(playerSaturation.get(player.getName()));
		setAllowGMChange(player, true);
		player.setGameMode(getPlayersLastGameMode(player));
		removePlayersLastGameMode(player);
		setAllowGMChange(player, false);
		
		invsave.getConfig().set("inventories." + player.getName(), null);
		invsave.saveConfig();
		
		player.updateInventory();
	}
	
	public Boolean getAllowTP(Player player){
		return allowTP.get(player.getName());
	}
	
	public void setAllowTP(Player player, Boolean var){
		allowTP.put(player.getName(), var);
	}
	
	public void removeAllowTP(Player player){
		allowTP.remove(player.getName());
	}
	
	public Boolean getAllowGMChange(Player player){
		return allowGMChange.get(player.getName());
	}
	
	public void setAllowGMChange(Player player, Boolean var){
		allowGMChange.put(player.getName(), var);
	}
	
	public void removeAllowGMChange(Player player){
		allowGMChange.remove(player.getName());
	}
	
	public void setPlayerCheckpoints(Player player, Location checkpoint){
		playerCheckpoints.put(player.getName(), checkpoint);
	}
	
	public void removePlayerCheckpoints(Player player){
		if(playerCheckpoints.containsKey(player.getName())){
			playerCheckpoints.remove(player.getName());
		}
	}
	
	public Location getPlayerCheckpoint(Player player){
		if(playerCheckpoints.containsKey(player.getName())){
			return playerCheckpoints.get(player.getName());
		}
		return null;
	}
	
	public void addPlayerFlags(Player player, String flag){
		List<String> list;
		
		if(playerFlags.containsKey(player.getName())){
			list = playerFlags.get(player.getName());
		}
		else{
			list = new ArrayList<String>();
		}
		
		list.add(flag);
		playerFlags.put(player.getName(), list);
	}
	
	public List<String> checkRequiredFlags(Player player, String minigame){
		List<String> checkpoints = new ArrayList<String>();
		checkpoints.addAll(mdata.getMinigame(minigame).getFlags());
		List<String> pchecks = playerFlags.get(player.getName());
		
		if(playerFlags.containsKey(player.getName()) && !pchecks.isEmpty()){
			checkpoints.removeAll(pchecks);
		}
		
		return checkpoints;
	}
	
	public boolean playerHasFlag(Player player, String flag){
		if(playerFlags.containsKey(player.getName())){
			List<String> flags = playerFlags.get(player.getName());
			if(flags.contains("flag")){
				return true;
			}
		}
		return false;
	}
	
	public void removeAllPlayerFlags(Player player){
		if(playerFlags.containsKey(player.getName())){
			playerFlags.remove(player.getName());
		}
	}
	
	public void addPlayerKill(Player ply){
		if(!plyKills.containsKey(ply.getName())){
			plyKills.put(ply.getName(), 0);
		}
		plyKills.put(ply.getName(), plyKills.get(ply.getName()) + 1);
	}
	
	public Integer getPlayerKills(Player ply){
		if(!plyKills.containsKey(ply.getName())){
			return 0;
		}
		return plyKills.get(ply.getName());
	}
	
	public void removePlayerKills(Player ply){
		if(plyKills.containsKey(ply.getName())){
			plyKills.remove(ply.getName());
		}
	}
	
	public void addPlayerDeath(Player ply){
		if(!plyDeaths.containsKey(ply.getName())){
			plyDeaths.put(ply.getName(), 0);
		}
		plyDeaths.put(ply.getName(), plyDeaths.get(ply.getName()) + 1);
	}
	
	public Integer getPlayerDeath(Player ply){
		if(!plyDeaths.containsKey(ply.getName())){
			return 0;
		}
		return plyDeaths.get(ply.getName());
	}
	
	public void removePlayerDeath(Player ply){
		if(plyDeaths.containsKey(ply.getName())){
			plyDeaths.remove(ply.getName());
		}
	}
	
	public GameMode getPlayersLastGameMode(Player player){
		if(lastGM.containsKey(player.getName())){
			return lastGM.get(player.getName());
		}
		else{
			return player.getGameMode();
		}
	}
	
	public void setPlayersLastGameMode(Player player, GameMode gm){
		lastGM.put(player.getName(), gm);
	}
	
	public boolean removePlayersLastGameMode(Player player){
		if(lastGM.containsKey(player.getName())){
			lastGM.remove(player.getName());
			return true;
		}
		return false;
	}
	
	public void saveItems(Player player){
		if(itemStore.get(player.getName()) != null){
			int num = 0;
			for(ItemStack item : itemStore.get(player.getName())){
				if(item != null){
					invsave.getConfig().set("inventories." + player.getName() + "." + num, item);
				}
				num++;
			}
		}
		else{
			invsave.getConfig().set("inventories." + player.getName(), null);
		}
		
		if(armourStore.get(player.getName()) != null){
			int num = 0;
			for(ItemStack item : armourStore.get(player.getName())){
				if(item != null){
					invsave.getConfig().set("inventories." + player.getName() + ".armour." + num, item);
				}
				num++;
			}
		}
		
		if(playerFood.containsKey(player.getName())){
			invsave.getConfig().set("inventories." + player.getName() + ".food", playerFood.get(player.getName()));
		}
		
		if(playerSaturation.containsKey(player.getName())){
			invsave.getConfig().set("inventories." + player.getName() + ".saturation", playerSaturation.get(player.getName()));
		}
		
		if(playerHealth.containsKey(player.getName())){
			invsave.getConfig().set("inventories." + player.getName() + ".health", playerHealth.get(player.getName()));
		}
		invsave.saveConfig();
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
	
	public void partyMode(Player player){
		if(onPartyMode()){
			Location loc = player.getLocation();
			Firework firework = (Firework) player.getWorld().spawnEntity(loc, EntityType.FIREWORK);
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
	
	public void addDCPlayer(Player player, Location location){
		dcPlayers.put(player.getName(), location);
	}
	
	public void addDCPlayer(String player, Location location){
		dcPlayers.put(player, location);
	}
	
	public Location getDCPlayer(Player player){
		return dcPlayers.get(player.getName());
	}
	
	public boolean hasDCPlayer(Player player){
		return dcPlayers.containsKey(player.getName());
	}
	
	public void removeDCPlayer(Player player){
		dcPlayers.remove(player.getName());
	}
	
	public void saveDCPlayers(){
		MinigameSave save = new MinigameSave("dcPlayers");
		for(String player : dcPlayers.keySet()){
			mdata.minigameSetLocations(player, dcPlayers.get(player), "rejoin", save.getConfig());
		}
		save.saveConfig();
	}
	
	public void loadDCPlayers(){
		MinigameSave save = new MinigameSave("dcPlayers");
		for(String player : save.getConfig().getKeys(false)){
			addDCPlayer(player, mdata.minigameLocations(player, "rejoin", save.getConfig()));
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
	
	public boolean hasStoredPlayerCheckpoint(Player player){
		if(storedPlayerCheckpoints.containsKey(player.getName())){
			return true;
		}
		return false;
	}
	
	public StoredPlayerCheckpoints getPlayersStoredCheckpoints(Player player){
		return storedPlayerCheckpoints.get(player.getName());
	}
	
	public void addStoredPlayerCheckpoint(Player player, String minigame, Location checkpoint){
		StoredPlayerCheckpoints spc = new StoredPlayerCheckpoints(player.getName(), minigame, checkpoint);
		storedPlayerCheckpoints.put(player.getName(), spc);
	}
	
	public void addStoredPlayerCheckpoints(String name, StoredPlayerCheckpoints spc){
		storedPlayerCheckpoints.put(name, spc);
	}
}
