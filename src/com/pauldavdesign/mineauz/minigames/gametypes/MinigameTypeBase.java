package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.List;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardItem;

public abstract class MinigameTypeBase implements Listener{
	private static Minigames plugin;
	private PlayerData pdata;
	
	protected MinigameTypeBase(){
		plugin = Minigames.plugin;
		pdata = plugin.pdata;
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
	
	public abstract void endMinigame(MinigamePlayer player, Minigame mgm);
	
	public void callGeneralQuit(final MinigamePlayer player, final Minigame minigame){
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if(!player.getPlayer().isDead()){
					pdata.minigameTeleport(player, minigame.getQuitPosition());
				}
				else{
					player.setQuitPos(minigame.getQuitPosition());
					player.setRequiredQuit(true);
				}
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	public static void issuePlayerRewards(MinigamePlayer player, Minigame save, boolean hascompleted){
		List<RewardItem> rewardL = save.getRewardItem();
		List<RewardItem> srewardL = save.getSecondaryRewardItem();
		double totalMoney = 0;
		if(!hascompleted && rewardL != null){
			for(RewardItem reward : rewardL){
				if(reward != null){
					if(reward.getItem() != null){
						if(!player.getPlayer().isDead())
							player.getPlayer().getInventory().addItem(reward.getItem());
						else{
							int c = 0;
							for(ItemStack i : player.getStoredItems()){
								if(i == null){
									player.getStoredItems()[c] = reward.getItem();
									break;
								}
								c++;
							}
						}
						player.sendMessage(MinigameUtils.formStr("player.end.awardItem", reward.getItem().getAmount(), MinigameUtils.getItemStackName(reward.getItem())), "win");
					}
					else{
						if(Minigames.plugin.hasEconomy() && reward.getMoney() != 0){
							Minigames.plugin.getEconomy().depositPlayer(player.getName(), reward.getMoney());
							totalMoney += reward.getMoney();
						}
					}
				}
			}
			if(totalMoney > 0){
				player.sendMessage(MinigameUtils.formStr("player.end.awardMoney", totalMoney), "win");
			}
		}
		else if(hascompleted && srewardL != null){
			for(RewardItem sreward : srewardL){
				if(sreward != null){
					if(sreward.getItem() != null){
						if(!player.getPlayer().isDead())
							player.getPlayer().getInventory().addItem(sreward.getItem());
						else{
							int c = 0;
							for(ItemStack i : player.getStoredItems()){
								if(i == null){
									player.getStoredItems()[c] = sreward.getItem();
									break;
								}
								c++;
							}
						}
						player.sendMessage(MinigameUtils.formStr("player.end.awardItem", sreward.getItem().getAmount(), MinigameUtils.getItemStackName(sreward.getItem())), "win");
					}
					else{
						if(Minigames.plugin.hasEconomy() && sreward.getMoney() != 0){
							Minigames.plugin.getEconomy().depositPlayer(player.getName(), sreward.getMoney());
							totalMoney += sreward.getMoney();
						}
					}
				}
			}
			if(totalMoney > 0){
				player.sendMessage(MinigameUtils.formStr("player.end.awardMoney", totalMoney), "win");
			}
		}
		player.getPlayer().updateInventory();
	}
}
