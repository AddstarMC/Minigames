package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.menu.MenuItem.IMenuItemClick;

public abstract class MenuItemReward extends MenuItemEnum<RewardRarity> implements IMenuItemClick {
	private RewardType reward;
	public MenuItemReward(String name, MaterialData displayItem, RewardType reward) {
		super(name, displayItem, reward.getRarityProperty(), RewardRarity.class);
		this.reward = reward;
		setShiftRightClickHandler(this);
	}
	public MenuItemReward(String name, String description, Material displayItem, RewardType reward) {
		super(name, description, displayItem, reward.getRarityProperty(), RewardRarity.class);
		this.reward = reward;
		setShiftRightClickHandler(this);
	}
	public MenuItemReward(String name, Material displayItem, RewardType reward) {
		super(name, null, displayItem, reward.getRarityProperty(), RewardRarity.class);
		this.reward = reward;
		setShiftRightClickHandler(this);
	}
	public MenuItemReward(String name, String description, MaterialData displayItem, RewardType reward) {
		super(name, description, displayItem, reward.getRarityProperty(), RewardRarity.class);
		this.reward = reward;
		setShiftRightClickHandler(this);
	}
	
	public RewardType getReward() {
		return reward;
	}
	
	@Override
	public void onClick(MenuItem menuItem, MinigamePlayer player) {
		// Shift right click
		reward.getRewards().removeReward(reward);
		remove();
	}
	
	@Override
	protected final boolean isManualEntryAllowed() {
		// Turn off enum manual entry so we can use custom one later
		return false;
	}
	
	@Override
	protected void onDoubleClick(MinigamePlayer player) {};
}
