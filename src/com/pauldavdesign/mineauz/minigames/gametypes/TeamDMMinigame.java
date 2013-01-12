package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.server.v1_4_6.EntityPlayer;
import net.minecraft.server.v1_4_6.Packet20NamedEntitySpawn;
import net.minecraft.server.v1_4_6.Packet5EntityEquipment;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_4_6.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigameSave;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.MultiplayerTimer;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.events.TimerExpireEvent;

public class TeamDMMinigame extends MinigameType{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	public TeamDMMinigame() {
		setLabel("teamdm");
	}
	
	@Override
	public boolean joinMinigame(final Player player, Minigame mgm){
		if(mgm.getQuitPosition() != null && mgm.isEnabled() && mgm.getEndPosition() != null && mgm.getLobbyPosition() != null){
			
			int redSize = mgm.getRedTeam().size();
			int blueSize = mgm.getBlueTeam().size();
			
			player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
			player.setAllowFlight(false);
			plugin.getLogger().info(player.getName() + " started " + mgm.getName());
			
			Location lobby = mgm.getLobbyPosition();
			
			String gametype = mgm.getType();
			
			if(!mgm.getPlayers().isEmpty() && mgm.getPlayers().size() < mgm.getMaxPlayers()){
				if(mgm.getMpTimer() == null || mgm.getMpTimer().getPlayerWaitTimeLeft() != 0){
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
					
					pdata.storePlayerData(player, mgm.getDefaultGamemode());
					
					player.teleport(lobby);
					pdata.addPlayerMinigame(player, mgm.getName());
					player.sendMessage(ChatColor.GREEN + "You have started a " + gametype + " minigame, type /minigame quit to exit.");
				
					if(mgm.getMpTimer() == null && mgm.getPlayers().size() >= mgm.getMinPlayers()){
						mgm.setMpTimer(new MultiplayerTimer(mgm.getName()));
						mgm.getMpTimer().start();
					}
					else if(mgm.getMpTimer() != null && mgm.getMpTimer().isPaused() && 
							(mgm.getBlueTeam().size() == mgm.getRedTeam().size() || 
							mgm.getBlueTeam().size() + 1 == mgm.getRedTeam().size() || 
							mgm.getBlueTeam().size() == mgm.getRedTeam().size() + 1)){
						mgm.getMpTimer().resumeTimer();
					}
					else{
						int neededPlayers = mgm.getMinPlayers() - mgm.getPlayers().size();
						if(neededPlayers == 1){
							player.sendMessage(ChatColor.BLUE + "Waiting for 1 more player.");
						}
						else if(neededPlayers > 1){
							player.sendMessage(ChatColor.BLUE + "Waiting for " + neededPlayers + " more players.");
						}
					}

					List<Player> plys = pdata.playersInMinigame();
					for(Player ply : plys){
						if(mgm.getName().equals(pdata.getPlayersMinigame(ply)) && !ply.getName().equals(player.getName())){
							String teamColour = ChatColor.RED + "Red";
							if(mgm.getBlueTeam().contains(player)){
								teamColour = ChatColor.BLUE + "Blue";
							}
							ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + player.getName() + " has joined " + teamColour + ChatColor.WHITE + " team!");
						}
					}
					return true;
				}
				else if(mgm.getMpTimer().getPlayerWaitTimeLeft() == 0){
					player.sendMessage(ChatColor.RED + "The minigame has already started. Try again soon.");
					return false;
				}
			}
			else if(mgm.getPlayers().isEmpty()){
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
						((CraftPlayer) ply).getHandle().playerConnection.sendPacket(new Packet20NamedEntitySpawn(changeingName));
					}
				}
				
				changeingName.name = oldName;
				
				pdata.storePlayerData(player, GameMode.ADVENTURE);
				
				player.teleport(lobby);
				pdata.addPlayerMinigame(player, mgm.getName());
				player.sendMessage(ChatColor.GREEN + "You have started a " + gametype + " minigame, type /minigame quit to exit.");
				
				int neededPlayers = mgm.getMinPlayers() - 1;
				
				if(neededPlayers > 0){
					player.sendMessage(ChatColor.BLUE + "Waiting for " + neededPlayers + " more players.");
				}
				else
				{
					mgm.setMpTimer(new MultiplayerTimer(mgm.getName()));
					mgm.getMpTimer().start();
				}

				List<Player> plys = pdata.playersInMinigame();
				for(Player ply : plys){
					if(mgm.getName().equals(pdata.getPlayersMinigame(ply)) && !ply.getName().equals(player.getName())){
						String teamColour = ChatColor.RED + "Red";
						if(mgm.getBlueTeam().contains(player)){
							teamColour = ChatColor.BLUE + "Blue";
						}
						ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + player.getName() + " has joined " + teamColour + ChatColor.WHITE + " team!");
					}
				}
				return true;
			}
			else if(mgm.getPlayers().size() == mgm.getMaxPlayers()){
				player.sendMessage(ChatColor.RED + "Sorry, this minigame is full.");
				return false;
			}
		}
		return false;
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
//					mdata.getMinigame(minigame).getMpTimer().setStartWaitTime(0);
					mgm.getMpTimer().pauseTimer();
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
					pdata.endTeamMinigame(1, mgm);
				}
				else{
					pdata.endTeamMinigame(0, mgm);
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
				mdata.getMinigame(minigame).getMpTimer().pauseTimer();
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
				((CraftPlayer) ply).getHandle().playerConnection.sendPacket(new Packet20NamedEntitySpawn(changeingName));
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
				((CraftPlayer) ply).getHandle().playerConnection.sendPacket(new Packet20NamedEntitySpawn(changeingName));
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
	}
	
//	public void endTeamMinigame(int teamnum, Minigame mgm){
//		
//		List<Player> losers = null;
//		List<Player> winners = null;
//		
//		if(teamnum == 1){
//			//Blue team
//			losers = mgm.getRedTeam();
//			winners = mgm.getBlueTeam();
//			if(plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
//				plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.BLUE + "Blue Team" + ChatColor.WHITE + " won " + mgm.getName() + ", " + ChatColor.BLUE + mgm.getBlueTeamScore() + ChatColor.WHITE + " to " + ChatColor.RED + mgm.getRedTeamScore());
//			}
//		}
//		else{
//			//Red team
//			losers = mgm.getBlueTeam();
//			winners = mgm.getRedTeam();
//			if(plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
//				plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.RED + "Red Team" + ChatColor.WHITE + " won " + mgm.getName() + ", " + ChatColor.RED + mgm.getRedTeamScore() + ChatColor.WHITE + " to " + ChatColor.BLUE + mgm.getBlueTeamScore());
//			}
//		}
//		
//		mgm.setRedTeamScore(0);
//		mgm.setBlueTeamScore(0);
//		
//		mgm.getMpTimer().setStartWaitTime(0);
//		mgm.setMpTimer(null);
//		
//		List<Player> winplayers = new ArrayList<Player>();
//		winplayers.addAll(winners);
//
//		if(plugin.getSQL() != null){
//			new SQLCompletionSaver(mgm.getName(), winplayers, this);
//		}
//		
//		if(!losers.isEmpty()){
//			List<Player> loseplayers = new ArrayList<Player>();
//			loseplayers.addAll(losers);
//			for(int i = 0; i < loseplayers.size(); i++){
//				if(loseplayers.get(i) instanceof Player){
//					final Player p = loseplayers.get(i);
//					
//					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//
//						@Override
//						public void run() {
//							p.sendMessage(ChatColor.RED + "You have been beaten! Bad luck!");
//							pdata.quitMinigame(p, false);
//						}
//					});
//				}
//				else{
//					loseplayers.remove(i);
//				}
//			}
//			mgm.setMpTimer(null);
//			for(Player pl : loseplayers){
//				mgm.getPlayers().remove(pl);
//			}
//		}
//		
//		for(int i = 0; i < winplayers.size(); i++){
//			if(winplayers.get(i) instanceof Player){
//				final Player p = winplayers.get(i);
//				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//					
//					@Override
//					public void run() {
//						pdata.endMinigame(p);
//					}
//				});
//			}
//			else{
//				winplayers.remove(i);
//			}
//		}
//	}
	
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
				
				//((CraftPlayer) ply).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(changeingName));
				((CraftPlayer) ply).getHandle().playerConnection.sendPacket(new Packet20NamedEntitySpawn(changeingName));
				
				if(hand != null){
					((CraftPlayer) ply).getHandle().playerConnection.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 0, CraftItemStack.asNMSCopy(hand)));
				}
				if(boots != null){
					((CraftPlayer) ply).getHandle().playerConnection.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 1, CraftItemStack.asNMSCopy(boots)));
				}
				if(leggings != null){
					((CraftPlayer) ply).getHandle().playerConnection.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 2, CraftItemStack.asNMSCopy(leggings)));
				}
				if(chest != null){
					((CraftPlayer) ply).getHandle().playerConnection.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 3, CraftItemStack.asNMSCopy(chest)));
				}
				if(helmet != null){
					((CraftPlayer) ply).getHandle().playerConnection.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 4, CraftItemStack.asNMSCopy(helmet)));
				}
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
		
		((CraftPlayer) toPlayer).getHandle().playerConnection.sendPacket(new Packet20NamedEntitySpawn(changeingName));
		
		if(hand != null){
			((CraftPlayer) toPlayer).getHandle().playerConnection.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 0, CraftItemStack.asNMSCopy(hand)));
		}
		if(boots != null){
			((CraftPlayer) toPlayer).getHandle().playerConnection.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 1, CraftItemStack.asNMSCopy(boots)));
		}
		if(leggings != null){
			((CraftPlayer) toPlayer).getHandle().playerConnection.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 2, CraftItemStack.asNMSCopy(leggings)));
		}
		if(chest != null){
			((CraftPlayer) toPlayer).getHandle().playerConnection.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 3, CraftItemStack.asNMSCopy(chest)));
		}
		if(helmet != null){
			((CraftPlayer) toPlayer).getHandle().playerConnection.sendPacket(new Packet5EntityEquipment(player.getEntityId(), 4, CraftItemStack.asNMSCopy(helmet)));
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
	
	/*----------------*/
	/*-----EVENTS-----*/
	/*----------------*/
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent event){
		Player ply = (Player) event.getEntity();
		if(pdata.getPlayersMinigame(ply) != null && mdata.getMinigame(pdata.getPlayersMinigame(ply)).getType().equals("teamdm") && ply.getKiller() != null && ply.getKiller() instanceof Player){
			int pteam = 0;
			if(mdata.getMinigame(pdata.getPlayersMinigame(ply)).getBlueTeam().contains(ply)){
				pteam = 1;
			}
			final Minigame mgm = mdata.getMinigame(pdata.getPlayersMinigame(ply));
			
//			pdata.addPlayerKill(ply.getKiller());
//			
//			if(pteam == 0){
//				mgm.incrementBlueTeamScore();
//				if(mgm.getMaxScore() != 0 && mgm.getBlueTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
//					pdata.endTeamMinigame(1, mgm);
//				}
//			}
//			else{
//				mgm.incrementRedTeamScore();
//				if(mgm.getMaxScore() != 0 && mgm.getRedTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
//					pdata.endTeamMinigame(0, mgm);
//				}
//			}
			
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
			
//			for(Player pl : mgm.getPlayers()){
//				pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Score: " + ChatColor.RED + mgm.getRedTeamScore() + ChatColor.WHITE + " to " + ChatColor.BLUE + mgm.getBlueTeamScore());
//			}
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
			
			mg.getLoadout(mg.getPlayersLoadout(event.getPlayer())).equiptLoadout(event.getPlayer());
//			if(mg.hasDefaultLoadout()){
//				mg.getDefaultPlayerLoadout().equiptLoadout(event.getPlayer());
//			}
			
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
						}
						else{
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void timerExpire(TimerExpireEvent event){
		if(event.getMinigame().getType().equals(getLabel())){
			if(event.getMinigame().getBlueTeamScore() > event.getMinigame().getRedTeamScore()){
				pdata.endTeamMinigame(1, event.getMinigame());
			}
			else{
				pdata.endTeamMinigame(0, event.getMinigame());
			}
		}
	}
}
