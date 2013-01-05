package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
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

public class RaceMinigame extends MinigameType{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	public RaceMinigame() {
		setLabel("race");
	}

	@Override
	public boolean joinMinigame(Player player, Minigame mgm) {
		return callLMSJoin(player, mgm, mgm.getDefaultGamemode());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void quitMinigame(Player player, Minigame mgm, boolean forced) {
		String minigame = pdata.getPlayersMinigame(player);
		if(!mdata.getMinigame(minigame).getPlayers().isEmpty()){
			mdata.getMinigame(minigame).removePlayer(player);
			if(mdata.getMinigame(minigame).getPlayers().size() == 0){
				if(mdata.getMinigame(minigame).getMpTimer() != null){
					mdata.getMinigame(minigame).getMpTimer().setStartWaitTime(0);
					if(mdata.getMinigame(minigame).getMpBets() != null){
						player.getInventory().addItem(mdata.getMinigame(minigame).getMpBets().getPlayersBet(player));
						mdata.getMinigame(minigame).setMpBets(null);
					}
					mdata.getMinigame(minigame).setMpTimer(null);
				}
			}
//			else if(mdata.getMinigame(minigame).getPlayers().size() == 1 && mdata.getMinigame(minigame).getMpTimer() != null && mdata.getMinigame(minigame).getMpTimer().getStartWaitTimeLeft() == 0){
//				//pdata.endMinigame(mdata.getMinigame(minigame).getPlayers().get(0));
//				
//				if(mdata.getMinigame(minigame).getMpBets() != null){
//					mdata.getMinigame(minigame).setMpBets(null);
//				}
//			}
			else if(mdata.getMinigame(minigame).getPlayers().size() < mgm.getMinPlayers() && mdata.getMinigame(minigame).getMpTimer() != null && mdata.getMinigame(minigame).getMpTimer().getStartWaitTimeLeft() != 0){
				mdata.getMinigame(minigame).getMpTimer().pauseTimer();
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
	@Override
	public void endMinigame(Player player, Minigame mgm) {
		String minigame = pdata.getPlayersMinigame(player);
		
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
	/*----------------*/
	/*-----EVENTS-----*/
	/*----------------*/
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event){
		if(pdata.playerInMinigame(event.getPlayer())){
			String minigame = pdata.getPlayersMinigame(event.getPlayer());
			Minigame mgm = mdata.getMinigame(minigame);
			if(mdata.getMinigame(minigame).hasPlayers()){
				String mgtype = mgm.getType();
				if(mgtype.equals("race")){
					event.setRespawnLocation(pdata.getPlayerCheckpoint(event.getPlayer()));
					event.getPlayer().sendMessage(ChatColor.GRAY + "Bad Luck! Returning to checkpoint.");
					
					
					mgm.getLoadout(mgm.getPlayersLoadout(event.getPlayer())).equiptLoadout(event.getPlayer());
//					if(mgm.hasDefaultLoadout()){
//						mgm.getDefaultPlayerLoadout().equiptLoadout(event.getPlayer());
//					}
				}
			}
		}
	}
	
	@EventHandler
	public void timerExpire(TimerExpireEvent event){
		if(event.getMinigame().getType().equals(getLabel())){
			List<Player> players = new ArrayList<Player>();
			players.addAll(event.getMinigame().getPlayers());
			for(Player ply : players){
				ply.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "The timer has expired!");
				pdata.quitMinigame(ply, true);
			}
		}
	}
}
