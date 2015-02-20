package au.com.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;

public class MenuItemDisplayRewards extends MenuItem{
	
	private Rewards rewards;

	public MenuItemDisplayRewards(String name, String description, Material displayItem, Rewards rewards) {
		super(name, description, displayItem);
		this.rewards = rewards;
	}
	
	public MenuItemDisplayRewards(String name, String description, MaterialData displayItem, Rewards rewards) {
		super(name, description, displayItem);
		this.rewards = rewards;
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		Menu rewardMenu = new Menu(5, getName());
		
		rewardMenu.setControlItem(new MenuItemRewardGroupAdd("Add Group", Material.ITEM_FRAME, rewards), 4);
		rewardMenu.setControlItem(new MenuItemRewardAdd("Add Item", Material.ITEM_FRAME, rewards), 3);

		List<String> list = new ArrayList<String>();
		for(RewardRarity r : RewardRarity.values()){
			list.add(r.toString());
		}
		
		List<MenuItem> mi = new ArrayList<MenuItem>();
		for(RewardType item : rewards.getRewards()){
			mi.add(item.getMenuItem());
		}
		for(RewardGroup group : rewards.getGroups()){
			MenuItemRewardGroup rwg = new MenuItemRewardGroup(group.getName() + " Group", Material.CHEST, group, rewards);
			mi.add(rwg);
		}
		rewardMenu.addItems(mi);
		rewardMenu.displayMenu(player);
	}

}
