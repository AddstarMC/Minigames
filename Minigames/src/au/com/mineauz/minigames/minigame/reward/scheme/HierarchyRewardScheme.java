package au.com.mineauz.minigames.minigame.reward.scheme;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.EnumFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import au.com.mineauz.minigames.stats.StoredGameStats;

public abstract class HierarchyRewardScheme<T extends Comparable<T>> implements RewardScheme {
	private EnumFlag<Comparison> comparisonType;
	private BooleanFlag enableRewardsOnLoss;
	private BooleanFlag lossUsesSecondary;
	
	private TreeMap<T, Rewards> primaryRewards;
	private TreeMap<T, Rewards> secondaryRewards;
	
	public HierarchyRewardScheme() {
		primaryRewards = new TreeMap<T, Rewards>();
		secondaryRewards = new TreeMap<T, Rewards>();
		
		comparisonType = new EnumFlag<Comparison>(Comparison.Greater, "comparison");
		enableRewardsOnLoss = new BooleanFlag(false, "loss-rewards");
		lossUsesSecondary = new BooleanFlag(true, "loss-use-secondary");
	}
	
	@Override
	public Map<String, Flag<?>> getFlags() {
		return ImmutableMap.<String, Flag<?>>builder()
				.put("comparison", comparisonType)
				.put("loss-rewards", enableRewardsOnLoss)
				.put("loss-use-secondary", lossUsesSecondary)
				.build();
	}
	
	@Override
	public void addMenuItems(final Menu menu) {
		menu.addItem(new MenuItemList("Comparison Type", Material.REDSTONE_COMPARATOR, getConfigurationTypeCallback(), Lists.transform(Arrays.asList(Comparison.values()), Functions.toStringFunction())));
		menu.addItem(enableRewardsOnLoss.getMenuItem("Award On Loss", "When on, awards will still;be given to losing;players", Material.LEVER));
		menu.addItem(lossUsesSecondary.getMenuItem("Losers Get Secondary", "When on, the losers;will only get the;secondary reward", Material.LEVER));
		menu.addItem(new MenuItemNewLine());
		
		MenuItem primary = new MenuItem("Primary Rewards", Material.CHEST) {
			@Override
			protected void onClick(MinigamePlayer player) {
				showRewardsMenu(primaryRewards, player, menu);
			}
		};
		
		MenuItem secondary = new MenuItem("Secondary Rewards", Material.CHEST) {
			@Override
			protected void onClick(MinigamePlayer player) {
				showRewardsMenu(secondaryRewards, player, menu);
			}
		};
		
		menu.addItem(primary);
		menu.addItem(secondary);
	}
	
	private void showRewardsMenu(TreeMap<T, Rewards> rewards, MinigamePlayer player, Menu parent) {
		Menu submenu = new Menu(5, "Rewards");
		
		for (T key : rewards.keySet()) {
			submenu.addItem(new MenuItemRewardPair(rewards, key, Material.CHEST));
		}
		
		submenu.setControlItem(new MenuItemAddReward(rewards, "Add Reward Set", Material.ITEM_FRAME), 4);
		
		submenu.displayMenu(player);
	}
	
	protected abstract T getValue(MinigamePlayer player, StoredGameStats data, Minigame minigame);
	
	@Override
	public void awardPlayer(MinigamePlayer player, StoredGameStats data, Minigame minigame, boolean firstCompletion) {
		T value = getValue(player, data, minigame);
		Rewards reward;
		
		TreeMap<T, Rewards> rewards = (firstCompletion ? primaryRewards : secondaryRewards);
		
		// Calculate rewards
		switch(comparisonType.getFlag()) {
		case Equal:
			reward = rewards.get(value);
			break;
		case Lesser:
			reward = null;
			for (Entry<T, Rewards> entry : rewards.entrySet()) {
				if (value.compareTo(entry.getKey()) < 0) {
					reward = entry.getValue();
					break;
				}
			}
			break;
		case Greater:
			reward = null;
			for (Entry<T, Rewards> entry : rewards.descendingMap().entrySet()) {
				if (value.compareTo(entry.getKey()) > 0) {
					reward = entry.getValue();
					break;
				}
			}
			break;
		default:
			throw new AssertionError();
		}
		
		// Apply reward
		if (reward != null) {
			List<RewardType> rewardItems = reward.getReward();
			for (RewardType item : rewardItems) {
				item.giveReward(player);
			}
		}
	}

	@Override
	public void awardPlayerOnLoss(MinigamePlayer player, StoredGameStats data, Minigame minigame) {
		if (enableRewardsOnLoss.getFlag())
			awardPlayer(player, data, minigame, lossUsesSecondary.getFlag());
	}

	@Override
	public void save(ConfigurationSection config) {
		ConfigurationSection primary = config.createSection("score-primary");
		ConfigurationSection secondary = config.createSection("score-secondary");
		
		save(primaryRewards, primary);
		save(secondaryRewards, secondary);
	}
	
	private void save(TreeMap<T, Rewards> map, ConfigurationSection section) {
		for (Entry<T, Rewards> entry : map.entrySet()) {
			ConfigurationSection scoreSection = section.createSection(String.valueOf(entry.getKey()));
			entry.getValue().save(scoreSection);
		}
	}
	
	@Override
	public void load(ConfigurationSection config) {
		ConfigurationSection primary = config.getConfigurationSection("score-primary");
		ConfigurationSection secondary = config.getConfigurationSection("score-secondary");
		
		load(primaryRewards, primary);
		load(secondaryRewards, secondary);
	}
	
	protected abstract T loadValue(String key);
	
	private void load(TreeMap<T, Rewards> map, ConfigurationSection section) {
		map.clear();
		for (String key : section.getKeys(false)) {
			T value = loadValue(key);
			
			ConfigurationSection subSection = section.getConfigurationSection(key);
			Rewards reward = new Rewards();
			reward.load(subSection);
			
			map.put(value, reward);
		}
	}
	
	private Callback<String> getConfigurationTypeCallback() {
		return new Callback<String>() {
			@Override
			public void setValue(String value) {
				comparisonType.setFlag(Comparison.valueOf(value));
			}
			
			@Override
			public String getValue() {
				return comparisonType.getFlag().name();
			}
		};
	}
	
	protected abstract String getMenuItemName(T value);
	protected abstract String getMenuItemDescName(T value);
	
	protected abstract T increment(T value);
	protected abstract T decrement(T value);

	public enum Comparison {
		Greater,
		Equal,
		Lesser
	}

	private class MenuItemRewardPair extends MenuItem {
		private Rewards reward;
		private T value;
		private TreeMap<T, Rewards> map;
		
		public MenuItemRewardPair(TreeMap<T, Rewards> map, T value, Material displayItem) {
			super(getMenuItemName(value), displayItem);
			
			this.map = map;
			this.value = value;
			this.reward = map.get(value);
			
			updateDescription();
		}
		
		private void updateDescription() {
			List<String> description = Arrays.asList(
					ChatColor.GREEN + getMenuItemDescName(value),
					"Shift + Left click to edit rewards",
					"Shift + Right click to remove"
					);
			
			setDescription(description);
			
			// Update name
			ItemStack item = getItem();
			ItemMeta meta = item.getItemMeta();
			
			meta.setDisplayName(getMenuItemName(value));
			item.setItemMeta(meta);
			
			setItem(item);
		}
		
		private void updateValue(T newValue) {
			map.remove(value);
			value = newValue;
			map.put(value, reward);
		}
		
		@Override
		// Increase score
		public void onClick(MinigamePlayer player) {
			T nextValue = increment(value);
			while (map.containsKey(nextValue)) {
				nextValue = increment(nextValue);
			}
			
			updateValue(nextValue);
			
			updateDescription();
		}
		
		@Override
		// Decrease score
		public void onRightClick(MinigamePlayer player){
			T nextValue = decrement(value);
			while (map.containsKey(nextValue)) {
				nextValue = decrement(nextValue);
			}
			
			updateValue(nextValue);
			
			updateDescription();
		}
		
		@Override
		// Open editor
		public void onDoubleClick(MinigamePlayer player) {
			beginManualEntry(player, "Enter the required value into chat, the menu will automatically reopen in 10s if nothing is entered.", 10);
		}
		
		@Override
		public void checkValidEntry(MinigamePlayer player, String entry){
			try {
				T value = loadValue(entry);
				if (map.containsKey(value)) {
					player.sendMessage("You cannot add duplicate entries", MessageType.Error);
				} else {
					updateValue(value);
					updateDescription();
				}
			} catch (IllegalArgumentException e) {
				player.sendMessage("Invalid value entry!", MessageType.Error);
			}
		}
		
		@Override
		// Open rewards
		public void onShiftClick(MinigamePlayer player) {
			Menu rewardMenu = reward.createMenu(getName());
			
			rewardMenu.displayMenu(player);
		}
		
		@Override
		// Remove
		public void onShiftRightClick(MinigamePlayer player) {
			remove();
			map.remove(value);
		}
	}
	
	private class MenuItemAddReward extends MenuItem {
		private TreeMap<T, Rewards> map;
		
		public MenuItemAddReward(TreeMap<T, Rewards> map, String name, Material displayItem) {
			super(name, displayItem);
			
			this.map = map;
		}
		
		@Override
		public void onClick(MinigamePlayer player) {
			beginManualEntry(player, "Enter the required value into chat, the menu will automatically reopen in 10s if nothing is entered.", 10);
		}
		
		@Override
		public void checkValidEntry(MinigamePlayer player, String entry) {
			try {
				T value = loadValue(entry);
				Rewards reward = new Rewards();
				
				if (map.containsKey(value)) {
					player.sendMessage("You cannot add duplicate entries", MessageType.Error);
				} else {
					map.put(value, reward);
					player.showPreviousMenu();
				}
			} catch (IllegalArgumentException e) {
				player.sendMessage("Invalid value entry!", MessageType.Error);
			}
		}
	}
}
