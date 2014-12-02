package au.com.mineauz.minigames.config;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemDisplayRewards;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.RewardTypes;
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
			for(RewardType item : getFlag().getRewards()){
				config.set(path + "." + getName() + "." + count + ".type", item.getName());
				config.set(path + "." + getName() + "." + count + ".rarity", item.getRarity().toString());
				item.saveReward(path + "." + getName() + "." + count + ".data", config);
				count++;
			}
			for(RewardGroup group : getFlag().getGroups()){
				count = 0;
				for(RewardType item : group.getItems()){
					config.set(path + "." + getName() + "." + group.getName() + "." + count + ".type", item.getName());
					item.saveReward(path + "." + getName() + "." + group.getName() + "." + count + ".data", config);
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
					config.contains(path + "." + getName() + "." + key + ".money")){ //TODO: Remove after 1.7 release
				ItemStack item = config.getItemStack(path + "." + getName() + "." + key + ".item");
				if(item != null){
					RewardType ir = RewardTypes.getRewardType("ITEM", getFlag());
					ir.loadReward(path + "." + getName() + "." + key + ".item", config);
					ir.setRarity(RewardRarity.valueOf(config.getString(path + "." + getName() + "." + key + ".rarity")));
					getFlag().addReward(ir);
				}
				else{
					RewardType ir = RewardTypes.getRewardType("MONEY", getFlag());
					ir.loadReward(path + "." + getName() + "." + key + ".money", config);
					ir.setRarity(RewardRarity.valueOf(config.getString(path + "." + getName() + "." + key + ".rarity")));
					getFlag().addReward(ir);
				}
			}
			else if(config.contains(path + "." + getName() + "." + key + ".type")){
				String np = path + "." + getName() + "." + key + ".";
				RewardType rew = RewardTypes.getRewardType(config.getString(np + "type"), getFlag());
				rew.loadReward(np + "data", config);
				rew.setRarity(RewardRarity.valueOf(config.getString(np + ".rarity")));
				getFlag().addReward(rew);
			}
			else{
				Set<String> keys2 = config.getConfigurationSection(path + "." + getName() + "." + key).getKeys(false);
				RewardGroup group = getFlag().addGroup(key, RewardRarity.valueOf(config.getString(path + "." + getName() + "." + key + ".rarity")));
				for(String key2 : keys2){
					if(!key2.equals("rarity")){
						if(!config.contains(path + "." + getName() + "." + key + "." + key2 + ".data")){ //TODO: Remove after 1.7 release
							ItemStack item = config.getItemStack(path + "." + getName() + "." + key + "." + key2 + ".item");
							if(item != null){
								RewardType it = RewardTypes.getRewardType("ITEM", getFlag());
								it.loadReward(path + "." + getName() + "." + key + "." + key2 + ".item", config);
								group.addItem(it);
							}
							else{
								RewardType it = RewardTypes.getRewardType("MONEY", getFlag());
								it.loadReward(path + "." + getName() + "." + key + "." + key2 + ".money", config);
								group.addItem(it);
							}
						}
						else{
							String np = path + "." + getName() + "." + key + "." + key2 + ".";
							RewardType rew = RewardTypes.getRewardType(config.getString(np + "type"), getFlag());
							rew.loadReward(np + "data", config);
							group.addItem(rew);
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
