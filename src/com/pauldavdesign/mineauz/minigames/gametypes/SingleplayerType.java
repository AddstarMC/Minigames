package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.Calendar;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.StoredPlayerCheckpoints;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class SingleplayerType extends MinigameTypeBase{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	
	public SingleplayerType() {
		setType(MinigameType.SINGLEPLAYER);
	}
	
	@Override
	public boolean joinMinigame(MinigamePlayer player, Minigame mgm){
		
		if(mgm.getLives() > 0){
			player.sendMessage(MinigameUtils.formStr("minigame.livesLeft", mgm.getLives()), null);
		}
		
		if(mgm.getType() == MinigameType.SINGLEPLAYER && player.getStoredPlayerCheckpoints().hasCheckpoint(mgm.getName())){
			player.setCheckpoint(player.getStoredPlayerCheckpoints().getCheckpoint(mgm.getName()));
			StoredPlayerCheckpoints spc = player.getStoredPlayerCheckpoints();
			if(spc.hasFlags(mgm.getName())){
				player.setFlags(spc.getFlags(mgm.getName()));
			}
			if(spc.hasTime(mgm.getName())){
				player.setStoredTime(spc.getTime(mgm.getName()));
			}
			if(spc.hasDeaths(mgm.getName())){
				player.setDeaths(spc.getDeaths(mgm.getName()));
			}
			if(spc.hasReverts(mgm.getName())){
				player.setReverts(spc.getReverts(mgm.getName()));
			}
			spc.removeCheckpoint(mgm.getName());
			spc.removeFlags(mgm.getName());
			spc.removeDeaths(mgm.getName());
			spc.removeTime(mgm.getName());
			spc.removeReverts(mgm.getName());
			pdata.revertToCheckpoint(player);
			spc.saveCheckpoints();
		}
		
		player.getLoadout().equiptLoadout(player);
		return true;
	}
	
	@Override
	public void endMinigame(List<MinigamePlayer> winners, List<MinigamePlayer> losers, Minigame mgm){
		if(mgm.getBlockRecorder().hasData()){
			if(!mgm.getPlayers().isEmpty()){
				for(MinigamePlayer player : winners){
					mgm.getBlockRecorder().restoreBlocks(player);
					mgm.getBlockRecorder().restoreEntities(player);
				}
			}
		}
	}

	@Override
	public void quitMinigame(final MinigamePlayer player, final Minigame mgm, boolean forced) {
		if(mgm.canSaveCheckpoint()){
			Location pcp = player.getCheckpoint();
			Location start = mgm.getStartLocations().get(0);
			if(pcp.getBlockX() != start.getBlockX() || pcp.getBlockY() != start.getBlockY() || pcp.getBlockZ() != start.getBlockZ()){
				
				StoredPlayerCheckpoints spc = player.getStoredPlayerCheckpoints();
				spc.addCheckpoint(mgm.getName(), player.getCheckpoint());
				if(!player.getFlags().isEmpty()){
					spc.addFlags(mgm.getName(), player.getFlags());
				}
				spc.addDeaths(mgm.getName(), player.getDeaths());
				spc.addReverts(mgm.getName(), player.getReverts());
				spc.addTime(mgm.getName(), Calendar.getInstance().getTimeInMillis() - player.getStartTime() + player.getStoredTime());
				spc.saveCheckpoints();
			}
		}
		
		if(mgm.getBlockRecorder().hasData()){
			if(!mgm.getPlayers().isEmpty()){
				mgm.getBlockRecorder().restoreBlocks(player);
				mgm.getBlockRecorder().restoreEntities(player);
			}
		}
	}
	
	/*----------------*/
	/*-----EVENTS-----*/
	/*----------------*/
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event){
		if(pdata.getMinigamePlayer(event.getPlayer()).isInMinigame()){
			MinigamePlayer player = pdata.getMinigamePlayer(event.getPlayer());
			Minigame mgm = player.getMinigame();
			if(mgm.getType() == MinigameType.SINGLEPLAYER){
				event.setRespawnLocation(player.getCheckpoint());
				player.sendMessage(MinigameUtils.getLang("player.checkpoint.deathRevert"), "error");
				
				player.getLoadout().equiptLoadout(player);
			}
		}
	}
}
