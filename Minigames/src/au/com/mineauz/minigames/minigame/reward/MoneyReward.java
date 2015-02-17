package au.com.mineauz.minigames.minigame.reward;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemDecimal;

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
		player.sendMessage(MinigameUtils.formStr("sign.reward.rewardedMoney", "$" + money), null);
	}

	@Override
	public MenuItem getMenuItem() {
		return new MenuItemReward(this);
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
	
	private class MenuItemReward extends MenuItem{
		private List<String> options = new ArrayList<String>();
		private MoneyReward reward;
		
		public MenuItemReward(MoneyReward reward){
			super("$" + money, Material.PAPER);
			for(RewardRarity rarity : RewardRarity.values()){
				options.add(rarity.toString());
			}
			this.reward = reward;
			updateDescription();
		}
		
		public void updateName(String newName){
			ItemMeta meta = getItem().getItemMeta();
			meta.setDisplayName(ChatColor.RESET + newName);
			getItem().setItemMeta(meta);
		}
		
		public void updateDescription(){
			List<String> description = null;
			if(options == null){
				options = new ArrayList<String>();
				for(RewardRarity rarity : RewardRarity.values()){
					options.add(rarity.toString());
				}
			}
			int pos = options.indexOf(getRarity().toString());
			int before = pos - 1;
			int after = pos + 1;
			if(before == -1)
				before = options.size() - 1;
			if(after == options.size())
				after = 0;
			
			if(getDescription() != null){
				description = getDescription();
				if(getDescription().size() >= 3){
					String desc = ChatColor.stripColor(getDescription().get(1));
					
					if(options.contains(desc)){
						description.set(0, ChatColor.GRAY.toString() + options.get(before));
						description.set(1, ChatColor.GREEN.toString() + getRarity().toString());
						description.set(2, ChatColor.GRAY.toString() + options.get(after));
					}
					else{
						description.add(0, ChatColor.GRAY.toString() + options.get(before));
						description.add(1, ChatColor.GREEN.toString() + getRarity().toString());
						description.add(2, ChatColor.GRAY.toString() + options.get(after));
						description.add(3, ChatColor.DARK_PURPLE.toString() + "Shift + Click to change");
						description.add(4, ChatColor.DARK_PURPLE.toString() + "Shift + Right Click to remove");
					}
				}
				else{
					description.add(0, ChatColor.GRAY.toString() + options.get(before));
					description.add(1, ChatColor.GREEN.toString() + getRarity().toString());
					description.add(2, ChatColor.GRAY.toString() + options.get(after));
					description.add(3, ChatColor.DARK_PURPLE.toString() + "Shift + Click to change");
					description.add(4, ChatColor.DARK_PURPLE.toString() + "Shift + Right Click to remove");
				}
			}
			else{
				description = new ArrayList<String>();
				description.add(ChatColor.GRAY.toString() + options.get(before));
				description.add(ChatColor.GREEN.toString() + getRarity().toString());
				description.add(ChatColor.GRAY.toString() + options.get(after));
				description.add(3, ChatColor.DARK_PURPLE.toString() + "Shift + Click to change");
				description.add(4, ChatColor.DARK_PURPLE.toString() + "Shift + Right Click to remove");
			}
			
			setDescription(description);
		}
		
		@Override
		public ItemStack onClick(MinigamePlayer player){
			int ind = options.lastIndexOf(getRarity().toString());
			ind++;
			if(ind == options.size())
				ind = 0;
			
			setRarity(RewardRarity.valueOf(options.get(ind)));
			updateDescription();
			
			return getItem();
		}
		
		@Override
		public ItemStack onRightClick(MinigamePlayer player){
			int ind = options.lastIndexOf(getRarity().toString());
			ind--;
			if(ind == -1)
				ind = options.size() - 1;
			
			setRarity(RewardRarity.valueOf(options.get(ind)));
			updateDescription();
			
			return getItem();
		}
		
		@Override
		public ItemStack onShiftClick(MinigamePlayer player){
			Menu m = new Menu(3, "Set Money Amount");
			MenuItemDecimal dec = new MenuItemDecimal("Money", Material.PAPER, new Callback<Double>() {

				@Override
				public void setValue(Double value) {
					reward.money = value;
					updateName("$" + value);
				}

				@Override
				public Double getValue() {
					return reward.money;
				}
			}, 50d, 100d, 1d, null);
			m.addItem(dec);
			m.addItem(new MenuItemBack(getContainer()), m.getSize() - 9);
			m.displayMenu(player);
			return null;
		}
		
		@Override
		public ItemStack onShiftRightClick(MinigamePlayer player){
			getRewards().removeReward(reward);
			getContainer().removeItem(getSlot());
			return null;
		}
	}

}
