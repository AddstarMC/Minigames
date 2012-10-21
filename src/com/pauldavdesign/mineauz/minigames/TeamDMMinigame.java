package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet5EntityEquipment;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.Configuration;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;

public class TeamDMMinigame extends MinigameType implements Listener{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	public TeamDMMinigame(){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@Override
	public void joinMinigame(final Player player, String minigame, Minigame mgm){
		if(mgm.getQuitPosition() != null && player.getGameMode() == GameMode.SURVIVAL && mgm.isEnabled() && mgm.getEndPosition() != null && mgm.getLobbyPosition() != null){
			
			int redSize = mgm.getRedTeam().size();
			int blueSize = mgm.getBlueTeam().size();
			
			player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
			player.setAllowFlight(false);
			plugin.getLogger().info(player.getName() + " started " + minigame);
			
			Location lobby = mgm.getLobbyPosition();
			
			String gametype = mgm.getType();
			
			if(!mdata.getMinigame(minigame).getPlayers().isEmpty() && mdata.getMinigame(minigame).getPlayers().size() < mgm.getMaxPlayers()){
				if(mdata.getMinigame(minigame).getMpTimer() == null || mdata.getMinigame(minigame).getMpTimer().getPlayerWaitTimeLeft() != 0){
					mdata.getMinigame(minigame).addPlayer(player);
					
					if(redSize <= blueSize){
						mgm.addRedTeamPlayer(player);
						player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You have joined " + ChatColor.RED + "Red Team");
						
						applyTeam(player, 0);
					}
					else{
						mgm.addBlueTeamPlayer(player);
						player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You have joined " + ChatColor.BLUE + "Blue Team");
						
						applyTeam(player, 1);
					}
					
					for(final Player play : mgm.getBlueTeam()){
						Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
							final Player ply = play;
							@Override
							public void run() {
								if(ply != player){
									applyTeam(ply, player, 1);
								}
							}
						}, 20L);
					}
					
					for(final Player play : mgm.getRedTeam()){
						Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
							final Player ply = play;
							@Override
							public void run() {
								if(ply != player){
									applyTeam(ply, player, 0);
								}
							}
						}, 20L);
					}
					
					pdata.storePlayerData(player);
					
					player.teleport(lobby);
					pdata.addPlayerMinigame(player, minigame);
					player.sendMessage(ChatColor.GREEN + "You have started a " + gametype + " minigame, type /minigame quit to exit.");
				
					if(mdata.getMinigame(minigame).getMpTimer() == null && mdata.getMinigame(minigame).getPlayers().size() >= mgm.getMinPlayers()){
						mdata.getMinigame(minigame).setMpTimer(new MultiplayerTimer(minigame));
						mdata.getMinigame(minigame).getMpTimer().start();
					}
					else if(mgm.getMpTimer() != null && mgm.getMpTimer().isPaused() && 
							(mgm.getBlueTeam().size() == mgm.getRedTeam().size() || 
							mgm.getBlueTeam().size() + 1 == mgm.getRedTeam().size() || 
							mgm.getBlueTeam().size() == mgm.getRedTeam().size() + 1)){
						mgm.getMpTimer().resumeTimer();
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
							String teamColour = ChatColor.RED + "Red";
							if(mgm.getBlueTeam().contains(player)){
								teamColour = ChatColor.BLUE + "Blue";
							}
							ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + player.getName() + " has joined " + teamColour + ChatColor.WHITE + " team!");
						}
					}
				}
				else if(mdata.getMinigame(minigame).getMpTimer().getPlayerWaitTimeLeft() == 0){
					player.sendMessage(ChatColor.RED + "The minigame has already started. Try again soon.");
				}
			}
			else if(mdata.getMinigame(minigame).getPlayers().isEmpty()){
				mdata.getMinigame(minigame).addPlayer(player);
				
				EntityPlayer changeingName = ((CraftPlayer) player).getHandle();
				String oldName = player.getName();
				
				if(redSize <= blueSize){
					mgm.addRedTeamPlayer(player);
					player.sendMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE + "You have joined " + ChatColor.RED + "Red Team");
					
					changeingName.name = ChatColor.RED.toString() + player.getName();
				}
				else{
					mgm.addBlueTeamPlayer(player);
					player.sendMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE + "You have joined " + ChatColor.BLUE + "Blue Team");
					
					changeingName.name = ChatColor.BLUE.toString() + player.getName();
				}
				
				for(Player ply : plugin.getServer().getOnlinePlayers()){
					if(ply != player){
						((CraftPlayer) ply).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(changeingName));
					}
				}
				
				changeingName.name = oldName;
				
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
						String teamColour = ChatColor.RED + "Red";
						if(mgm.getBlueTeam().contains(player)){
							teamColour = ChatColor.BLUE + "Blue";
						}
						ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + player.getName() + " has joined " + teamColour + ChatColor.WHITE + " team!");
					}
				}
			}
			else if(mdata.getMinigame(minigame).getPlayers().size() == mgm.getMaxPlayers()){
				player.sendMessage(ChatColor.RED + "Sorry, this minigame is full.");
			}
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void quitMinigame(Player player, Minigame mgm, boolean forced){

		String minigame = pdata.getPlayersMinigame(player);
		if(!mdata.getMinigame(minigame).getPlayers().isEmpty()){
			mdata.getMinigame(minigame).removePlayer(player);
			if(mgm.getRedTeam().contains(player)){
				mgm.getRedTeam().remove(player);
			}
			else{
				mgm.getBlueTeam().remove(player);
			}
			
			if(mdata.getMinigame(minigame).getPlayers().size() == 0 && !forced){
				if(mdata.getMinigame(minigame).getMpTimer() != null){
					mdata.getMinigame(minigame).getMpTimer().setStartWaitTime(0);
					if(mdata.getMinigame(minigame).getMpBets() != null){
						player.getInventory().addItem(mdata.getMinigame(minigame).getMpBets().getPlayersBet(player));
						mdata.getMinigame(minigame).setMpBets(null);
					}
					mdata.getMinigame(minigame).setMpTimer(null);
				}
			}
			else if(mdata.getMinigame(minigame).getPlayers().size() >= 1 && 
					(mdata.getMinigame(minigame).getRedTeam().size() == 0 ||
					mdata.getMinigame(minigame).getBlueTeam().size() == 0) &&
					mdata.getMinigame(minigame).getMpTimer() != null && 
					mdata.getMinigame(minigame).getMpTimer().getStartWaitTimeLeft() == 0
					&& !forced){
				
				if(mdata.getMinigame(minigame).getRedTeam().size() == 0){
					endTeamMinigame(1, mgm);
				}
				else{
					endTeamMinigame(0, mgm);
				}
				
				if(mdata.getMinigame(minigame).getMpBets() != null){
					mdata.getMinigame(minigame).setMpBets(null);
				}
			}
			else if(mdata.getMinigame(minigame).getPlayers().size() < mgm.getMinPlayers() && 
					mdata.getMinigame(minigame).getMpTimer() != null && 
					mdata.getMinigame(minigame).getMpTimer().getStartWaitTimeLeft() != 0
					&& !forced){
				mdata.getMinigame(minigame).getMpTimer().setPlayerWaitTime(10);
				mdata.getMinigame(minigame).getMpTimer().setStartWaitTime(0);
				mdata.getMinigame(minigame).setMpTimer(null);
				for(Player pl : mdata.getMinigame(minigame).getPlayers()){
					pl.sendMessage(ChatColor.BLUE + "Waiting for " + (mgm.getMinPlayers() - 1) + " more players.");
				}
			}
			else if(mgm.getBlueTeam().size() > mgm.getRedTeam().size() + 1 || mgm.getRedTeam().size() > mgm.getBlueTeam().size() + 1){
				if(mgm.getMpTimer() != null){
					mgm.getMpTimer().pauseTimer("Teams unbalanced!");
				}
			}
		}
		
		callGeneralQuit(player);
		
		EntityPlayer changeingName = ((CraftPlayer) player).getHandle();
		for(Player ply : plugin.getServer().getOnlinePlayers()){
			if(ply != player && !player.isDead() && player.isOnline()){
				((CraftPlayer) ply).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(changeingName));
			}
		}
		
		if(mdata.getMinigame(minigame).getMpTimer() == null){
			if(mdata.getMinigame(minigame).getMpBets() != null){
				player.getInventory().addItem(mdata.getMinigame(minigame).getMpBets().getPlayersBet(player));
				mdata.getMinigame(minigame).getMpBets().removePlayersBet(player);
				player.updateInventory();
			}
		}
	}
	
	@Override
	//@SuppressWarnings("deprecation")
	public void endMinigame(Player player, Minigame mgm){
		player.getInventory().clear();
		String minigame = pdata.getPlayersMinigame(player);
		
		/*if(mdata.getMinigame(minigame).getMpBets() != null){
			player.getInventory().addItem(mdata.getMinigame(minigame).getMpBets().claimBets());
			mdata.getMinigame(minigame).setMpBets(null);
			player.updateInventory();
		}*/ //TODO Fix betting system for teams
		
		boolean hascompleted = false;
		Configuration completion = null;
		
		player.sendMessage(ChatColor.GREEN + "You've finished the " + minigame + " minigame. Congratulations!");
		
		if(mgm.getEndPosition() != null){
			player.teleport(mgm.getEndPosition());
		}

		mdata.getMinigame(minigame).removePlayer(player);
		if(mgm.getRedTeam().contains(player)){
			mgm.getRedTeam().remove(player);
		}
		else{
			mgm.getBlueTeam().remove(player);
		}

		player.setFireTicks(0);
		
		EntityPlayer changeingName = ((CraftPlayer) player).getHandle();
		for(Player ply : plugin.getServer().getOnlinePlayers()){
			if(ply != player){
				((CraftPlayer) ply).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(changeingName));
			}
		}
		
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
		
		pdata.restorePlayerData(player);
		pdata.saveItems(player);
		pdata.saveInventoryConfig();
	}
	
	public void endTeamMinigame(int teamnum, Minigame mgm){
		
		List<Player> losers = null;
		List<Player> winners = null;
		
		if(teamnum == 1){
			//Blue team
			losers = mgm.getRedTeam();
			winners = mgm.getBlueTeam();
			if(plugin.getConfig().getBoolean("lastmanstanding.broadcastwin")){
				plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.BLUE + "Blue Team" + ChatColor.WHITE + " won " + mgm.getName() + ", " + ChatColor.BLUE + mgm.getBlueTeamScore() + ChatColor.WHITE + " to " + ChatColor.RED + mgm.getRedTeamScore());
			}
		}
		else{
			//Red team
			losers = mgm.getBlueTeam();
			winners = mgm.getRedTeam();
			if(plugin.getConfig().getBoolean("lastmanstanding.broadcastwin")){
				plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.RED + "Red Team" + ChatColor.WHITE + " won " + mgm.getName() + ", " + ChatColor.RED + mgm.getRedTeamScore() + ChatColor.WHITE + " to " + ChatColor.BLUE + mgm.getBlueTeamScore());
			}
		}
		
		mgm.setRedTeamScore(0);
		mgm.setBlueTeamScore(0);
		
		mgm.getMpTimer().setStartWaitTime(0);
		mgm.setMpTimer(null);
		
		List<Player> winplayers = new ArrayList<Player>();
		winplayers.addAll(winners);

		if(plugin.getSQL() != null){
			new SQLCompletionSaver(mgm.getName(), winplayers, this);
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
							p.sendMessage(ChatColor.RED + "You have been beaten! Bad luck!");
							pdata.quitMinigame(p, false);
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
						pdata.endMinigame(p);
					}
				});
			}
			else{
				winplayers.remove(i);
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
	}
	
	public void applyTeam(Player player, int team){
		EntityPlayer changeingName = ((CraftPlayer) player).getHandle();
		String oldName = player.getName();
		
		if(team == 1){
			changeingName.name = ChatColor.BLUE.toString() + player.getName();
		}
		else{
			changeingName.name = ChatColor.RED.toString() + player.getName();
		}
		
		for(Player ply : plugin.getServer().getOnlinePlayers()){
			if(ply != player){
//				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//					
//					@Override
//					public void run() {
						CraftItemStack hand = null;
						CraftItemStack boots = null;
						CraftItemStack leggings = null;
						CraftItemStack chest = null;
						CraftItemStack helmet = null;
						if(player.getItemInHand() instanceof CraftItemStack){
							hand = (CraftItemStack) player.getItemInHand();
						}
						if(player.getInventory().getBoots() instanceof CraftItemStack){
							boots = (CraftItemStack) player.getInventory().getBoots();
						}
						if(player.getInventory().getLeggings() instanceof CraftItemStack){
							leggings = (CraftItemStack) player.getInventory().getLeggings();
						}
						if(player.getInventory().getChestplate() instanceof CraftItemStack){
							chest = (CraftItemStack) player.getInventory().getChestplate();
						}
						if(player.getInventory().getHelmet() instanceof CraftItemStack){
							helmet = (CraftItemStack) player.getInventory().getHelmet();
						}
						
						((CraftPlayer) ply).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(changeingName));
						
						if(hand != null){
							((CraftPlayer) ply).getHandle().netServerHandler.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 0, hand.getHandle()));
						}
						if(boots != null){
							((CraftPlayer) ply).getHandle().netServerHandler.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 1, boots.getHandle()));
						}
						if(leggings != null){
							((CraftPlayer) ply).getHandle().netServerHandler.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 2, leggings.getHandle()));
						}
						if(chest != null){
							((CraftPlayer) ply).getHandle().netServerHandler.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 3, chest.getHandle()));
						}
						if(helmet != null){
							((CraftPlayer) ply).getHandle().netServerHandler.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 4, helmet.getHandle()));
						}
//					}
//				});
			}
		}
		
		changeingName.name = oldName;
	}
	
	public void applyTeam(Player player, Player toPlayer, int team){
		EntityPlayer changeingName = ((CraftPlayer) player).getHandle();
		String oldName = player.getName();
		
		if(team == 1){
			changeingName.name = ChatColor.BLUE.toString() + player.getName();
		}
		else{
			changeingName.name = ChatColor.RED.toString() + player.getName();
		}
		
		CraftItemStack hand = null;
		CraftItemStack boots = null;
		CraftItemStack leggings = null;
		CraftItemStack chest = null;
		CraftItemStack helmet = null;
		if(player.getItemInHand() instanceof CraftItemStack){
			hand = (CraftItemStack) player.getItemInHand();
		}
		if(player.getInventory().getBoots() instanceof CraftItemStack){
			boots = (CraftItemStack) player.getInventory().getBoots();
		}
		if(player.getInventory().getLeggings() instanceof CraftItemStack){
			leggings = (CraftItemStack) player.getInventory().getLeggings();
		}
		if(player.getInventory().getChestplate() instanceof CraftItemStack){
			chest = (CraftItemStack) player.getInventory().getChestplate();
		}
		if(player.getInventory().getHelmet() instanceof CraftItemStack){
			helmet = (CraftItemStack) player.getInventory().getHelmet();
		}
		
		((CraftPlayer) toPlayer).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(changeingName));
		
		if(hand != null){
			((CraftPlayer) toPlayer).getHandle().netServerHandler.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 0, hand.getHandle()));
		}
		if(boots != null){
			((CraftPlayer) toPlayer).getHandle().netServerHandler.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 1, boots.getHandle()));
		}
		if(leggings != null){
			((CraftPlayer) toPlayer).getHandle().netServerHandler.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 2, leggings.getHandle()));
		}
		if(chest != null){
			((CraftPlayer) toPlayer).getHandle().netServerHandler.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 3, chest.getHandle()));
		}
		if(helmet != null){
			((CraftPlayer) toPlayer).getHandle().netServerHandler.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 4, helmet.getHandle()));
		}
		
		changeingName.name = oldName;
	}
	
	public void switchTeam(Minigame mgm, Player player){
		if(mgm.getBlueTeam().contains(player)){
			mgm.getBlueTeam().remove(player);
			mgm.addRedTeamPlayer(player);
		}
		else{
			mgm.getRedTeam().remove(player);
			mgm.addBlueTeamPlayer(player);
		}
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent event){
		Player ply = (Player) event.getEntity();
		if(pdata.getPlayersMinigame(ply) != null && mdata.getMinigame(pdata.getPlayersMinigame(ply)).getType().equals("teamdm") && ply.getKiller() != null && ply.getKiller() instanceof Player){
			int pteam = 0;
			if(mdata.getMinigame(pdata.getPlayersMinigame(ply)).getBlueTeam().contains(ply)){
				pteam = 1;
			}
			final Minigame mgm = mdata.getMinigame(pdata.getPlayersMinigame(ply));
			
			pdata.addPlayerKill(ply.getKiller());
			
			if(pteam == 0){
				mgm.incrementBlueTeamScore();
				if(mgm.getBlueTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
					endTeamMinigame(1, mgm);
				}
			}
			else{
				mgm.incrementRedTeamScore();
				if(mgm.getRedTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
					endTeamMinigame(0, mgm);
				}
			}
			
			if(pteam == 1){
				if(mgm.getRedTeam().size() < mgm.getBlueTeam().size() - 1){
					switchTeam(mgm, ply);
					ply.sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "You have been switched to " + ChatColor.RED + "Red Team");
					for(Player pl : mgm.getPlayers()){
						if(pl != ply){
							pl.sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + ply.getName() + " has been switched to " + ChatColor.RED + "Red Team");
						}
					}
				}
			}
			else{
				if(mgm.getBlueTeam().size() < mgm.getRedTeam().size()  - 1){
					switchTeam(mgm, ply);
					ply.sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "You have been switched to " + ChatColor.BLUE + "Blue Team");
					for(Player pl : mgm.getPlayers()){
						if(pl != ply){
							pl.sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + ply.getName() + " has been switched to " + ChatColor.BLUE + "Blue Team");
						}
					}
				}
			}
			
			for(Player pl : mgm.getPlayers()){
				pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Score: " + ChatColor.RED + mgm.getRedTeamScore() + ChatColor.WHITE + " to " + ChatColor.BLUE + mgm.getBlueTeamScore());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerRespawn(PlayerRespawnEvent event){
		final Player ply = event.getPlayer();
		if(pdata.getPlayersMinigame(ply) != null && mdata.getMinigame(pdata.getPlayersMinigame(ply)).getType().equals("teamdm")){
			int team = 0;
			Minigame mg = mdata.getMinigame(pdata.getPlayersMinigame(ply));
			if(mg.getBlueTeam().contains(ply)){
				team = 1;
			}
			List<Location> starts = new ArrayList<Location>();
			if(!mg.getStartLocationsBlue().isEmpty() && !mg.getStartLocationsRed().isEmpty()){
				if(team == 1){
					starts.addAll(mg.getStartLocationsBlue());
					Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
						final Player player = ply;
						@Override
						public void run() {
							if(pdata.getPlayersMinigame(ply) != null){
								applyTeam(player, 1);
							}
						}
					}, 40L);
				}
				else{
					starts.addAll(mg.getStartLocationsRed());
					Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
						final Player player = ply;
						@Override
						public void run() {
							if(pdata.getPlayersMinigame(ply) != null){
								applyTeam(player, 0);
							}
						}
					}, 40L);
				}
			}
			else{
				starts.addAll(mg.getStartLocations());
				if(team == 1){
					Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
						final Player player = ply;
						@Override
						public void run() {
							if(pdata.getPlayersMinigame(ply) != null){
								applyTeam(player, 1);
							}
						}
					}, 40L);
				}
				else{
					Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
						final Player player = ply;
						@Override
						public void run() {
							if(pdata.getPlayersMinigame(ply) != null){
								applyTeam(player, 0);
							}
						}
					}, 40L);
				}
			}
			Collections.shuffle(starts);
			event.setRespawnLocation(starts.get(0));
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					ply.setNoDamageTicks(100);
				}
			});
			
			if(!mg.getLoadout().isEmpty()){
				mdata.equiptLoadout(mg.getName(), event.getPlayer());
			}
			
			for(final Player play : mg.getBlueTeam()){
				Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
					final Player player = play;
					@Override
					public void run() {
						if(player != ply && pdata.getPlayersMinigame(player) != null){
							applyTeam(player, ply, 1);
						}
					}
				}, 100L);
			}
			
			for(final Player play : mg.getRedTeam()){
				Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
					final Player player = play;
					@Override
					public void run() {
						if(player != ply && pdata.getPlayersMinigame(player) != null){
							applyTeam(player, ply, 0);
						}
					}
				}, 100L);
			}
		}
	}
	
	@EventHandler
	public void friendlyPvP(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player){
			Player ply = (Player) event.getEntity();
			if(pdata.getPlayersMinigame(ply) != null && mdata.getMinigame(pdata.getPlayersMinigame(ply)).getType().equals("teamdm")){
				if(event.getDamager() instanceof Player){
					Player attacker = (Player) event.getDamager();
					if(pdata.getPlayersMinigame(attacker) != null && mdata.getMinigame(pdata.getPlayersMinigame(attacker)).getType().equals("teamdm")){
						Minigame mg = mdata.getMinigame(pdata.getPlayersMinigame(ply));
						int team = 0;
						int ateam = 0;
						if(mg.getBlueTeam().contains(ply)){
							team = 1;
						}
						
						if(mg.getBlueTeam().contains(attacker)){
							ateam = 1;
						}
						
						if(team == ateam){
							event.setCancelled(true);
						}
						else if(event.getDamage() >= ply.getHealth() && team != ateam){
							boolean end = false;
							if(ateam == 0){
								if(mg.getRedTeamScore() + 1 >= mg.getMaxScorePerPlayer(mg.getPlayers().size())){
									end = true;
								}
							}
							else{
								if(mg.getBlueTeamScore() + 1 >= mg.getMaxScorePerPlayer(mg.getPlayers().size())){
									end = true;
								}
							}
							if(end){
								for(Player pl : mg.getPlayers()){
									pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + attacker.getName() + " took the final kill against " + ply.getName());
								}
								if(ateam == 1){
									if(mg.getBlueTeamScore() + 1 >= mg.getMaxScorePerPlayer(mg.getPlayers().size())){
										event.setCancelled(true);
										mg.incrementBlueTeamScore();
										endTeamMinigame(1, mg);
									}
								}
								else{
									if(mg.getRedTeamScore() + 1 >= mg.getMaxScorePerPlayer(mg.getPlayers().size())){
										event.setCancelled(true);
										mg.incrementRedTeamScore();
										endTeamMinigame(0, mg);
									}
								}
							}
						}
					}
					else{
						event.setCancelled(true);
					}
				}
				else if(event.getDamager() instanceof Arrow){
					Arrow arrow = (Arrow) event.getDamager();
					if(arrow.getShooter() instanceof Player){
						Player attacker = (Player) arrow.getShooter();
						if(pdata.getPlayersMinigame(attacker) != null && mdata.getMinigame(pdata.getPlayersMinigame(attacker)).getType().equals("teamdm")){
							Minigame mg = mdata.getMinigame(pdata.getPlayersMinigame(ply));
							int team = 0;
							int ateam = 0;
							if(mg.getBlueTeam().contains(ply)){
								team = 1;
							}
							
							if(mg.getBlueTeam().contains(attacker)){
								ateam = 1;
							}
							
							if(team == ateam){
								event.setCancelled(true);
							}
							else if(event.getDamage() >= ply.getHealth() && team != ateam){
								boolean end = false;
								if(ateam == 0){
									if(mg.getRedTeamScore() + 1 >= mg.getMaxScorePerPlayer(mg.getPlayers().size())){
										end = true;
									}
								}
								else{
									if(mg.getBlueTeamScore() + 1 >= mg.getMaxScorePerPlayer(mg.getPlayers().size())){
										end = true;
									}
								}
								if(end){
									for(Player pl : mg.getPlayers()){
										pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + attacker.getName() + " took the final shot against " + ply.getName());
									}
									if(ateam == 1){
										if(mg.getBlueTeamScore() + 1 >= mg.getMaxScorePerPlayer(mg.getPlayers().size())){
											event.setCancelled(true);
											mg.incrementBlueTeamScore();
											endTeamMinigame(1, mg);
										}
									}
									else{
										if(mg.getRedTeamScore() + 1 >= mg.getMaxScorePerPlayer(mg.getPlayers().size())){
											event.setCancelled(true);
											mg.incrementRedTeamScore();
											endTeamMinigame(0, mg);
										}
									}
								}
							}
						}
						else{
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
}
