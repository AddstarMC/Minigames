package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.reward.Rewards;

public class MenuItemDisplayRewards extends MenuItem{
	
	private Rewards rewards;

	public MenuItemDisplayRewards(String name, Material displayItem, Rewards rewards) {
		super(name, "", displayItem);
		this.rewards = rewards;
	}
	
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
		Menu rewardMenu = rewards.createMenu(getName());
		
		rewardMenu.displayMenu(player);
	}

}
