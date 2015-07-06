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

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.EnumFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.InteractionInterface;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import au.com.mineauz.minigames.sql.SQLPlayer;

public class ScoreRewardScheme implements RewardScheme {
	private EnumFlag<ScoreComparison> comparisonType;
	private BooleanFlag enableRewardsOnLoss;
	private BooleanFlag lossUsesSecondary;
	
	private TreeMap<Integer, Rewards> primaryRewards;
	private TreeMap<Integer, Rewards> secondaryRewards;
	
	public ScoreRewardScheme() {
		primaryRewards = new TreeMap<Integer, Rewards>();
		secondaryRewards = new TreeMap<Integer, Rewards>();
		
		comparisonType = new EnumFlag<ScoreComparison>(ScoreComparison.Greater, "comparison");
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
		menu.addItem(new MenuItemList("Comparison Type", Material.REDSTONE_COMPARATOR, getConfigurationTypeCallback(), Lists.transform(Arrays.asList(ScoreComparison.values()), Functions.toStringFunction())));
		menu.addItem(enableRewardsOnLoss.getMenuItem("Award On Loss", Material.LEVER, MinigameUtils.stringToList("When on, awards will still;be given to losing;players")));
		menu.addItem(lossUsesSecondary.getMenuItem("Losers Get Secondary", Material.LEVER, MinigameUtils.stringToList("When on, the losers;will only get the;secondary reward")));
		menu.addItem(new MenuItemNewLine());
		
		MenuItemCustom primary = new MenuItemCustom("Primary Rewards", Material.CHEST);
		primary.setClick(new InteractionInterface() {
			@Override
			public Object interact(Object object) {
				showRewardsMenu(primaryRewards, menu.getViewer(), menu);
				return null;
			}
		});
		
		MenuItemCustom secondary = new MenuItemCustom("Secondary Rewards", Material.CHEST);
		secondary.setClick(new InteractionInterface() {
			@Override
			public Object interact(Object object) {
				showRewardsMenu(secondaryRewards, menu.getViewer(), menu);
				return null;
			}
		});
		
		menu.addItem(primary);
		menu.addItem(secondary);
	}
	
	private void showRewardsMenu(TreeMap<Integer, Rewards> rewards, MinigamePlayer player, Menu parent) {
		Menu submenu = new Menu(6, "Rewards", player);
		
		for (Integer score : rewards.keySet()) {
			submenu.addItem(new MenuItemRewardPair(rewards, score, Material.CHEST));
		}
		
		submenu.addItem(new MenuItemAddReward(rewards, "Add Reward Set", Material.ITEM_FRAME), submenu.getSize()-2);
		submenu.addItem(new MenuItemBack(parent), submenu.getSize()-1);
		
		submenu.setPreviousPage(parent);
		
		submenu.displayMenu(player);
	}
	
	@Override
	public void awardPlayer(MinigamePlayer player, SQLPlayer data, Minigame minigame, boolean firstCompletion) {
		int score = data.getScore();
		Rewards reward;
		
		TreeMap<Integer, Rewards> rewards = (firstCompletion ? primaryRewards : secondaryRewards);
		
		// Calculate rewards
		switch(comparisonType.getFlag()) {
		case Equal:
			reward = rewards.get(score);
			break;
		case Lesser:
			reward = null;
			for (Entry<Integer, Rewards> entry : rewards.entrySet()) {
				if (score <= entry.getKey()) {
					reward = entry.getValue();
					break;
				}
			}
			break;
		case Greater:
			reward = null;
			for (Entry<Integer, Rewards> entry : rewards.descendingMap().entrySet()) {
				if (score >= entry.getKey()) {
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
	public void awardPlayerOnLoss(MinigamePlayer player, SQLPlayer data, Minigame minigame) {
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
	
	private void save(TreeMap<Integer, Rewards> map, ConfigurationSection section) {
		for (Entry<Integer, Rewards> entry : map.entrySet()) {
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
	
	private void load(TreeMap<Integer, Rewards> map, ConfigurationSection section) {
		map.clear();
		for (String key : section.getKeys(false)) {
			int score = Integer.parseInt(key);
			
			ConfigurationSection scoreSection = section.getConfigurationSection(key);
			Rewards reward = new Rewards();
			reward.load(scoreSection);
			
			map.put(score, reward);
		}
	}
	
	private Callback<String> getConfigurationTypeCallback() {
		return new Callback<String>() {
			@Override
			public void setValue(String value) {
				comparisonType.setFlag(ScoreComparison.valueOf(value));
			}
			
			@Override
			public String getValue() {
				return comparisonType.getFlag().name();
			}
		};
	}

	public enum ScoreComparison {
		Greater,
		Equal,
		Lesser
	}

	private class MenuItemRewardPair extends MenuItem {
		private Rewards reward;
		private int score;
		private TreeMap<Integer, Rewards> map;
		
		public MenuItemRewardPair(TreeMap<Integer, Rewards> map, int score, Material displayItem) {
			super(String.valueOf(score), displayItem);
			
			this.map = map;
			this.score = score;
			this.reward = map.get(score);
			
			updateDescription();
		}
		
		private void updateDescription() {
			List<String> description = Arrays.asList(
					ChatColor.GREEN + "Score: " + score,
					"Shift + Left click to edit rewards",
					"Shift + Right click to remove"
					);
			
			setDescription(description);
			
			// Update name
			ItemStack item = getItem();
			ItemMeta meta = item.getItemMeta();
			
			meta.setDisplayName(String.valueOf(score));
			item.setItemMeta(meta);
			
			setItem(item);
		}
		
		private void updateScore(int newScore) {
			map.remove(score);
			score = newScore;
			map.put(score, reward);
		}
		
		@Override
		// Increase score
		public ItemStack onClick() {
			int nextScore = score + 1;
			while (map.containsKey(nextScore)) {
				++nextScore;
			}
			
			updateScore(nextScore);
			
			updateDescription();
			return getItem();
		}
		
		@Override
		// Decrease score
		public ItemStack onRightClick(){
			int nextScore = score - 1;
			while (map.containsKey(nextScore)) {
				--nextScore;
			}
			
			updateScore(nextScore);
			
			updateDescription();
			return getItem();
		}
		
		@Override
		// Open editor
		public ItemStack onDoubleClick() {
			MinigamePlayer ply = getContainer().getViewer();
			ply.setNoClose(true);
			ply.getPlayer().closeInventory();
			ply.sendMessage("Enter the required score into chat, the menu will automatically reopen in 10s if nothing is entered.", null);
			
			ply.setManualEntry(this);
			getContainer().startReopenTimer(10);
			
			return null;
		}
		
		@Override
		public void checkValidEntry(String entry){
			if(entry.matches("[0-9]+")){
				int score = Integer.parseInt(entry);
				if (map.containsKey(score)) {
					getContainer().getViewer().sendMessage("You cannot add duplicate entries", "error");
				} else {
					updateScore(score);
					updateDescription();
				}
			} else {
				getContainer().getViewer().sendMessage("Invalid value entry!", "error");
			}
			
			getContainer().cancelReopenTimer();
			getContainer().displayMenu(getContainer().getViewer());
		}
		
		@Override
		// Open rewards
		public ItemStack onShiftClick() {
			Menu rewardMenu = reward.createMenu(getName(), getContainer().getViewer(), getContainer());
			
			rewardMenu.displayMenu(getContainer().getViewer());
			return null;
		}
		
		@Override
		// Remove
		public ItemStack onShiftRightClick() {
			getContainer().removeItem(getSlot());
			map.remove(score);

			return getItem();
		}
	}
	
	private class MenuItemAddReward extends MenuItem {
		private TreeMap<Integer, Rewards> map;
		
		public MenuItemAddReward(TreeMap<Integer, Rewards> map, String name, Material displayItem) {
			super(name, displayItem);
			
			this.map = map;
		}
		
		@Override
		public ItemStack onClick() {
			MinigamePlayer ply = getContainer().getViewer();
			ply.setNoClose(true);
			ply.getPlayer().closeInventory();
			ply.sendMessage("Enter the required score into chat, the menu will automatically reopen in 10s if nothing is entered.", null);
			
			ply.setManualEntry(this);
			getContainer().startReopenTimer(10);
			
			return null;
		}
		
		@Override
		public void checkValidEntry(String entry) {
			boolean show = true;
			if(entry.matches("[0-9]+")){
				int score = Integer.parseInt(entry);
				Rewards reward = new Rewards();
				if (map.containsKey(score)) {
					getContainer().getViewer().sendMessage("You cannot add a duplicate score", "error");
				} else {
					map.put(score, reward);
					showRewardsMenu(map, getContainer().getViewer(), getContainer().getPreviousPage());
					show = false;
				}
			} else {
				getContainer().getViewer().sendMessage("Invalid value entry!", "error");
			}
			
			getContainer().cancelReopenTimer();
			if (show) {
				getContainer().displayMenu(getContainer().getViewer());
			}
		}
	}
}
