package au.com.mineauz.minigames.minigame.reward;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemDecimal;
import au.com.mineauz.minigames.menu.MenuItemReward;

public class MoneyReward extends RewardType{

	private double money = 0d;
	
	public MoneyReward(Rewards rewards) {
		super(rewards);
	}

	@Override
	public String getName() {
		return "MONEY";
	}

	@Override
	public boolean isUsable() {
		if(Minigames.plugin.getEconomy() == null)
			return false;
		return true;
	}

	@Override
	public void giveReward(MinigamePlayer player) {
		Minigames.plugin.getEconomy().depositPlayer(player.getPlayer().getPlayer(), money);
		player.sendMessage(MinigameUtils.formStr("reward.money", Minigames.plugin.getEconomy().format(money)), MessageType.Normal);
	}

	@Override
	public MenuItem getMenuItem() {
		return new MenuItemRewardMoney(this);
	}

	@Override
	public void saveReward(String path, FileConfiguration config) {
		config.set(path, money);
	}

	@Override
	public void loadReward(String path, FileConfiguration config) {
		money = config.getDouble(path);
	}
	
	public void setRewardMoney(double amount){
		money = amount;
	}
	
	public double getRewardMoney(){
		return money;
	}
	
	private static class MenuItemRewardMoney extends MenuItemReward {
		public MenuItemRewardMoney(MoneyReward reward){
			super("$" + MenuItemDecimal.format.format(reward.money), "Double Click to change;Shift + Right Click to remove", Material.PAPER, reward);
		}
		
		@Override
		protected void onDoubleClick(MinigamePlayer player) {
			beginManualEntry(player, "Enter reward money value into chat, the menu will automatically reopen in 20s if nothing is entered." , 20);
		}
		
		@Override
		protected void checkValidEntry(MinigamePlayer player, String entry) {
			if (entry.startsWith("$")) {
				entry = entry.substring(1);
			}
			
			try {
				double value = Double.parseDouble(entry);
				value = Math.max(value, 0);
				((MoneyReward)getReward()).money = value;
				setName("$" + MenuItemDecimal.format.format(value));
				updateDescription();
			} catch (NumberFormatException e) {
				player.sendMessage("Invalid value entry!", MessageType.Error);
			}
		}
	}

}
