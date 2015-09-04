package au.com.mineauz.minigames.minigame.reward;

import org.bukkit.configuration.ConfigurationSection;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.properties.Properties;
import au.com.mineauz.minigames.properties.Property;

public abstract class RewardType {
	private Property<RewardRarity> rarity = Properties.create(RewardRarity.NORMAL);
	private Rewards rewards;
	
	public RewardType(Rewards rewards){
		this.rewards = rewards;
	}
	
	public RewardRarity getRarity() {
		return rarity.getValue();
	}
	
	public void setRarity(RewardRarity rarity) {
		this.rarity.setValue(rarity);
	}
	
	public Property<RewardRarity> getRarityProperty() {
		return rarity;
	}
	
	public Rewards getRewards(){
		return rewards;
	}
	
	public abstract String getName();
	public abstract boolean isUsable();
	public abstract void giveReward(MinigamePlayer player);
	public abstract MenuItem getMenuItem();
	public abstract void saveReward(String path, ConfigurationSection section);
	public abstract void loadReward(String path, ConfigurationSection section);
}
