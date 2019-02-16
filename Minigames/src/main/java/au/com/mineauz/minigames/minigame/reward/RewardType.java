package au.com.mineauz.minigames.minigame.reward;

import org.bukkit.configuration.ConfigurationSection;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.menu.MenuItem;

public abstract class RewardType {
    private RewardRarity rarity = RewardRarity.NORMAL;
    private Rewards rewards;

    public RewardType(Rewards rewards) {
        this.rewards = rewards;
    }

    public RewardRarity getRarity() {
        return rarity;
    }

    public void setRarity(RewardRarity rarity) {
        this.rarity = rarity;
    }

    public Rewards getRewards() {
        return rewards;
    }

    public abstract String getName();

    public abstract boolean isUsable();

    public abstract void giveReward(MinigamePlayer player);

    public abstract MenuItem getMenuItem();

    public abstract void saveReward(String path, ConfigurationSection section);

    public abstract void loadReward(String path, ConfigurationSection section);
}
