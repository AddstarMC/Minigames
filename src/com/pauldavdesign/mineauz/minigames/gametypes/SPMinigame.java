package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.List;


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
	public boolean joinMinigame(Player player, Minigame mgm){
		if(mgm.getQuitPosition() != null && mgm.isEnabled()){
			pdata.setAllowTP(player, true);
			pdata.storePlayerData(player, mgm.getDefaultGamemode());
			pdata.addPlayerMinigame(player, mgm.getName());
			player.setAllowFlight(false);
			mgm.addPlayer(player);
			plugin.getLogger().info(player.getName() + " started " + mgm.getName());
			
			Location startpos = mdata.getMinigame(mgm.getName()).getStartLocations().get(0);
			player.teleport(startpos);
			player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + 
					"You have started a singleplayer minigame, type /minigame quit to exit.");
			pdata.setPlayerCheckpoints(player, startpos);
			
			mdata.sendMinigameMessage(mgm, player.getName() + " has joined " + mgm.getName(), null, player);
			
			if(mgm.hasRestoreBlocks() && !mgm.hasPlayers()){
				for(RestoreBlock block : mgm.getRestoreBlocks().values()){
					mgm.getBlockRecorder().addBlock(block.getLocation().getBlock(), null);
				}
			}
			
			if(mgm.getLives() > 0){
				player.sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "Lives left: " + mgm.getLives());
			}
			
			mgm.getPlayersLoadout(player).equiptLoadout(player);
			return true;
		}
		else if(mgm.getQuitPosition() == null){
			player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "This Minigame has no quit position!");
		}
		else if(!mgm.isEnabled()){
			player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "This Minigame is not enabled!");
		}
		return false;
	}
	
	@Override
	public void endMinigame(Player player, Minigame mgm){
		String minigame = pdata.getPlayersMinigame(player);
		
		boolean hascompleted = false;
		Configuration completion = null;
		
		player.sendMessage(ChatColor.GREEN + "You've finished the " + minigame + " minigame. Congratulations!");
		
		if(plugin.getConfig().getBoolean("singleplayer.broadcastcompletion")){
			plugin.getServer().broadcastMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE + player.getName() + " completed " + mgm.getName());
		}
		
		if(mgm.getEndPosition() != null){
			player.teleport(mgm.getEndPosition());
		}

		player.setFireTicks(0);
		
		plugin.getLogger().info(player.getName() + " completed " + minigame);
		
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
		
		mgm.removePlayer(player);
		
		if(plugin.getSQL() == null){
			completion = mdata.getConfigurationFile("completion");
			hascompleted = completion.getStringList(minigame).contains(player.getName());
			
			if(!completion.getStringList(minigame).contains(player.getName())){
				List<String> completionlist = completion.getStringList(minigame);
				completionlist.add(player.getName());
				completion.set(minigame, completionlist);
				MinigameSave completionsave = new MinigameSave("completion");
				completionsave.getConfig().set(minigame, completionlist);
				completionsave.saveConfig();
			}
			
			issuePlayerRewards(player, mgm, hascompleted);
		}
		else{
			new SQLCompletionSaver(minigame, player, this);
		}
	}

	@Override
	public void quitMinigame(final Player player, final Minigame mgm, boolean forced) {
		if(mgm.canSaveCheckpoint()){
			Location pcp = pdata.getPlayerCheckpoint(player);
			Location start = mgm.getStartLocations().get(0);
			if(pcp.getBlockX() != start.getBlockX() || pcp.getBlockY() != start.getBlockY() || pcp.getBlockZ() != start.getBlockZ()){
				if(pdata.hasStoredPlayerCheckpoint(player)){
					pdata.getPlayersStoredCheckpoints(player).addCheckpoint(mgm.getName(), pdata.getPlayerCheckpoint(player));
					if(pdata.playerHasFlags(player)){
						pdata.getPlayersStoredCheckpoints(player).addFlags(mgm.getName(), pdata.getPlayerFlags(player));
					}
				}
				else{
					pdata.addStoredPlayerCheckpoint(player, mgm.getName(), pdata.getPlayerCheckpoint(player));
					if(pdata.playerHasFlags(player)){
						pdata.getPlayersStoredCheckpoints(player).addFlags(mgm.getName(), pdata.getPlayerFlags(player));
					}
				}
			}
		}

		callGeneralQuit(player);

		mgm.removePlayer(player);
		
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
		if(pdata.playerInMinigame(event.getPlayer())){
			String minigame = pdata.getPlayersMinigame(event.getPlayer());
			Minigame mgm = mdata.getMinigame(minigame);
			if(mgm.getType().equalsIgnoreCase("sp")){
				event.setRespawnLocation(pdata.getPlayerCheckpoint(event.getPlayer()));
				event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Bad Luck! Returning to checkpoint.");
				
				mgm.getPlayersLoadout(event.getPlayer()).equiptLoadout(event.getPlayer());
			}
		}
	}
}
