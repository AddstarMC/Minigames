package au.com.mineauz.minigames.minigame.reward;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemReward;

public class ItemReward extends RewardType{

	private ItemStack item = new ItemStack(Material.DIAMOND);
	
	public ItemReward(Rewards rewards) {
		super(rewards);
	}
	
	@Override
	public String getName(){
		return "ITEM";
	}

	@Override
	public boolean isUsable() {
		return true;
	}
	
	@Override
	public void giveReward(MinigamePlayer player) {
		if(player.isInMinigame())
			player.addRewardItem(item);
		else
			player.getPlayer().getInventory().addItem(item);
		player.sendMessage(MinigameUtils.formStr("reward.item", item.getAmount(), 
				MinigameUtils.capitalize(item.getType().toString())), MessageType.Normal);
	}

	@Override
	public MenuItem getMenuItem() {
		return new MenuItemRewardItem(this);
	}

	@Override
	public void saveReward(String path, FileConfiguration config) {
		config.set(path, item);
	}

	@Override
	public void loadReward(String path, FileConfiguration config) {
		item = config.getItemStack(path);
	}
	
	public ItemStack getRewardItem(){
		return item;
	}
	
	public void setRewardItem(ItemStack item){
		this.item = item;
	}
	
	private static class MenuItemRewardItem extends MenuItemReward {

		public MenuItemRewardItem(ItemReward reward) {
			super("PLACEHOLDER", "Click with item;to change.;Shift + Right Click to remove", Material.DIAMOND, reward);
			setItem(reward.getRewardItem());
			setName(MinigameUtils.capitalize(reward.getRewardItem().getType().toString().replace('_', ' ')));
		}
		
		@Override
		public void onClickWithItem(MinigamePlayer player, ItemStack item){
			setItem(item);
			setName(MinigameUtils.capitalize(item.getType().toString().replace('_', ' ')));
			updateDescription();
			((ItemReward)getReward()).item = item.clone();
		}
	}
}
