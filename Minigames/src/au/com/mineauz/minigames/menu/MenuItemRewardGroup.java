package au.com.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import au.com.mineauz.minigames.menu.MenuItem.IMenuItemClick;

public class MenuItemRewardGroup extends MenuItemEnum<RewardRarity> implements IMenuItemClick {
	private RewardGroup group;
	private Rewards rewards;

	public MenuItemRewardGroup(String name, Material displayItem, RewardGroup group, Rewards rewards) {
		super(name, "Double Click to edit;Shift + Right click to remove", displayItem, group.getRarityCallback(), RewardRarity.class);
		this.group = group;
		this.rewards = rewards;
		
		setShiftRightClickHandler(this);
	}
	
	@Override
	public void onClick(MenuItem menuItem, MinigamePlayer player) {
		// Shift right click
		beginManualEntry(player, "Delete the reward group \"" + group.getName() + "\"? Type \"Yes\" to confirm.", 10);
		player.sendMessage("The menu will automatically reopen in 10s if nothing is entered.");
	}
	
	@Override
	protected final boolean isManualEntryAllowed() {
		// Turn off enum manual entry so we can use custom one later
		return false;
	}
	
	@Override
	public void checkValidEntry(MinigamePlayer player, String entry){
		if(entry.equalsIgnoreCase("yes")){
			rewards.removeGroup(group);
			remove();
			return;
		}
		player.sendMessage("The selected group will not be removed from the rewards.", "error");
	}
	
	@Override
	public void onDoubleClick(MinigamePlayer player){
		Menu rewardMenu = new Menu(5, getName());

		rewardMenu.setControlItem(new MenuItemRewardAdd("Add Item", "Click this with an item;to add it to rewards.;Click without an item;to add a money reward.", Material.ITEM_FRAME, group), 4);
		List<String> list = new ArrayList<String>();
		for(RewardRarity r : RewardRarity.values()){
			list.add(r.toString());
		}
		
		List<MenuItem> mi = new ArrayList<MenuItem>();
		for(RewardType item : group.getItems()){
			mi.add(item.getMenuItem());
		}
		
		rewardMenu.addItems(mi);
		rewardMenu.displayMenu(player);
	}
}
