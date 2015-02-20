package au.com.mineauz.minigames.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.RewardTypes;
import au.com.mineauz.minigames.minigame.reward.Rewards;

public class MenuItemRewardAdd extends MenuItem{
	
	private Rewards rewards;
	private RewardGroup group = null;
	
	public MenuItemRewardAdd(String name, Material displayItem, Rewards rewards) {
		super(name, displayItem);
		this.rewards = rewards;
	}

	public MenuItemRewardAdd(String name, String description, Material displayItem, Rewards rewards) {
		super(name, description, displayItem);
		this.rewards = rewards;
	}
	
	public MenuItemRewardAdd(String name, Material displayItem, RewardGroup group) {
		super(name, displayItem);
		this.group = group;
	}

	public MenuItemRewardAdd(String name, String description, Material displayItem, RewardGroup group) {
		super(name, description, displayItem);
		this.group = group;
	}
	
	@Override
	public void onClick(final MinigamePlayer player){
		Menu m = new Menu(6, "Select Reward Type");
		m.setTrackHistory(false);
		
		for(String type : RewardTypes.getAllRewardTypeNames()){
			final MenuItem custom = new MenuItem("TYPE", Material.STONE);
			final RewardType rewType = RewardTypes.getRewardType(type, rewards);
			if(rewType.isUsable()){
				ItemMeta meta = custom.getItem().getItemMeta();
				meta.setDisplayName(ChatColor.RESET + type);
				custom.getItem().setItemMeta(meta);
				custom.setItem(rewType.getMenuItem().getItem());
				custom.setClickHandler(new IMenuItemClick() {
					@Override
					public void onClick(MenuItem menuItem, MinigamePlayer player) {
						if(rewards != null)
							rewards.addReward(rewType);
						else
							group.addItem(rewType);
						getContainer().addItem(rewType.getMenuItem());
						player.showPreviousMenu();
					}
				});
				m.addItem(custom);
			}
		}
		m.displayMenu(player);
	}
}
