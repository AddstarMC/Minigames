package au.com.mineauz.minigames.minigame.reward;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RewardGroup {

    private String groupName;
    //    private List<RewardItem> items = new ArrayList<RewardItem>();
    private List<RewardType> items = new ArrayList<>();
    private RewardRarity rarity;

    public RewardGroup(String groupName, RewardRarity rarity) {
        this.groupName = groupName;
        this.rarity = rarity;
    }

    public static RewardGroup load(ConfigurationSection section, Rewards container) {
        RewardRarity rarity = RewardRarity.valueOf(section.getString("rarity"));
        RewardGroup group = new RewardGroup(section.getName(), rarity);

        // Load contents
        for (String key : section.getKeys(false)) {
            if (key.equals("rarity")) {
                continue;
            }

            ConfigurationSection itemSection = section.getConfigurationSection(key);

            // Upgrade from pre 1.7
            // TODO: Remove after 1.7 release
            if (!itemSection.contains("data")) {
                ItemStack item = itemSection.getItemStack("item");
                if (item != null) {
                    RewardType it = RewardTypes.getRewardType("ITEM", container);
                    it.loadReward("item", itemSection);
                    group.addItem(it);
                } else {
                    RewardType it = RewardTypes.getRewardType("MONEY", container);
                    it.loadReward("money", itemSection);
                    group.addItem(it);
                }
            } else {
                RewardType rew = RewardTypes.getRewardType(itemSection.getString("type"), container);
                rew.loadReward("data", itemSection);
                group.addItem(rew);
            }
        }

        return group;
    }

    public String getName() {
        return groupName;
    }

    public void addItem(RewardType item) {
        items.add(item);
    }

    public void removeItem(RewardType item) {
        items.remove(item);
    }

    public List<RewardType> getItems() {
        return items;
    }

    public RewardRarity getRarity() {
        return rarity;
    }

    public void setRarity(RewardRarity rarity) {
        this.rarity = rarity;
    }

    public void clearGroup() {
        items.clear();
    }

    public void save(ConfigurationSection section) {
        int index = 0;
        for (RewardType item : items) {
            ConfigurationSection itemSection = section.createSection(String.valueOf(index));
            itemSection.set("type", item.getName());
            item.saveReward("data", itemSection);
            index++;
        }

        section.set("rarity", rarity.name());
    }
}
