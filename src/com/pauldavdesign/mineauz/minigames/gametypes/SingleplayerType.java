package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.Calendar;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameSave;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.RestoreBlock;
import com.pauldavdesign.mineauz.minigames.StoredPlayerCheckpoints;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.sql.SQLPlayer;

public class SingleplayerType extends MinigameTypeBase{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	public SingleplayerType() {
		setType(MinigameType.SINGLEPLAYER);
	}
	
	@Override
	public boolean joinMinigame(MinigamePlayer player, Minigame mgm){
		if(mgm.getQuitPosition() != null && mgm.isEnabled() && (!mgm.isSpMaxPlayers() || mgm.getPlayers().size() < mgm.getMaxPlayers())){
			Location startpos = mdata.getMinigame(mgm.getName()).getStartLocations().get(0);
			if(player.getPlayer().getWorld() != mgm.getStartLocations().get(0).getWorld() && player.getPlayer().hasPermission("minigame.set.start") && plugin.getConfig().getBoolean("warnings")){
				player.sendMessage(ChatColor.RED + "WARNING: " + ChatColor.WHITE + "Join location is across worlds! This may cause some server performance issues!", "error");
			}
			pdata.minigameTeleport(player, startpos);

			if(mgm.hasRestoreBlocks() && !mgm.hasPlayers()){
				for(RestoreBlock block : mgm.getRestoreBlocks().values()){
					mgm.getBlockRecorder().addBlock(block.getLocation().getBlock(), null);
				}
			}
			
			player.storePlayerData();
			player.setMinigame(mgm);
			mgm.addPlayer(player);
			
			if(mgm.getGametypeName() == null)
				player.sendMessage(MinigameUtils.formStr("player.join.plyInfo", mgm.getType().getName()), "win");
			else
				player.sendMessage(MinigameUtils.formStr("player.join.plyInfo", mgm.getGametypeName()), "win");
			
			if(mgm.getObjective() != null){
				player.sendMessage(ChatColor.GREEN + "----------------------------------------------------");
				player.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + MinigameUtils.formStr("player.join.objective", 
						ChatColor.RESET.toString() + ChatColor.WHITE + mgm.getObjective()));
				player.sendMessage(ChatColor.GREEN + "----------------------------------------------------");
			}
			
			player.setCheckpoint(startpos);
			
			if(mgm.getLives() > 0){
				player.sendMessage(MinigameUtils.formStr("minigame.livesLeft", mgm.getLives()), null);
			}
			
			player.getLoadout().equiptLoadout(player);
			return true;
		}
		else if(mgm.getQuitPosition() == null){
			player.sendMessage(MinigameUtils.getLang("minigame.error.noQuit"), "error");
		}
		else if(!mgm.isEnabled()){
			player.sendMessage(MinigameUtils.getLang("minigame.error.notEnabled"), "error");
		}
		else if(mgm.isSpMaxPlayers()){
			player.sendMessage(MinigameUtils.getLang("minigame.full"), "error");
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
			if(player.getPlayer().getWorld() != mgm.getEndPosition().getWorld() && player.getPlayer().hasPermission("minigame.set.end") && plugin.getConfig().getBoolean("warnings")){
				player.sendMessage(ChatColor.RED + "WARNING: " + ChatColor.WHITE + "End location is across worlds! This may cause some server performance issues!", "error");
			}
			if(!player.getPlayer().isDead()){
				player.getPlayer().teleport(mgm.getEndPosition());
			}
			else{
				player.setRequiredQuit(true);
				player.setQuitPos(mgm.getEndPosition());
			}
		}
		
		if(mgm.getBlockRecorder().hasData()){
			if(mgm.getPlayers().isEmpty()){
				mgm.getBlockRecorder().restoreBlocks();
				mgm.getBlockRecorder().restoreEntities();
				mgm.getBlockRecorder().setCreatedRegenBlocks(false);
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
//			new SQLCompletionSaver(mgm.getName(), player, this, true);
			plugin.addSQLToStore(new SQLPlayer(mgm.getName(), player.getName(), 1, 0, player.getKills(), player.getDeaths(), player.getScore(), player.getReverts(), player.getEndTime() - player.getStartTime()));
			plugin.startSQLCompletionSaver();
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

		callGeneralQuit(player, mgm);
		
		if(mgm.getBlockRecorder().hasData()){
			if(mgm.getPlayers().isEmpty()){
				mgm.getBlockRecorder().restoreBlocks();
				mgm.getBlockRecorder().restoreEntities();
				mgm.getBlockRecorder().setCreatedRegenBlocks(false);
			}
			else{
				mgm.getBlockRecorder().restoreBlocks(player);
				mgm.getBlockRecorder().restoreEntities(player);
			}
		}

		if(plugin.getSQL() != null){
			if(mgm.canSaveCheckpoint() == false){
//				new SQLCompletionSaver(mgm.getName(), player, this, false);
				plugin.addSQLToStore(new SQLPlayer(mgm.getName(), player.getName(), 0, 1, player.getKills(), player.getDeaths(), player.getScore(), player.getReverts(), player.getEndTime() - player.getStartTime() + player.getStoredTime()));
				plugin.startSQLCompletionSaver();
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
