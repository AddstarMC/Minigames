package com.pauldavdesign.mineauz.minigames.minigame.regions;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.events.EndMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.JoinMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.QuitMinigameEvent;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.modules.RegionModule;

public class RegionEvents implements Listener{
	
	private Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	
	private void executeRegionChanges(Minigame mg, MinigamePlayer ply){
		for(Region r : RegionModule.getMinigameModule(mg).getRegions()){
			if(r.playerInRegion(ply)){
				if(!r.hasPlayer(ply)){
					r.addPlayer(ply);
					r.execute(RegionTrigger.ENTER, ply);
				}
			}
			else{
				if(r.hasPlayer(ply)){
					r.removePlayer(ply);
					r.execute(RegionTrigger.LEAVE, ply);
				}
			}
		}
	}
	
	@EventHandler
	private void playerMove(PlayerMoveEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame()){
			Minigame mg = ply.getMinigame();
			executeRegionChanges(mg, ply);
		}
	}
	
	@EventHandler
	private void playerSpawn(PlayerRespawnEvent event){
		final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame()){
			final Minigame mg = ply.getMinigame();
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					executeRegionChanges(mg, ply);
				}
			});
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void playerJoin(JoinMinigameEvent event){
		final MinigamePlayer ply = event.getMinigamePlayer();
		if(ply == null) return;
		final Minigame mg = event.getMinigame();
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				executeRegionChanges(mg, ply);
			}
		});
	}
	
	@EventHandler
	private void playerQuit(QuitMinigameEvent event){
		MinigamePlayer ply = event.getMinigamePlayer();
		if(ply == null) return;
		Minigame mg = ply.getMinigame();
		for(Region r : RegionModule.getMinigameModule(mg).getRegions()){
			if(r.hasPlayer(ply))
				r.removePlayer(ply);
		}
	}
	
	@EventHandler
	private void playersEnd(EndMinigameEvent event){
		for(MinigamePlayer ply : event.getWinners()){
			Minigame mg = ply.getMinigame();
			for(Region r : RegionModule.getMinigameModule(mg).getRegions()){
				if(r.hasPlayer(ply))
					r.removePlayer(ply);
			}
		}
	}
}
