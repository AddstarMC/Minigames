package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameSave;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.RestoreBlock;
import com.pauldavdesign.mineauz.minigames.SQLCompletionSaver;

public class SPMinigame extends MinigameType{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	public SPMinigame() {
		setLabel("sp");
	}
	
	@Override
	public boolean joinMinigame(MinigamePlayer player, Minigame mgm){
		if(mgm.getQuitPosition() != null && mgm.isEnabled()){
			Location startpos = mdata.getMinigame(mgm.getName()).getStartLocations().get(0);
			pdata.minigameTeleport(player, startpos);

			if(mgm.hasRestoreBlocks() && !mgm.hasPlayers()){
				for(RestoreBlock block : mgm.getRestoreBlocks().values()){
					mgm.getBlockRecorder().addBlock(block.getLocation().getBlock(), null);
				}
			}
			
//			pdata.storePlayerData(player, mgm.getDefaultGamemode());
			player.storePlayerData();
//			pdata.addPlayerMinigame(player, mgm);
			player.setMinigame(mgm);
			mgm.addPlayer(player);
			
			player.sendMessage(MinigameUtils.formStr("player.join.plyInfo", mgm.getType()), null);
			
			//pdata.setPlayerCheckpoints(player, startpos);
			player.setCheckpoint(startpos);
			
			if(mgm.getLives() > 0){
				player.sendMessage(MinigameUtils.formStr("minigame.livesLeft", mgm.getLives()), null);
			}
			
			mgm.getPlayersLoadout(player).equiptLoadout(player);
			return true;
		}
		else if(mgm.getQuitPosition() == null){
			player.sendMessage(MinigameUtils.getLang("minigame.error.noQuit"), "error");
		}
		else if(!mgm.isEnabled()){
			player.sendMessage(MinigameUtils.getLang("minigame.error.notEnabled"), "error");
		}
		return false;
	}
	
	@Override
	public void endMinigame(MinigamePlayer player, Minigame mgm){
		boolean hascompleted = false;
		Configuration completion = null;
		
		player.sendMessage(MinigameUtils.formStr("player.end.plyMsg", mgm.getName()), "win");
		
		if(plugin.getConfig().getBoolean("singleplayer.broadcastcompletion")){
			plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("player.end.broadcastMsg", player.getName(), mgm.getName()));
		}
		
		if(mgm.getEndPosition() != null){
			if(!player.getPlayer().isDead()){
				player.getPlayer().teleport(mgm.getEndPosition());
			}
			else{
//				pdata.addRespawnPosition(player.getPlayer(), mgm.getEndPosition());
				player.setRequiredQuit(true);
				player.setQuitPos(mgm.getEndPosition());
			}
		}
		
		if(mgm.getBlockRecorder().hasData()){
			if(mgm.getPlayers().isEmpty()){
				mgm.getBlockRecorder().restoreBlocks();
				mgm.getBlockRecorder().restoreEntities();
			}
			else{
				mgm.getBlockRecorder().restoreBlocks(player);
				mgm.getBlockRecorder().restoreEntities(player);
			}
		}
		
		if(plugin.getSQL() == null){
			completion = mdata.getConfigurationFile("completion");
			hascompleted = completion.getStringList(mgm.getName()).contains(player.getName());
			
			if(!completion.getStringList(mgm.getName()).contains(player.getName())){
				List<String> completionlist = completion.getStringList(mgm.getName());
				completionlist.add(player.getName());
				completion.set(mgm.getName(), completionlist);
				MinigameSave completionsave = new MinigameSave("completion");
				completionsave.getConfig().set(mgm.getName(), completionlist);
				completionsave.saveConfig();
			}
			
			issuePlayerRewards(player, mgm, hascompleted);
		}
		else{
			new SQLCompletionSaver(mgm.getName(), player, this);
		}
	}

	@Override
	public void quitMinigame(final MinigamePlayer player, final Minigame mgm, boolean forced) {
		if(mgm.canSaveCheckpoint()){
			Location pcp = player.getCheckpoint(); //pdata.getPlayerCheckpoint(player);
			Location start = mgm.getStartLocations().get(0);
			if(pcp.getBlockX() != start.getBlockX() || pcp.getBlockY() != start.getBlockY() || pcp.getBlockZ() != start.getBlockZ()){
				if(pdata.hasStoredPlayerCheckpoint(player)){
					pdata.getPlayersStoredCheckpoints(player).addCheckpoint(mgm.getName(), player.getCheckpoint());
					//if(pdata.playerHasFlags(player)){
					if(!player.getFlags().isEmpty()){
						pdata.getPlayersStoredCheckpoints(player).addFlags(mgm.getName(), player.getFlags());
					}
				}
				else{
					pdata.addStoredPlayerCheckpoint(player, mgm.getName(), player.getCheckpoint());
					if(!player.getFlags().isEmpty()){
						pdata.getPlayersStoredCheckpoints(player).addFlags(mgm.getName(), player.getFlags());
					}
				}
			}
		}

		callGeneralQuit(player, mgm);
		
		if(mgm.getBlockRecorder().hasData()){
			if(mgm.getPlayers().isEmpty()){
				mgm.getBlockRecorder().restoreBlocks();
				mgm.getBlockRecorder().restoreEntities();
			}
			else{
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
			if(mgm.getType().equalsIgnoreCase("sp")){
				event.setRespawnLocation(player.getCheckpoint());
				player.sendMessage(MinigameUtils.getLang("player.checkpoint.deathRevert"), "error");
				
				mgm.getPlayersLoadout(player).equiptLoadout(player);
			}
		}
	}
}
