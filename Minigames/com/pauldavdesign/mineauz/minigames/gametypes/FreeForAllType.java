package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.MultiplayerTimer;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.events.TimerExpireEvent;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class FreeForAllType extends MinigameTypeBase{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	public FreeForAllType() {
		setType(MinigameType.FREE_FOR_ALL);
	}

	@Override
	public boolean joinMinigame(MinigamePlayer player, Minigame mgm) {
		if(!mgm.canInteractPlayerWait())
			player.setCanInteract(false);
		if(!mgm.canMovePlayerWait())
			player.setFrozen(true);
		
		if(!mgm.isNotWaitingForPlayers()){
			if(mgm.getMpTimer() == null && mgm.getPlayers().size() == mgm.getMinPlayers()){
				mgm.setMpTimer(new MultiplayerTimer(mgm));
				mgm.getMpTimer().startTimer();
				if(mgm.getPlayers().size() == mgm.getMaxPlayers()){
					mgm.getMpTimer().setPlayerWaitTime(0);
					mdata.sendMinigameMessage(mgm, MinigameUtils.getLang("minigame.skipWaitTime"), "info", null);
				}
			}
			else if(mgm.getMpTimer() != null && mgm.getPlayers().size() == mgm.getMaxPlayers()){
				mgm.getMpTimer().setPlayerWaitTime(0);
				mdata.sendMinigameMessage(mgm, MinigameUtils.getLang("minigame.skipWaitTime"), "info", null);
			}
			else if(mgm.getMpTimer() == null){
				int neededPlayers = mgm.getMinPlayers() - mgm.getPlayers().size();
				mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("minigame.waitingForPlayers", neededPlayers), null, null);
			}
		}
		else{
			player.setLatejoining(true);
			player.sendMessage(MinigameUtils.formStr("minigame.lateJoin", 5)); //TODO: Late join delay variable
			final MinigamePlayer fply = player;
			final Minigame fmgm = mgm;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					if(fply.isInMinigame()){
						List<Location> locs = new ArrayList<Location>(fmgm.getStartLocations());
						Collections.shuffle(locs);
						fply.teleport(locs.get(0));
						fply.getLoadout().equiptLoadout(fply);
						fply.setLatejoining(false);
						fply.setFrozen(false);
						fply.setCanInteract(true);
					}
				}
			}, 5 * 20); //TODO: Latejoin variable

			player.getPlayer().setScoreboard(mgm.getScoreboardManager());
			mgm.setScore(player, 1);
			mgm.setScore(player, 0);
		}
		return true;
	}

	@Override
	public void quitMinigame(MinigamePlayer player, Minigame mgm, boolean forced) {
		if(mgm.getMpBets() != null && (mgm.getMpTimer() == null || mgm.getMpTimer().getPlayerWaitTimeLeft() != 0)){
			if(mgm.getMpBets().getPlayersBet(player) != null){
				final ItemStack item = mgm.getMpBets().getPlayersBet(player).clone();
				final MinigamePlayer ply = player;
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						ply.getPlayer().getInventory().addItem(item);
					}
				});
			}
			else if(mgm.getMpBets().getPlayersMoneyBet(player) != null){
				plugin.getEconomy().depositPlayer(player.getName(), mgm.getMpBets().getPlayersMoneyBet(player));
			}
			mgm.getMpBets().removePlayersBet(player);
		}
		
		if(mgm.getPlayers().size() - 1 < mgm.getMinPlayers() && mgm.getMpTimer() != null && mgm.getMpTimer().getStartWaitTimeLeft() != 0){
			mgm.getMpTimer().pauseTimer();
			mgm.getMpTimer().removeTimer();
			mgm.setMpTimer(null);
			mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("minigame.waitingForPlayers", 1), null, null);
		}
	}
	
	@Override
	public void endMinigame(List<MinigamePlayer> winners, List<MinigamePlayer> losers, Minigame mgm) {
		if(mgm.getMpTimer() == null) return;
		mgm.getMpTimer().setStartWaitTime(0);
		mgm.setMpTimer(null);
	}
	
	/*----------------*/
	/*-----EVENTS-----*/
	/*----------------*/
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerRespawn(PlayerRespawnEvent event){
		final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply.isInMinigame() && ply.getMinigame().getType() == MinigameType.FREE_FOR_ALL){
			Minigame mg = ply.getMinigame();
			Location respawnPos;
			if(mg.hasStarted() && !ply.isLatejoining()){
				if(mg.isAllowedMPCheckpoints() && ply.hasCheckpoint()){
					respawnPos = ply.getCheckpoint();
				}
				else{
					List<Location> starts = new ArrayList<Location>();
					starts.addAll(mg.getStartLocations());
					Collections.shuffle(starts);
					respawnPos = starts.get(0);
				}
				
				ply.getLoadout().equiptLoadout(ply);
			}
			else{
				respawnPos = mg.getLobbyPosition();
			}
				
			event.setRespawnLocation(respawnPos);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					ply.getPlayer().setNoDamageTicks(60);
				}
			});
			
		}
	}
	
	@EventHandler
	public void timerExpire(TimerExpireEvent event){
		if(event.getMinigame().getType() == MinigameType.FREE_FOR_ALL){
			MinigamePlayer player = null;
			int score = 0;
			for(MinigamePlayer ply : event.getMinigame().getPlayers()){
				if(ply.getScore() > 0){
					if(ply.getScore() > score){
						player = ply;
						score = ply.getScore();
					}
					else if(ply.getScore() == score){
						if(player != null && ply.getDeaths() < player.getDeaths()){
							player = ply;
						}
						else if(player == null){
							player = ply;
						}
					}
				}
			}
			List<MinigamePlayer> losers = new ArrayList<MinigamePlayer>();
			losers.addAll(event.getMinigame().getPlayers());
			List<MinigamePlayer> winners = new ArrayList<MinigamePlayer>();
			if(player != null){
				losers.remove(player);
				winners.add(player);
			}
			
			pdata.endMinigame(event.getMinigame(), winners, losers);
		}
	}
}
