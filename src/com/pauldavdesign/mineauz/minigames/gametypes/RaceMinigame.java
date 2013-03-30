package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
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
		Minigame minigame = pdata.getPlayersMinigame(player);
		if(!minigame.getPlayers().isEmpty()){
			minigame.removePlayer(player);
			if(minigame.getPlayers().size() == 0){
				if(mgm.getMpTimer() != null){
					mgm.getMpTimer().pauseTimer();
					mgm.getMpTimer().removeTimer();
					mgm.setMpTimer(null);
				}
				
				if(mgm.getMpBets() != null && (mgm.getMpTimer() == null || mgm.getMpTimer().getPlayerWaitTimeLeft() != 0)){
					if(mgm.getMpBets().getPlayersBet(player) != null){
						player.getInventory().addItem(mgm.getMpBets().getPlayersBet(player));
					}
					else if(mgm.getMpBets().getPlayersMoneyBet(player) != null){
						plugin.getEconomy().depositPlayer(player.getName(), mgm.getMpBets().getPlayersMoneyBet(player));
					}
				}
				mgm.setMpBets(null);
			}
			else if(minigame.getPlayers().size() == 1 && minigame.getMpTimer() != null && minigame.getMpTimer().getStartWaitTimeLeft() == 0 && !forced){
				pdata.endMinigame(minigame.getPlayers().get(0));
				
				if(minigame.getMpBets() != null){
					minigame.setMpBets(null);
				}
			}
			else if(minigame.getPlayers().size() < mgm.getMinPlayers() && minigame.getMpTimer() != null && minigame.getMpTimer().getStartWaitTimeLeft() != 0){
				minigame.getMpTimer().pauseTimer();
				mgm.getMpTimer().removeTimer();
				minigame.setMpTimer(null);
				for(Player pl : minigame.getPlayers()){
					pl.sendMessage(ChatColor.BLUE + "Waiting for 1 more player.");
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

	@SuppressWarnings("deprecation")
	@Override
	public void endMinigame(Player player, Minigame mgm) {
		Minigame minigame = pdata.getPlayersMinigame(player);
		
		if(minigame.getMpBets() != null){
			if(mgm.getMpBets().hasBets()){
				player.getInventory().addItem(minigame.getMpBets().claimBets());
				minigame.setMpBets(null);
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
		
		player.sendMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE + "You've finished the " + minigame + " minigame. Congratulations!");
		if(plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
			plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE + player.getName() + " won " + mgm.getName());
		}
		
		if(mgm.getEndPosition() != null){
			player.teleport(mgm.getEndPosition());
		}

		minigame.removePlayer(player);
		
		if(mgm.getPlayers().isEmpty()){
			minigame.getMpTimer().setStartWaitTime(0);
			
			minigame.setMpTimer(null);
			for(Player pl : minigame.getPlayers()){
				minigame.getPlayers().remove(pl);
			}
		}
		else{
			minigame.getMpTimer().setStartWaitTime(0);
			List<Player> players = new ArrayList<Player>();
			players.addAll(minigame.getPlayers());
			for(int i = 0; i < players.size(); i++){
				if(players.get(i) instanceof Player){
					Player p = players.get(i);
					if(!p.getName().equals(player.getName())){
						p.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You have been beaten! Bad luck!");
						pdata.quitMinigame(p, true);
					}
				}
				else{
					players.remove(i);
				}
			}
			minigame.setMpTimer(null);
			for(Player pl : players){
				minigame.getPlayers().remove(pl);
			}
		}

		player.setFireTicks(0);
		
		
		plugin.getLogger().info(player.getName() + " completed " + minigame);
		
		if(plugin.getSQL() == null){
			completion = mdata.getConfigurationFile("completion");
			hascompleted = completion.getStringList(minigame.getName()).contains(player.getName());
			
			if(plugin.getSQL() == null){
				if(!completion.getStringList(minigame.getName()).contains(player.getName())){
					List<String> completionlist = completion.getStringList(minigame.getName());
					completionlist.add(player.getName());
					completion.set(minigame.getName(), completionlist);
					MinigameSave completionsave = new MinigameSave("completion");
					completionsave.getConfig().set(minigame.getName(), completionlist);
					completionsave.saveConfig();
				}
			}
			
			issuePlayerRewards(player, mgm, hascompleted);
		}
		else{
			new SQLCompletionSaver(minigame.getName(), player, this);
		}
	}
	/*----------------*/
	/*-----EVENTS-----*/
	/*----------------*/
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event){
		if(pdata.playerInMinigame(event.getPlayer())){
			Minigame mgm = pdata.getPlayersMinigame(event.getPlayer());
			if(mgm.hasPlayers()){
				String mgtype = mgm.getType();
				if(mgtype.equals("race")){
					event.setRespawnLocation(pdata.getPlayerCheckpoint(event.getPlayer()));
					event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Bad Luck! Returning to checkpoint.");
					
					mgm.getPlayersLoadout(event.getPlayer()).equiptLoadout(event.getPlayer());
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
