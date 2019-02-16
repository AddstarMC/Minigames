package au.com.mineauz.minigames.minigame.reward;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.menu.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rewards {

    private List<RewardType> items = new ArrayList<>();
    private List<RewardGroup> groups = new ArrayList<>();

    public boolean isEmpty() {
        return items.isEmpty() && groups.isEmpty();
    }

    public List<RewardType> getReward() {
        double rand = Math.random();
        RewardRarity rarity = null;
        List<Object> itemsCopy = new ArrayList<>();
        itemsCopy.addAll(items);
        itemsCopy.addAll(groups);
        Collections.shuffle(itemsCopy);

        if (rand > RewardRarity.VERY_COMMON.getRarity())
            rarity = RewardRarity.VERY_COMMON;
        else if (rand > RewardRarity.COMMON.getRarity())
            rarity = RewardRarity.COMMON;
        else if (rand > RewardRarity.NORMAL.getRarity())
            rarity = RewardRarity.NORMAL;
        else if (rand > RewardRarity.RARE.getRarity())
            rarity = RewardRarity.RARE;
        else
            rarity = RewardRarity.VERY_RARE;


        if (!itemsCopy.isEmpty()) {
            RewardType item = null;
            RewardGroup group = null;
            RewardRarity orarity = rarity;
            boolean up = true;

            while (item == null && group == null) {
                for (Object ritem : itemsCopy) {
                    if (ritem instanceof RewardType) {
                        RewardType ri = (RewardType) ritem;
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

                if (rarity == RewardRarity.VERY_COMMON) {
                    rarity = orarity;
                    up = false;
                }

                if (up)
                    rarity = rarity.getNextRarity();
                else {
                    rarity = rarity.getPreviousRarity();
                }
            }
            if (item != null) {
                List<RewardType> items = new ArrayList<>();
                items.add(item);
                return items;
            } else if (group != null) {
                return group.getItems();
            }
        }

        return null;
    }

    public void addReward(RewardType reward) {
        items.add(reward);
    }

//    public RewardItem addItem(ItemStack item, RewardRarity rarity){
//        RewardItem ritem = new RewardItem(item, rarity);
//        items.add(ritem);
//        return ritem;
//    }
//
//    public RewardItem addMoney(double money, RewardRarity rarity){
//        RewardItem ritem = new RewardItem(money, rarity);
//        items.add(ritem);
//        return ritem;
//    }

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

    public Menu createMenu(String name, MinigamePlayer player, Menu parent) {
        Menu rewardMenu = new Menu(5, name, player);

        rewardMenu.setPreviousPage(parent);

        rewardMenu.addItem(new MenuItemRewardGroupAdd("Add Group", MenuUtility.getCreateMaterial(), this), 42);
        rewardMenu.addItem(new MenuItemRewardAdd("Add Item", MenuUtility.getCreateMaterial(), this), 43);
        rewardMenu.addItem(new MenuItemPage("Save " + name, MenuUtility.getSaveMaterial(), parent), 44);

        List<MenuItem> mi = new ArrayList<>();
        for (RewardType item : items) {
            mi.add(item.getMenuItem());
        }
        List<String> des = new ArrayList<>();
        des.add("Double Click to edit");
        for (RewardGroup group : groups) {
            MenuItemRewardGroup rwg = new MenuItemRewardGroup(group.getName() + " Group", des, Material.CHEST, group, this);
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
            // Upgrade from pre 1.7
            //TODO: Remove after 1.7 release
            if (section.contains(key + ".item") || section.contains(key + ".money")) {
                ItemStack item = section.getItemStack(key + ".item");
                if (item != null) {
                    RewardType ir = RewardTypes.getRewardType("ITEM", this);
                    ir.loadReward(key + ".item", section);
                    ir.setRarity(RewardRarity.valueOf(section.getString(key + ".rarity")));
                    addReward(ir);
                } else {
                    RewardType ir = RewardTypes.getRewardType("MONEY", this);
                    ir.loadReward(key + ".money", section);
                    ir.setRarity(RewardRarity.valueOf(section.getString(key + ".rarity")));
                    addReward(ir);
                }
                // Load reward item
            } else if (section.contains(key + ".type")) {
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
