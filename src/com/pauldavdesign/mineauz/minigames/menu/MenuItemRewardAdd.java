package com.pauldavdesign.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardGroup;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardItem;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardRarity;
import com.pauldavdesign.mineauz.minigames.minigame.reward.Rewards;

public class MenuItemRewardAdd extends MenuItem{
	
	private Rewards rewards;
	private RewardGroup group = null;
	
	public MenuItemRewardAdd(String name, Material displayItem, Rewards rewards) {
		super(name, displayItem);
		this.rewards = rewards;
	}

	public MenuItemRewardAdd(String name, List<String> description, Material displayItem, Rewards rewards) {
		super(name, description, displayItem);
		this.rewards = rewards;
	}
	
	public MenuItemRewardAdd(String name, Material displayItem, RewardGroup group) {
		super(name, displayItem);
		this.group = group;
	}

	public MenuItemRewardAdd(String name, List<String> description, Material displayItem, RewardGroup group) {
		super(name, description, displayItem);
		this.group = group;
	}
	
	@Override
	public ItemStack onClick(){
		if(Minigames.plugin.hasEconomy()){
			MinigamePlayer ply = getContainer().getViewer();
			ply.setNoClose(true);
			ply.getPlayer().closeInventory();
			ply.sendMessage("Enter a money amount for the reward, the menu will automatically reopen in 10s if nothing is entered.", null);
			ply.setManualEntry(this);

			getContainer().startReopenTimer(10);
			return null;
		}
		else
			getContainer().getViewer().sendMessage("This server doesn't have Vault!", "error");
		return getItem();
	}
	
	@Override
	public void checkValidEntry(String entry){
		if(entry.matches("\\$?[0-9]+(\\.[0-9]{2})?")){
			if((rewards != null && rewards.getRewards().size() < 45) || group.getItems().size() < 45){
				double money = Double.parseDouble(entry.replace("$", ""));
				RewardItem it;
				if(group == null)
					it = rewards.addMoney(money, RewardRarity.NORMAL);
				else{
					it = new RewardItem(money, RewardRarity.NORMAL);
					group.addItem(it);
				}
	
				List<String> list = new ArrayList<String>();
				for(RewardRarity r : RewardRarity.values()){
					list.add(r.toString());
				}
				
				MenuItemReward rew = new MenuItemReward("$" + money, Material.PAPER, it, rewards, list);
				for(int i = 0; i < 45; i++){
					if(!getContainer().hasMenuItem(i)){
						getContainer().addItem(rew, i);
						break;
					}
				}
				
				getContainer().cancelReopenTimer();
				getContainer().displayMenu(getContainer().getViewer());
			}
			else{
				getContainer().cancelReopenTimer();
				getContainer().displayMenu(getContainer().getViewer());
				
				getContainer().getViewer().sendMessage("Too many reward items!", "error");
			}
			return;
		}
		getContainer().cancelReopenTimer();
		getContainer().displayMenu(getContainer().getViewer());
		
		getContainer().getViewer().sendMessage("Invalid value entry!", "error");
	}
	
	@Override
	public ItemStack onClickWithItem(ItemStack item){
		item = item.clone();
		if((rewards != null && rewards.getRewards().size() < 45) || group.getItems().size() < 45){
			RewardItem it;
			if(group == null)
				it = rewards.addItem(item, RewardRarity.NORMAL);
			else{
				it = new RewardItem(item, RewardRarity.NORMAL);
				group.addItem(it);
			}
			
			List<String> list = new ArrayList<String>();
			for(RewardRarity r : RewardRarity.values()){
				list.add(r.toString());
			}
			
			MenuItemReward rew = new MenuItemReward(MinigameUtils.getItemStackName(item), item.getType(), it, rewards, list);
			rew.setItem(item);
			rew.updateDescription();
			for(int i = 0; i < 45; i++){
				if(!getContainer().hasMenuItem(i)){
					getContainer().addItem(rew, i);
					break;
				}
			}
		}
		else
			getContainer().getViewer().sendMessage("Too many reward items!", "error");
		return getItem();
	}
}
