package au.com.mineauz.minigames.gametypes;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.reward.RewardType;

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
	
	@SuppressWarnings("deprecation")
	public static void issuePlayerRewards(MinigamePlayer player, Minigame mgm, boolean hascompleted){
		List<RewardType> rewardL = mgm.getRewardItem();
		List<RewardType> srewardL = mgm.getSecondaryRewardItem();
		double totalMoney = 0;
		if(!hascompleted && rewardL != null){
			for(RewardType reward : rewardL){
				if(reward != null){
					reward.giveReward(player);
//					if(reward.getItem() != null){
//						giveRewardItem(player, reward);
//					}
//					else{
//						if(Minigames.plugin.hasEconomy() && reward.getMoney() != 0){
//							Minigames.plugin.getEconomy().depositPlayer(player.getPlayer().getPlayer(), reward.getMoney());
//							totalMoney += reward.getMoney();
//						}
//					}
				}
			}
			if(totalMoney > 0){
				player.sendMessage(MinigameUtils.formStr("player.end.awardMoney", totalMoney), "win");
			}
		}
		else if(hascompleted && srewardL != null){
			for(RewardType sreward : srewardL){
				if(sreward != null){
					sreward.giveReward(player);
//					if(sreward.getItem() != null){
//						giveRewardItem(player, sreward);
//					}
//					else{
//						if(Minigames.plugin.hasEconomy() && sreward.getMoney() != 0){
//							Minigames.plugin.getEconomy().depositPlayer(player.getPlayer().getPlayer(), sreward.getMoney());
//							totalMoney += sreward.getMoney();
//						}
//					}
				}
			}
			if(totalMoney > 0){
				player.sendMessage(MinigameUtils.formStr("player.end.awardMoney", totalMoney), "win");
			}
		}
		player.getPlayer().updateInventory();
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
