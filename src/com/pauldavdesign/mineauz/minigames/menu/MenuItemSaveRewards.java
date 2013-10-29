package com.pauldavdesign.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardGroup;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardItem;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardRarity;

public class MenuItemSaveRewards extends MenuItem{
	private RewardGroup group;

	public MenuItemSaveRewards(String name, List<String> description, Material displayItem) {
		super(name, description, displayItem);
	}
	
	@Override
	public ItemStack onClick(){
		ItemStack[] items = getContainer().getInventory();
		group.clearGroup();
		
		for(int i = 0; i < 36; i++){
			if(items[i] != null){
				RewardItem item = new RewardItem(items[i], RewardRarity.NORMAL);
				group.addItem(item);
			}
		}
		getContainer().getViewer().sendMessage("Saved the '" + group.getName() + "' reward group.", null);
		getContainer().getPreviousPage().displayMenu(getContainer().getViewer());
		return null;
	}
}
