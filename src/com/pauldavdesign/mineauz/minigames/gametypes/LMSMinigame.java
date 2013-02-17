package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigameSave;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.SQLCompletionSaver;
import com.pauldavdesign.mineauz.minigames.events.TimerExpireEvent;

public class LMSMinigame extends MinigameType {
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	public LMSMinigame(){
		setLabel("lms");
	}
	
	@Override
	public boolean joinMinigame(Player player, Minigame mgm){
		return callLMSJoin(player, mgm, mgm.getDefaultGamemode());
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void quitMinigame(Player player, Minigame mgm, boolean forced){
		if(!mgm.getPlayers().isEmpty()){
			mgm.removePlayer(player);
			if(mgm.getPlayers().size() == 0){
				if(mgm.getMpTimer() != null){
					mgm.getMpTimer().pauseTimer();
					mgm.setMpTimer(null);
				}
				
				if(mgm.getMpBets() != null && (mgm.getMpTimer() == null || mgm.getMpTimer().getPlayerWaitTimeLeft() != 0)){
					if(mgm.getMpBets().getPlayersBet(player) != null){
						player.getInventory().addItem(mgm.getMpBets().getPlayersBet(player));
					}
					else if(mgm.getMpBets().getPlayersMoneyBet(player) != null){
						plugin.getEconomy().depositPlayer(player.getName(), mgm.getMpBets().getPlayersMoneyBet(player));
					}
					mgm.setMpBets(null);
				}
				
				if(mgm.getFloorDegenerator() != null){
					mgm.getFloorDegenerator().stopDegenerator();
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
					pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Waiting for " + (mgm.getMinPlayers() - 1) + " more players.");
				}
			}
		}
		
		callGeneralQuit(player);

		if(mgm.getMpBets() != null && (mgm.getMpTimer() == null || mgm.getMpTimer().getPlayerWaitTimeLeft() != 0)){
			if(mgm.getMpBets().getPlayersBet(player) != null){
				player.getInventory().addItem(mgm.getMpBets().getPlayersBet(player));
			}
			else if(mgm.getMpBets().getPlayersMoneyBet(player) != null){
				plugin.getEconomy().depositPlayer(player.getName(), mgm.getMpBets().getPlayersMoneyBet(player));
			}
			mgm.getMpBets().removePlayersBet(player);
		}
		player.updateInventory();
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void endMinigame(Player player, Minigame mgm){
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
		pdata.saveInventoryConfig();
		
		boolean hascompleted = false;
		Configuration completion = null;
		
		if(mgm.getFloorDegenerator() != null){
			mgm.getFloorDegenerator().stopDegenerator();
		}
		
		player.sendMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE + "You've won the " + minigame + " minigame. Congratulations!");
		if(plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
			plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE + player.getName() + " won " + mgm.getName());
		}
		
		if(mgm.getEndPosition() != null){
			player.teleport(mgm.getEndPosition());
		}

		mdata.getMinigame(minigame).removePlayer(player);
		
		if(mgm.getPlayers().isEmpty()){
			if(mgm.getMpTimer() != null){
				mdata.getMinigame(minigame).getMpTimer().pauseTimer();
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
						p.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You have been beaten! Bad luck!");
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
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent event){
		if(event.getEntity() instanceof Player){
			final Player ply = (Player) event.getEntity();
			if(pdata.playerInMinigame(ply)){
				String minigame = pdata.getPlayersMinigame(ply);
				Minigame mgm = mdata.getMinigame(minigame);
				
				String mgtype = mgm.getType();
				if(mgtype.equals("lms")){
					for(Player pl : mgm.getPlayers()){
						pl.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + event.getDeathMessage());
					}
					ply.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Bad Luck! Leaving the minigame.");
					if(ply.getKiller() != null){
						Player attacker = ply.getKiller();
						if(mgm.getName().equals(pdata.getPlayersMinigame(attacker))){
							pdata.addPlayerKill(attacker);
						}
					}
					
					if(!mgm.hasDeathDrops()){
						event.getDrops().clear();
					}
					
					pdata.partyMode(ply);
					event.setDeathMessage(null);
					ply.setHealth(2);
					
					pdata.quitMinigame(ply, false);
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						@Override
						public void run() {
							ply.setFireTicks(0);
						}
					});
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
			if(plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
				plugin.getServer().broadcastMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "The timer expired for " + event.getMinigame().getName());
			}
		}
	}
}
