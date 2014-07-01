package au.com.mineauz.minigames.config;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemDisplayRewards;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardItem;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.Rewards;

public class RewardsFlag extends Flag<Rewards>{
	
	public RewardsFlag(Rewards value, String name){
		setFlag(value);
		setDefaultFlag(value);
		setName(name);
	}

	@Override
	public void saveValue(String path, FileConfiguration config) {
		if(!getFlag().getRewards().isEmpty() || !getFlag().getGroups().isEmpty()){
			int count = 0;
			for(RewardItem item : getFlag().getRewards()){
				if(item.getItem() != null){
					config.set(path + "." + getName() + "." + count + ".item", item.getItem());
					config.set(path + "." + getName() + "." + count + ".rarity", item.getRarity().toString());
				}
				else if(item.getMoney() != 0){
					config.set(path + "." + getName() + "." + count + ".money", item.getMoney());
					config.set(path + "." + getName() + "." + count + ".rarity", item.getRarity().toString());
				}
				count++;
			}
			for(RewardGroup group : getFlag().getGroups()){
				count = 0;
				for(RewardItem item : group.getItems()){
					if(item.getItem() != null){
						config.set(path + "." + getName() + "." + group.getName() + "." + count + ".item", item.getItem());
					}
					else if(item.getMoney() != 0){
						config.set(path + "." + getName() + "." + group.getName() + "." + count + ".money", item.getMoney());
					}
					count++;
				}
				config.set(path + "." + getName() + "." + group.getName() + ".rarity", group.getRarity().toString());
			}
		}
	}

	@Override
	public void loadValue(String path, FileConfiguration config) {
		Set<String> keys = config.getConfigurationSection(path + "." + getName()).getKeys(false);
		for(String key : keys){
			if(config.contains(path + "." + getName() + "." + key + ".item") || 
					config.contains(path + "." + getName() + "." + key + ".money")){
				ItemStack item = config.getItemStack(path + "." + getName() + "." + key + ".item");
				double money = config.getDouble(path + "." + getName() + "." + key + ".money");
				RewardRarity rarity = RewardRarity.valueOf(config.getString(path + "." + getName() + "." + key + ".rarity"));
				if(item != null)
					getFlag().addItem(item, rarity);
				else
					getFlag().addMoney(money, rarity);
			}
			else{
				Set<String> keys2 = config.getConfigurationSection(path + "." + getName() + "." + key).getKeys(false);
				RewardGroup group = getFlag().addGroup(key, RewardRarity.valueOf(config.getString(path + "." + getName() + "." + key + ".rarity")));
				for(String key2 : keys2){
					if(!key2.equals("rarity")){
						ItemStack item = config.getItemStack(path + "." + getName() + "." + key + "." + key2 + ".item");
						double money = config.getDouble(path + "." + getName() + "." + key + "." + key2 + ".money");
						RewardRarity rarity = RewardRarity.NORMAL;
						if(item != null){
							RewardItem it = new RewardItem(item, rarity);
							group.addItem(it);
						}
						else{
							RewardItem it = new RewardItem(money, rarity);
							group.addItem(it);
						}
					}
				}
			}
		}
	}

	@Override
	public MenuItem getMenuItem(String name, Material displayItem) {
		return new MenuItemDisplayRewards(name, displayItem, getFlag());
	}

	@Override
	public MenuItem getMenuItem(String name, Material displayItem,
			List<String> description) {
		return new MenuItemDisplayRewards(name, description, displayItem, getFlag());
	}

}
