package au.com.mineauz.minigames.minigame.reward;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Rewards {

    private final List<RewardType> items = new ArrayList<>();
    private final List<RewardGroup> groups = new ArrayList<>();

    public boolean isEmpty() {
        return items.isEmpty() && groups.isEmpty();
    }

    public List<RewardType> getReward() {
        double rand = ThreadLocalRandom.current().nextDouble();
        RewardRarity rarity;
        List<Object> itemsCopyList = new ArrayList<>();
        itemsCopyList.addAll(items);
        itemsCopyList.addAll(groups);
        Collections.shuffle(itemsCopyList);

        if (rand > RewardRarity.VERY_COMMON.getRarity()) {
            rarity = RewardRarity.VERY_COMMON;
        } else if (rand > RewardRarity.COMMON.getRarity()) {
            rarity = RewardRarity.COMMON;
        } else if (rand > RewardRarity.NORMAL.getRarity()) {
            rarity = RewardRarity.NORMAL;
        } else if (rand > RewardRarity.RARE.getRarity()) {
            rarity = RewardRarity.RARE;
        } else {
            rarity = RewardRarity.VERY_RARE;
        }

        if (!itemsCopyList.isEmpty()) {
            RewardType item = null;
            RewardGroup group = null;
            final RewardRarity originalRarity = rarity;
            boolean up = false;

            while (item == null && group == null) {
                for (Object ritem : itemsCopyList) {
                    if (ritem instanceof RewardType ri) {
                        if (ri.getRarity() == rarity) {
                            item = ri;
                            break;
                        }
                    } else {
                        RewardGroup rg = (RewardGroup) ritem;
                        if (rg.getRarity() == rarity) {
                            group = rg;
                            break;
                        }
                    }
                }

                // nothing in the list with the same rarity
                // only go up if there is no way further down
                if (rarity == RewardRarity.VERY_COMMON && !up) {
                    rarity = originalRarity;
                    up = true;
                }

                if (up) {
                    rarity = rarity.getHigherRarity();
                } else {
                    rarity = rarity.getLowerRarity();
                }
            }

            if (item != null) {
                return List.of(item);
            } else {
                return group.getItems();
            }
        }

        return null;
    }

    public void addReward(RewardType reward) {
        items.add(reward);
    }

    public void removeReward(RewardType item) {
        items.remove(item);
    }

    public List<RewardType> getRewards() {
        return items;
    }

    public RewardGroup addGroup(String groupName, RewardRarity rarity) {
        RewardGroup group = new RewardGroup(groupName, rarity);
        groups.add(group);
        return group;
    }

    public void removeGroup(RewardGroup group) {
        groups.remove(group);
    }

    public List<RewardGroup> getGroups() {
        return groups;
    }

    public Menu createMenu(Component name, MinigamePlayer player, Menu parent) {
        Menu rewardMenu = new Menu(5, name, player);

        rewardMenu.setPreviousPage(parent);

        rewardMenu.addItem(new MenuItemRewardGroupAdd(MenuUtility.getCreateMaterial(),
                MgMenuLangKey.MENU_REWARD_GROUP_ADD_NAME, this), 42);
        rewardMenu.addItem(new MenuItemRewardAdd(MenuUtility.getCreateMaterial(), MgMenuLangKey.MENU_REWARD_ITEM_ADD_NAME, this), 43);
        rewardMenu.addItem(new MenuItemPage(MenuUtility.getSaveMaterial(),
                MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_REWARD_SAVE_NAME,
                        Placeholder.component(MinigamePlaceHolderKey.REWARD.getKey(), name)),
                parent), 44);

        List<MenuItem> mi = new ArrayList<>();
        for (RewardType item : items) {
            mi.add(item.getMenuItem());
        }

        List<Component> des = MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_EDIT_SHIFTLEFT);
        for (RewardGroup group : groups) {
            MenuItemRewardGroup rwg = new MenuItemRewardGroup(Material.CHEST,
                    MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_REWARD_GROUP_NAME,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), group.getName())),
                    des, group, this);
            mi.add(rwg);
        }
        rewardMenu.addItems(mi);

        return rewardMenu;
    }

    public void save(ConfigurationSection section) {
        int index = 0;
        for (RewardType item : items) {
            ConfigurationSection itemSection = section.createSection(String.valueOf(index));
            itemSection.set("type", item.getName());
            itemSection.set("rarity", item.getRarity().name());
            item.saveReward("data", itemSection);
            index++;
        }

        for (RewardGroup group : groups) {
            ConfigurationSection groupSection = section.createSection(group.getName());
            group.save(groupSection);
        }
    }

    public void load(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            // Load reward item
            if (section.contains(key + ".type")) {
                ConfigurationSection itemSection = section.getConfigurationSection(key);
                RewardType rew = RewardTypes.getRewardType(itemSection.getString("type"), this);
                rew.loadReward("data", itemSection);
                rew.setRarity(RewardRarity.valueOf(itemSection.getString("rarity")));
                addReward(rew);
                // Load reward group
            } else {
                ConfigurationSection groupSection = section.getConfigurationSection(key);
                groups.add(RewardGroup.load(groupSection, this));
            }
        }
    }
}
