package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.Rewards;

public class MenuItemRewardGroupAdd extends MenuItem{
	
	private Rewards rewards;

	public MenuItemRewardGroupAdd(String name, Material displayItem, Rewards rewards) {
		super(name, displayItem);
		this.rewards = rewards;
	}

	public MenuItemRewardGroupAdd(String name, List<String> description, Material displayItem, Rewards rewards) {
		super(name, description, displayItem);
		this.rewards = rewards;
	}
	
	@Override
	public ItemStack onClick(MinigamePlayer player){
		beginManualEntry(player, "Enter reward group name into chat, the menu will automatically reopen in 30s if nothing is entered.", 30);
		return null;
	}
	
	@Override
	public void checkValidEntry(MinigamePlayer player, String entry){
		entry = entry.replace(" ", "_");
		for(RewardGroup group : rewards.getGroups()){
			if(group.getName().equals(entry)){
				player.sendMessage("A reward group already exists by the name \"" + entry + "\"!", "error");
				return;
			}
		}
		
		RewardGroup group = rewards.addGroup(entry, RewardRarity.NORMAL);
		
		MenuItemRewardGroup mrg = new MenuItemRewardGroup(entry + " Group", Material.CHEST, group, rewards);
		getContainer().addItem(mrg);
	}

}
