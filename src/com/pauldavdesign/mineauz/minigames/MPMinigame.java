package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class MPMinigame extends MinigameType {
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	public MPMinigame(){}
	
	public void joinMinigame(Player player, String minigame, Minigame mgm){
		if(mgm.getQuitPosition() != null && player.getGameMode() == GameMode.SURVIVAL && mgm.isEnabled() && mgm.getEndPosition() != null && mgm.getLobbyPosition() != null){
			
			player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
			player.setAllowFlight(false);
			plugin.getLogger().info(player.getName() + " started " + minigame);

			String gametype = mgm.getType();
			
			Location lobby = mgm.getLobbyPosition();
			if(!mdata.getMinigame(minigame).getPlayers().isEmpty() && mdata.getMinigame(minigame).getPlayers().size() < mgm.getMaxPlayers()){
				if(mdata.getMinigame(minigame).getMpTimer() == null || mdata.getMinigame(minigame).getMpTimer().getPlayerWaitTimeLeft() != 0){
					mdata.getMinigame(minigame).addPlayer(player);
					
					pdata.storePlayerData(player);
					
					player.teleport(lobby);
					pdata.addPlayerMinigame(player, minigame);
					player.sendMessage(ChatColor.GREEN + "You have started a " + gametype + " minigame, type /minigame quit to exit.");
				
					if(mdata.getMinigame(minigame).getMpTimer() == null && mdata.getMinigame(minigame).getPlayers().size() == mgm.getMinPlayers()){
						mdata.getMinigame(minigame).setMpTimer(new MultiplayerTimer(minigame));
						mdata.getMinigame(minigame).getMpTimer().start();
					}
					else{
						int neededPlayers = mgm.getMinPlayers() - mdata.getMinigame(minigame).getPlayers().size();
						if(neededPlayers == 1){
							player.sendMessage(ChatColor.BLUE + "Waiting for 1 more player.");
						}
						else if(neededPlayers > 1){
							player.sendMessage(ChatColor.BLUE + "Waiting for " + neededPlayers + " more players.");
						}
					}

					List<Player> plys = pdata.playersInMinigame();
					for(Player ply : plys){
						if(minigame.equals(pdata.getPlayersMinigame(ply)) && !ply.getName().equals(player.getName())){
							ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + player.getName() + " has joined " + minigame);
						}
					}
				}
				else if(mdata.getMinigame(minigame).getMpTimer().getPlayerWaitTimeLeft() == 0){
					player.sendMessage(ChatColor.RED + "The minigame has already started. Try again soon.");
				}
			}
			else if(mdata.getMinigame(minigame).getPlayers().isEmpty()){
				mdata.getMinigame(minigame).addPlayer(player);
				
				pdata.storePlayerData(player);
				
				player.teleport(lobby);
				pdata.addPlayerMinigame(player, minigame);
				player.sendMessage(ChatColor.GREEN + "You have started a " + gametype + " minigame, type /minigame quit to exit.");
				
				int neededPlayers = mgm.getMinPlayers() - 1;
				
				if(neededPlayers > 0){
					player.sendMessage(ChatColor.BLUE + "Waiting for " + neededPlayers + " more players.");
				}
				else
				{
					mdata.getMinigame(minigame).setMpTimer(new MultiplayerTimer(minigame));
					mdata.getMinigame(minigame).getMpTimer().start();
				}

				List<Player> plys = pdata.playersInMinigame();
				for(Player ply : plys){
					if(minigame.equals(pdata.getPlayersMinigame(ply)) && !ply.getName().equals(player.getName())){
						ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + player.getName() + " has joined " + minigame);
					}
				}
			}
			else if(mdata.getMinigame(minigame).getPlayers().size() == mgm.getMaxPlayers()){
				player.sendMessage(ChatColor.RED + "Sorry, this minigame is full.");
			}
		}
		else if(mgm.getQuitPosition() == null){
			player.sendMessage(ChatColor.RED + "This minigame has no quit position!");
		}
		else if(mgm.getEndPosition() == null){
			player.sendMessage(ChatColor.RED + "This minigame has no end position!");
		}
		else if(mgm.getLobbyPosition() == null){
			player.sendMessage(ChatColor.RED + "This minigame has no lobby!");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void quitMinigame(Player player, Minigame mgm, boolean forced){

		String minigame = pdata.getPlayersMinigame(player);
		if(!mdata.getMinigame(minigame).getPlayers().isEmpty()){
			mdata.getMinigame(minigame).removePlayer(player);
			if(mdata.getMinigame(minigame).getPlayers().size() == 0){
				if(mdata.getMinigame(minigame).getMpTimer() != null){
					mdata.getMinigame(minigame).getMpTimer().setStartWaitTime(0);
					if(mgm.getType().equals("spleef")){
						SpleefFloorGen floor = new SpleefFloorGen(mgm.getSpleefFloor1(), mgm.getSpleefFloor2());
						if(mgm.getFloorDegenerator() != null){
							mgm.getFloorDegenerator().stopDegenerator();
						}
						floor.regenFloor(mgm.getSpleefFloorMaterial());
					}
					if(mdata.getMinigame(minigame).getMpBets() != null){
						player.getInventory().addItem(mdata.getMinigame(minigame).getMpBets().getPlayersBet(player));
						mdata.getMinigame(minigame).setMpBets(null);
					}
					mdata.getMinigame(minigame).setMpTimer(null);
				}
			}
			else if(mdata.getMinigame(minigame).getPlayers().size() == 1 && mdata.getMinigame(minigame).getMpTimer() != null && mdata.getMinigame(minigame).getMpTimer().getStartWaitTimeLeft() == 0){
				if(!mdata.getMinigame(minigame).getType().equals("race")){
					pdata.endMinigame(mdata.getMinigame(minigame).getPlayers().get(0));
				}
				
				if(mdata.getMinigame(minigame).getMpBets() != null){
					mdata.getMinigame(minigame).setMpBets(null);
				}
			}
			else if(mdata.getMinigame(minigame).getPlayers().size() < mgm.getMinPlayers() && mdata.getMinigame(minigame).getMpTimer() != null && mdata.getMinigame(minigame).getMpTimer().getStartWaitTimeLeft() != 0){
				mdata.getMinigame(minigame).getMpTimer().setStartWaitTime(0);
				mdata.getMinigame(minigame).setMpTimer(null);
				for(Player pl : mdata.getMinigame(minigame).getPlayers()){
					pl.sendMessage(ChatColor.BLUE + "Waiting for " + (mgm.getMinPlayers() - 1) + " more players.");
				}
			}
		}
		
		callGeneralQuit(player);
		
		if(mdata.getMinigame(minigame).getMpTimer() == null){
			if(mdata.getMinigame(minigame).getMpBets() != null){
				player.getInventory().addItem(mdata.getMinigame(minigame).getMpBets().getPlayersBet(player));
				mdata.getMinigame(minigame).getMpBets().removePlayersBet(player);
				player.updateInventory();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void endMinigame(Player player, Minigame mgm){
		player.getInventory().clear();
		String minigame = pdata.getPlayersMinigame(player);
		
		pdata.restorePlayerData(player);
		
		if(mdata.getMinigame(minigame).getMpBets() != null){
			player.getInventory().addItem(mdata.getMinigame(minigame).getMpBets().claimBets());
			mdata.getMinigame(minigame).setMpBets(null);
			player.updateInventory();
		}
		//pdata.saveItems(player);
		pdata.saveInventoryConfig();
		
		boolean hascompleted = false;
		Configuration completion = null;
		
		player.sendMessage(ChatColor.GREEN + "You've finished the " + minigame + " minigame. Congratulations!");
		if(plugin.getConfig().getBoolean("lastmanstanding.broadcastwin")){
			plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE + player.getName() + " won " + mgm.getName());
		}
		
		if(mgm.getEndPosition() != null){
			player.teleport(mgm.getEndPosition());
		}

		mdata.getMinigame(minigame).removePlayer(player);
		
		if(mgm.getPlayers().isEmpty()){
			mdata.getMinigame(minigame).getMpTimer().setStartWaitTime(0);
			if(mgm.getType().equals("spleef")){
				SpleefFloorGen floor = new SpleefFloorGen(mgm.getSpleefFloor1(), mgm.getSpleefFloor2());
				mgm.getFloorDegenerator().stopDegenerator();
				floor.regenFloor(mgm.getSpleefFloorMaterial());
			}
			
			mdata.getMinigame(minigame).setMpTimer(null);
			for(Player pl : mdata.getMinigame(minigame).getPlayers()){
				mdata.getMinigame(minigame).getPlayers().remove(pl);
			}
		}
		else{
			mdata.getMinigame(minigame).getMpTimer().setStartWaitTime(0);
			List<Player> players = new ArrayList<Player>();
			players.addAll(mdata.getMinigame(minigame).getPlayers());
			for(int i = 0; i < players.size(); i++){
				if(players.get(i) instanceof Player){
					Player p = players.get(i);
					if(!p.getName().equals(player.getName())){
						p.sendMessage(ChatColor.RED + "You have been beaten! Bad luck!");
						pdata.quitMinigame(p, false);
					}
				}
				else{
					players.remove(i);
				}
			}
			mdata.getMinigame(minigame).setMpTimer(null);
			for(Player pl : players){
				mdata.getMinigame(minigame).getPlayers().remove(pl);
			}
		}
		
		if(mgm.hasRestoreBlocks()){
			Set<String> blocks = mgm.getRestoreBlocks().keySet();
			
			for(String name : blocks){
				String mat = mgm.getRestoreBlocks().get(name).getBlock().toString();
				if(mat.equalsIgnoreCase("CHEST") || mat.equalsIgnoreCase("FURNACE") || mat.equalsIgnoreCase("DISPENSER")){
					Location loc = mgm.getRestoreBlocks().get(name).getLocation();
					
					if(loc.getBlock().getType() != Material.getMaterial(mat)){
						loc.getBlock().setType(Material.getMaterial(mat));
					}
					
					if(loc.getBlock().getState() instanceof Chest){
						Chest chest = (Chest) loc.getBlock().getState();
						chest.getInventory().clear();
						chest.getInventory().setContents(mgm.getRestoreBlocks().get(name).getItems());
					}
					else if(loc.getBlock().getState() instanceof Furnace){
						Furnace furnace = (Furnace) loc.getBlock().getState();
						furnace.getInventory().clear();
						furnace.getInventory().setContents(mgm.getRestoreBlocks().get(name).getItems());
					}
					else if(loc.getBlock().getState() instanceof Dispenser){
						Dispenser dispenser = (Dispenser) loc.getBlock().getState();
						dispenser.getInventory().clear();
						dispenser.getInventory().setContents(mgm.getRestoreBlocks().get(name).getItems());
					}
				}
			}
		}

		player.setFireTicks(0);
		
		
		plugin.getLogger().info(player.getName() + " completed " + minigame);
		
		if(plugin.getSQL() == null){
			completion = mdata.getConfigurationFile("completion");
			hascompleted = completion.getStringList(minigame).contains(player.getName());
			
			if(plugin.getSQL() == null){
				if(!completion.getStringList(minigame).contains(player.getName())){
					List<String> completionlist = completion.getStringList(minigame);
					completionlist.add(player.getName());
					completion.set(minigame, completionlist);
					MinigameSave completionsave = new MinigameSave("completion");
					completionsave.getConfig().set(minigame, completionlist);
					completionsave.saveConfig();
				}
			}
			
			issuePlayerRewards(player, mgm, hascompleted);
		}
		else{
			new SQLCompletionSaver(minigame, player, this);
		}
	}
}
