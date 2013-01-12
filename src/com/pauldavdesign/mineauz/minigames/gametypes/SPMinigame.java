package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.List;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigameSave;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;
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
			player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
			player.setAllowFlight(false);
			plugin.getLogger().info(player.getName() + " started " + mgm.getName());
			
			Location startpos = mdata.getMinigame(mgm.getName()).getStartLocations().get(0);
			player.teleport(startpos);
			player.sendMessage(ChatColor.GREEN + "You have started a singleplayer minigame, type /minigame quit to exit.");
			pdata.setPlayerCheckpoints(player, startpos);
			mgm.addPlayer(player);
			
			List<Player> plys = pdata.playersInMinigame();
			for(Player ply : plys){
				if(mgm.getName().equals(pdata.getPlayersMinigame(ply)) && !ply.getName().equals(player.getName())){
					ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + player.getName() + " has joined " + mgm.getName());
				}
			}
			
			mgm.getLoadout(mgm.getPlayersLoadout(player)).equiptLoadout(player);
			return true;
		}
		else if(mgm.getQuitPosition() == null){
			player.sendMessage(ChatColor.RED + "This minigame has no quit position!");
		}
		else if(!mgm.isEnabled()){
			player.sendMessage(ChatColor.RED + "This minigame is not enabled!");
		}
		return false;
//		if(mgm.hasDefaultLoadout()){
//			mgm.getDefaultPlayerLoadout().equiptLoadout(player);
//		}
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
			mgm.getBlockRecorder().restoreBlocks(player);
		}
		
		mgm.removePlayer(player);
		
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

	@Override
	public void quitMinigame(final Player player, final Minigame mgm, boolean forced) {
		callGeneralQuit(player);

		mgm.removePlayer(player);
		
		if(mgm.getBlockRecorder().hasData()){
			if(mgm.getPlayers().isEmpty()){
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						mgm.getBlockRecorder().restoreBlocks();
					}
				});
			}
			else{
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						mgm.getBlockRecorder().restoreBlocks(player);
					}
				});
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
				event.getPlayer().sendMessage(ChatColor.GRAY + "Bad Luck! Returning to checkpoint.");
				
				mgm.getLoadout(mgm.getPlayersLoadout(event.getPlayer())).equiptLoadout(event.getPlayer());
//				if(mgm.hasDefaultLoadout()){
//					mgm.getDefaultPlayerLoadout().equiptLoadout(event.getPlayer());
//				}
			}
		}
	}
}
