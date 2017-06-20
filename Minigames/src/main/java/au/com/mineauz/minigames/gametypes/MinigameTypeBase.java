package au.com.mineauz.minigames.gametypes;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;

public abstract class MinigameTypeBase implements Listener{
	private static Minigames plugin;
	
	protected MinigameTypeBase(){
		plugin = Minigames.plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	private MinigameType type;
	
	public void setType(MinigameType type){
		this.type = type;
	}
	
	public MinigameType getType(){
		return type;
	}
	
	public abstract boolean joinMinigame(MinigamePlayer player, Minigame mgm);
	
	public abstract void quitMinigame(MinigamePlayer player, Minigame mgm, boolean forced);
	
	public abstract void endMinigame(List<MinigamePlayer> winners, List<MinigamePlayer> losers, Minigame mgm);
	
	public void callGeneralQuit(MinigamePlayer player, Minigame minigame){
			if(!player.getPlayer().isDead()){
				if(player.getPlayer().getWorld() != minigame.getQuitPosition().getWorld() && player.getPlayer().hasPermission("minigame.set.quit") && plugin.getConfig().getBoolean("warnings")){
					player.sendMessage(ChatColor.RED + "WARNING: " + ChatColor.WHITE + "Quit location is across worlds! This may cause some server performance issues!", "error");
				}
				player.teleport(minigame.getQuitPosition());
			}
			else{
				player.setQuitPos(minigame.getQuitPosition());
				player.setRequiredQuit(true);
			}
	}
	
//	private static void giveRewardItem(MinigamePlayer player, RewardType reward){
//		if(!player.isInMinigame()){
//			if(!player.getPlayer().isDead())
//				player.getPlayer().getInventory().addItem(reward.getItem());
//			else{
//				int c = 0;
//				for(ItemStack i : player.getOfflineMinigamePlayer().getStoredItems()){
//					if(i == null){
//						player.getOfflineMinigamePlayer().getStoredItems()[c] = reward.getItem();
//						break;
//					}
//					c++; //TODO: Add temp reward item to player instead and give it to them on respawn
//				}
//				player.getOfflineMinigamePlayer().savePlayerData();
//			}
//		}
//		else{
//			player.addTempRewardItem(reward.getItem());
//		}
//		player.sendMessage(MinigameUtils.formStr("player.end.awardItem", reward.getItem().getAmount(), MinigameUtils.getItemStackName(reward.getItem())), "win");
//	}
}
