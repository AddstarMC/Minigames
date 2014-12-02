package au.com.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;

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
		rewardMenu.addItem(new MenuItemRewardGroupAdd("Add Group", Material.ITEM_FRAME, rewards), 42);
		rewardMenu.addItem(new MenuItemRewardAdd("Add Item", des, Material.ITEM_FRAME, rewards), 43);
		rewardMenu.addItem(new MenuItemPage("Save " + getName(), Material.REDSTONE_TORCH_ON, rewardMenu.getPreviousPage()), 44);
		List<String> list = new ArrayList<String>();
		for(RewardRarity r : RewardRarity.values()){
			list.add(r.toString());
		}
		
		List<MenuItem> mi = new ArrayList<MenuItem>();
		for(RewardType item : rewards.getRewards()){
			mi.add(item.getMenuItem());
		}
		des = new ArrayList<String>();
		des.add("Double Click to edit");
		for(RewardGroup group : rewards.getGroups()){
			MenuItemRewardGroup rwg = new MenuItemRewardGroup(group.getName() + " Group", des, Material.CHEST, group, rewards);
			mi.add(rwg);
		}
		rewardMenu.addItems(mi);
		rewardMenu.displayMenu(getContainer().getViewer());
		return null;
	}

}
