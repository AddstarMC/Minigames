package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigameSave;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.SQLCompletionSaver;
import com.pauldavdesign.mineauz.minigames.events.TimerExpireEvent;

public class DMMinigame extends MinigameType{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	public DMMinigame() {
		setLabel("dm");
	}

	@Override
	public boolean joinMinigame(Player player, Minigame mgm) {
		return callLMSJoin(player, mgm, mgm.getDefaultGamemode());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void quitMinigame(Player player, Minigame mgm, boolean forced) {
		if(!mgm.getPlayers().isEmpty()){
			mgm.removePlayer(player);
			if(mgm.getPlayers().size() == 0){
				if(mgm.getMpTimer() != null){
					mgm.getMpTimer().pauseTimer();
					mgm.setMpTimer(null);
				}
				
				if(mgm.getMpBets() != null){
					if(mgm.getMpBets().getPlayersBet(player) != null){
						player.getInventory().addItem(mgm.getMpBets().getPlayersBet(player));
					}
					else{
						plugin.getEconomy().depositPlayer(player.getName(), mgm.getMpBets().getPlayersMoneyBet(player));
					}
					mgm.setMpBets(null);
				}
			}
			else if(mgm.getPlayers().size() == 1 && mgm.getMpTimer() != null && mgm.getMpTimer().getStartWaitTimeLeft() == 0 && !forced){
				pdata.endMinigame(mgm.getPlayers().get(0));
				
				if(mgm.getMpBets() != null){
					mgm.setMpBets(null);
				}
			}
			else if(mgm.getPlayers().size() < mgm.getMinPlayers() && mgm.getMpTimer() != null && mgm.getMpTimer().getStartWaitTimeLeft() != 0){
				mgm.getMpTimer().pauseTimer();
				mgm.setMpTimer(null);
				for(Player pl : mgm.getPlayers()){
					pl.sendMessage(ChatColor.BLUE + "Waiting for " + (mgm.getMinPlayers() - 1) + " more players.");
				}
			}
		}
		
		callGeneralQuit(player);

		if(mgm.getMpBets() != null){
			if(mgm.getMpBets().getPlayersBet(player) != null){
				player.getInventory().addItem(mgm.getMpBets().getPlayersBet(player));
			}
			else{
				plugin.getEconomy().depositPlayer(player.getName(), mgm.getMpBets().getPlayersMoneyBet(player));
			}
			mgm.getMpBets().removePlayersBet(player);
		}
		player.updateInventory();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void endMinigame(Player player, Minigame mgm) {
		String minigame = pdata.getPlayersMinigame(player);
		
		if(mdata.getMinigame(minigame).getMpBets() != null){
			if(mgm.getMpBets().hasBets()){
				player.getInventory().addItem(mdata.getMinigame(minigame).getMpBets().claimBets());
				mdata.getMinigame(minigame).setMpBets(null);
				player.updateInventory();
			}
			else{
				plugin.getEconomy().depositPlayer(player.getName(), mgm.getMpBets().claimMoneyBets());
				player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You won $" + mgm.getMpBets().claimMoneyBets());
				mgm.setMpBets(null);
			}
		}
		//pdata.saveItems(player);
		pdata.saveInventoryConfig();
		
		boolean hascompleted = false;
		Configuration completion = null;
		
		player.sendMessage(ChatColor.GREEN + "You've won the " + minigame + " minigame. Congratulations!");
		if(plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
			plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE + player.getName() + " won " + mgm.getName() + ". Score: " + pdata.getPlayerKills(player));
		}
		
		if(mgm.getEndPosition() != null){
			player.teleport(mgm.getEndPosition());
		}

		mdata.getMinigame(minigame).removePlayer(player);
		
		if(mgm.getPlayers().isEmpty()){
			mdata.getMinigame(minigame).getMpTimer().setStartWaitTime(0);
			
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
		
//		if(mgm.hasRestoreBlocks()){
//			Set<String> blocks = mgm.getRestoreBlocks().keySet();
//			
//			for(String name : blocks){
//				String mat = mgm.getRestoreBlocks().get(name).getBlock().toString();
//				if(mat.equalsIgnoreCase("CHEST") || mat.equalsIgnoreCase("FURNACE") || mat.equalsIgnoreCase("DISPENSER")){
//					Location loc = mgm.getRestoreBlocks().get(name).getLocation();
//					
//					if(loc.getBlock().getType() != Material.getMaterial(mat)){
//						loc.getBlock().setType(Material.getMaterial(mat));
//					}
//					
//					if(loc.getBlock().getState() instanceof Chest){
//						Chest chest = (Chest) loc.getBlock().getState();
//						chest.getInventory().clear();
//						chest.getInventory().setContents(mgm.getRestoreBlocks().get(name).getItems());
//					}
//					else if(loc.getBlock().getState() instanceof Furnace){
//						Furnace furnace = (Furnace) loc.getBlock().getState();
//						furnace.getInventory().clear();
//						furnace.getInventory().setContents(mgm.getRestoreBlocks().get(name).getItems());
//					}
//					else if(loc.getBlock().getState() instanceof Dispenser){
//						Dispenser dispenser = (Dispenser) loc.getBlock().getState();
//						dispenser.getInventory().clear();
//						dispenser.getInventory().setContents(mgm.getRestoreBlocks().get(name).getItems());
//					}
//				}
//			}
//		}

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
	
	/*----------------*/
	/*-----EVENTS-----*/
	/*----------------*/

//	@EventHandler
//	public void playerDeath(PlayerDeathEvent event){
//		Player ply = (Player) event.getEntity();
//		if(pdata.getPlayersMinigame(ply) != null && mdata.getMinigame(pdata.getPlayersMinigame(ply)).getType().equals("dm") && ply.getKiller() != null && ply.getKiller() instanceof Player){
//			Minigame mgm = mdata.getMinigame(pdata.getPlayersMinigame(ply));
//			
//			pdata.addPlayerKill(ply.getKiller());
//			
//			if(mgm.getMaxScore() != 0 && pdata.getPlayerKills(ply.getKiller()) >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
//				for(Player pl : mgm.getPlayers()){
//					if(pl != ply.getKiller()){
//						pdata.quitMinigame(pl, false);
//					}
//				}
//			}
//			else{
//				for(Player pl : mgm.getPlayers()){
//					pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + ply.getKiller().getName() + "'s Score: " + pdata.getPlayerKills(ply.getKiller()));
//				}
//			}
//		}
//	}
	
//	@EventHandler
//	public void playerAttack(EntityDamageByEntityEvent event){
//		if(event.getEntity() instanceof Player){
//			Player ply = (Player) event.getEntity();
//			if(pdata.getPlayersMinigame(ply) != null && mdata.getMinigame(pdata.getPlayersMinigame(ply)).getType().equals("dm") && event.getDamage() >= ply.getHealth()){
//				Player attacker = null;
//				if(event.getDamager() instanceof Player){
//					attacker = (Player) event.getDamager();
//				}
//				else if(event.getDamager() instanceof Arrow){
//					Arrow arr = (Arrow) event.getDamager();
//					if(arr.getShooter() instanceof Player){
//						attacker = (Player) arr.getShooter();
//					}
//					else{
//						return;
//					}
//				}
//				else{
//					return;
//				}
//				Minigame mgm = mdata.getMinigame(pdata.getPlayersMinigame(ply));
//				
//				if(!pdata.getPlayersMinigame(ply).equals(pdata.getPlayersMinigame(attacker))){
//					return;
//				}
//				
//				if(mgm.getMaxScore() != 0 && pdata.getPlayerKills(attacker) + 1 >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
//					event.setCancelled(true);
//					pdata.addPlayerKill(attacker);
//					List<Player> conPlayers = new ArrayList<Player>();
//					conPlayers.addAll(mgm.getPlayers());
//					conPlayers.remove(attacker);
//					for(Player pl : conPlayers){
//						if(pl != attacker){
//							pdata.quitMinigame(pl, false);
//						}
//					}
//				}
//			}
//		}
//	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerRespawn(PlayerRespawnEvent event){
		final Player ply = event.getPlayer();
		if(pdata.getPlayersMinigame(ply) != null && mdata.getMinigame(pdata.getPlayersMinigame(ply)).getType().equals("dm")){
			Minigame mg = mdata.getMinigame(pdata.getPlayersMinigame(ply));
			List<Location> starts = new ArrayList<Location>();
			
			starts.addAll(mg.getStartLocations());
			Collections.shuffle(starts);
			event.setRespawnLocation(starts.get(0));
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					ply.setNoDamageTicks(100);
				}
			});
			
			mg.getLoadout(mg.getPlayersLoadout(event.getPlayer())).equiptLoadout(event.getPlayer());
		}
	}
	
	@EventHandler
	public void timerExpire(TimerExpireEvent event){
		if(event.getMinigame().getType().equals(getLabel())){
			Player player = null;
			int score = 0;
			for(Player ply : event.getMinigame().getPlayers()){
				if(pdata.getPlayerKills(ply) > score){
					player = ply;
					score = pdata.getPlayerKills(ply);
				}
				else if(pdata.getPlayerKills(ply) == score){
					if(player != null && pdata.getPlayerDeath(ply) < pdata.getPlayerDeath(player)){
						player = ply;
					}
					else if(player == null){
						player = ply;
					}
				}
			}
			List<Player> players = new ArrayList<Player>();
			players.addAll(event.getMinigame().getPlayers());
			
			for(Player ply : players){
				if(ply != player){
					pdata.quitMinigame(ply, true);
				}
			}
			pdata.endMinigame(player);
		}
	}
}
