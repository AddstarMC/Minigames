package com.pauldavdesign.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardItem;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardRarity;
import com.pauldavdesign.mineauz.minigames.minigame.reward.Rewards;

public class MenuItemDisplayRewards extends MenuItem{
	
	private Rewards rewards;

	public MenuItemDisplayRewards(String name, Material displayItem, Rewards rewards) {
		super(name, displayItem);
		this.rewards = rewards;
	}

	public MenuItemDisplayRewards(String name, List<String> description, Material displayItem, Rewards rewards) {
		super(name, description, displayItem);
		this.rewards = rewards;
	}
	
	@Override
	public ItemStack onClick(){
		Menu rewardMenu = new Menu(5, getName(), getContainer().getViewer());
		
		rewardMenu.setPreviousPage(getContainer());
		
		List<String> des = new ArrayList<String>();
		des.add("Click this with an item");
		des.add("to add it to rewards.");
		des.add("Click without an item");
		des.add("to add a money reward.");
		rewardMenu.addItem(new MenuItemRewardAdd("Add Item", des, Material.PORTAL, rewards), 43);
		rewardMenu.addItem(new MenuItemPage("Save " + getName(), Material.REDSTONE_TORCH_ON, rewardMenu.getPreviousPage()), 44);
		List<String> list = new ArrayList<String>();
		for(RewardRarity r : RewardRarity.values()){
			list.add(r.toString());
		}
		int inc = 0;
		for(RewardItem item : rewards.getRewards()){
			if(item.getItem() != null){
				MenuItemReward rew = new MenuItemReward(MinigameUtils.getItemStackName(item.getItem()), item.getItem().getType(), item, rewards, list);
				rew.setItem(item.getItem());
				rew.updateDescription();
				rewardMenu.addItem(rew, inc);
			}
			else{
				MenuItemReward rew = new MenuItemReward("$" + item.getMoney(), Material.PAPER, item, rewards, list);
				rewardMenu.addItem(rew, inc);
			}
			inc++;
		}
		rewardMenu.displayMenu(getContainer().getViewer());
		return null;
	}

}
