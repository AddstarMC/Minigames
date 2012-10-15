package com.pauldavdesign.mineauz.minigames;

import java.util.List;


import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class SPMinigame extends MinigameType{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	public SPMinigame(){}
	
	public void joinMinigame(Player player, String minigame, Minigame mgm){
		if(mgm.getQuitPosition() != null && player.getGameMode() == GameMode.SURVIVAL && mgm.isEnabled()){
			pdata.addPlayerMinigame(player, minigame);
			player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
			player.setAllowFlight(false);
			plugin.getLogger().info(player.getName() + " started " + minigame);
			
			Location startpos = mdata.getMinigame(minigame).getStartLocations().get(0);
			player.teleport(startpos);
			player.sendMessage(ChatColor.GREEN + "You have started a singleplayer minigame, type /minigame quit to exit.");
			pdata.setPlayerCheckpoints(player, startpos);
				
			pdata.storePlayerData(player);
			
			List<Player> plys = pdata.playersInMinigame();
			for(Player ply : plys){
				if(minigame.equals(pdata.getPlayersMinigame(ply)) && !ply.getName().equals(player.getName())){
					ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + player.getName() + " has joined " + minigame);
				}
			}
		}
		else if(mgm.getQuitPosition() == null){
			player.sendMessage(ChatColor.RED + "This minigame has no quit position!");
		}
		else if(!mgm.isEnabled()){
			player.sendMessage(ChatColor.RED + "This minigame is not enabled!");
		}
		if(!mgm.getLoadout().isEmpty()){
			mdata.equiptLoadout(minigame, player);
		}
	}
	
	public void endMinigame(Player player, Minigame mgm){
		
		player.getInventory().clear();
		String minigame = pdata.getPlayersMinigame(player);
		
		pdata.restorePlayerData(player);
		pdata.saveItems(player);
		
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
}
