package au.com.mineauz.minigames.minigame.reward.scheme;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemDisplayRewards;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;
import au.com.mineauz.minigames.properties.types.RewardsProperty;
import au.com.mineauz.minigames.stats.StoredGameStats;

/**
 * The standard reward scheme handles the previous reward behaviour.
 * It provides rewards only on victory and has a primary and secondary
 * reward. The primary reward is acquired on the first completion only.
 */
public class StandardRewardScheme implements RewardScheme {
	private final ConfigPropertyContainer properties;
	
	private Rewards primaryReward;
	private final RewardsProperty primaryRewardFlag;
	
	private Rewards secondaryReward;
	private final RewardsProperty secondaryRewardFlag;
	
	public StandardRewardScheme() {
		primaryReward = new Rewards();
		primaryRewardFlag = new RewardsProperty(primaryReward, "reward");
		
		secondaryReward = new Rewards();
		secondaryRewardFlag = new RewardsProperty(secondaryReward, "reward2");
		
		properties = new ConfigPropertyContainer();
		properties.addProperty(primaryRewardFlag);
		properties.addProperty(secondaryRewardFlag);
	}
	
	@Override
	public ConfigPropertyContainer getProperties() {
		return properties;
	}
	
	public Rewards getPrimaryReward() {
		return primaryReward;
	}
	
	public Rewards getSecondaryReward() {
		return secondaryReward;
	}
	
	@Override
	public void awardPlayer(MinigamePlayer player, StoredGameStats data, Minigame minigame, boolean firstCompletion) {
		List<RewardType> rewards = primaryReward.getReward();
		
		if (firstCompletion && rewards != null) {
			MinigameUtils.debugMessage("Issue Primary Reward for " + player.getName());
			giveRewards(rewards, player);
		} else {
			rewards = secondaryReward.getReward();
			if (rewards != null) {
				MinigameUtils.debugMessage("Issue Secondary Reward for " + player.getName());
				giveRewards(rewards, player);
			}
		}
		
		player.updateInventory();
	}
	
	@Override
	public void awardPlayerOnLoss(MinigamePlayer player, StoredGameStats data, Minigame minigame) {
		// No lose awards
	}
	
	private void giveRewards(List<RewardType> rewards, MinigamePlayer player) {
		for(RewardType reward : rewards) {
			if(reward != null) {
				MinigameUtils.debugMessage("Giving " + player.getName() + reward.getName() + " reward type.");
				reward.giveReward(player);
			}
		}
	}
	
	@Override
	public void load(ConfigurationSection config) {}
	
	@Override
	public void save(ConfigurationSection config) {}
	
	@Override
	public void addMenuItems(Menu menu) {
		menu.addItem(new MenuItemDisplayRewards("Primary Rewards", Material.CHEST, primaryReward));
		menu.addItem(new MenuItemDisplayRewards("Secondary Rewards", Material.CHEST, secondaryReward));
	}
}
