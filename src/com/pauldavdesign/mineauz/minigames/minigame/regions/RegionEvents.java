package com.pauldavdesign.mineauz.minigames.minigame.regions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.modules.RegionModule;

public class RegionEvents implements Listener{
	
	private Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	
	@EventHandler
	private void playerMove(PlayerMoveEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame()){
			Minigame mg = ply.getMinigame();
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
	}
	
	@EventHandler
	private void playerDeath(PlayerDeathEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
		if(ply == null) return;
		if(ply.isInMinigame()){
//			Minigame mg = ply.getMinigame();
//			for(Region r : RegionModule.getMinigameModule(mg).getRegions()){
//				if(r.hasPlayer(ply)){
//					r.removePlayer(ply);
//					r.execute(RegionTrigger.LEAVE, ply);
//				}
//			}
		}
	}
}
