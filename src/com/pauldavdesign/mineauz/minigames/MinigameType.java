package com.pauldavdesign.mineauz.minigames;

import org.bukkit.ChatColor;
//import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class MinigameType {
	private static Minigames plugin;
	private PlayerData pdata;
	private MinigameData mdata;
	
	public MinigameType(){
		plugin = Minigames.plugin;
		pdata = plugin.pdata;
		mdata = plugin.mdata;
	}
	
	public void joinMinigame(Player player, String minigame, Minigame mgm){
		
	}
	
	public void quitMinigame(Player player, Minigame mgm, boolean forced){
		callGeneralQuit(player);
	}
	
	public void endMinigame(Player player, Minigame mgm){
		
	}
	
	public void callGeneralQuit(Player player){
		String minigame = pdata.getPlayersMinigame(player);

		player.teleport(mdata.getMinigame(minigame).getQuitPosition());
		
		pdata.removePlayerCheckpoints(player);
		pdata.removeAllPlayerFlags(player);
		
		player.sendMessage(ChatColor.RED + "You've left the " + minigame + " minigame.");
		player.getInventory().clear();
		
		pdata.restorePlayerData(player);
		player.setFireTicks(0);
		
		plugin.getLogger().info(player.getName() + " quit " + minigame);
	}
	
	@SuppressWarnings("deprecation")
	public void issuePlayerRewards(Player player, Minigame save, boolean hascompleted){
		if(save.getRewardItem() != null && !hascompleted){
			player.getInventory().addItem(save.getRewardItem());
		}
		else if(save.getSecondaryRewardItem() != null && hascompleted){
			player.getInventory().addItem(save.getSecondaryRewardItem());
		}
		player.updateInventory();
		
		if(Minigames.plugin.hasEconomy()){
			if(save.getRewardPrice() != 0 && !hascompleted){
				Minigames.plugin.getEconomy().depositPlayer(player.getName(), save.getRewardPrice());
				player.sendMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE + String.format("You have been awarded $%s.", save.getRewardPrice()));
			}
			else if(save.getSecondaryRewardPrice() != 0 && hascompleted){
				Minigames.plugin.getEconomy().depositPlayer(player.getName(), save.getSecondaryRewardPrice());
				player.sendMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE + String.format("You have been awarded $%s.", save.getSecondaryRewardPrice()));
			}
		}
	}
}
