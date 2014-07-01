package au.com.mineauz.minigames.config;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.menu.MenuItem;

public class LoadoutFlag extends Flag<PlayerLoadout>{
	
	public LoadoutFlag(PlayerLoadout value, String name){
		setFlag(value);
		setDefaultFlag(null);
		setName(name);
	}

	@Override
	public void saveValue(String path, FileConfiguration config) {
		for(Integer slot : getFlag().getItems()){
			config.set(path + "." + getName() + ".items." + slot, getFlag().getItem(slot));
		}
		
		for(PotionEffect eff : getFlag().getAllPotionEffects()){
			config.set(path + "." + getName() + ".potions." + eff.getType().getName() + ".amp", eff.getAmplifier());
			config.set(path + "." + getName() + ".potions." + eff.getType().getName() + ".dur", eff.getDuration());
		}
		
		if(getFlag().getUsePermissions()){
			config.set(path + "." + getName() + ".usepermissions", true);
		}
		
		if(!getFlag().hasFallDamage())
			config.set(path + "." + getName() + ".falldamage", getFlag().hasFallDamage());
		
		if(getFlag().hasHunger())
			config.set(path + "." + getName() + ".hunger", getFlag().hasHunger());
	}

	@Override
	public void loadValue(String path, FileConfiguration config) {
		Set<String> items = config.getConfigurationSection(path + "." + getName() + ".items").getKeys(false);
		for(String key : items){
			if(key.matches("[0-9]+"))
				getFlag().addItem(config.getItemStack(path + "." + getName() + ".items." + key), Integer.parseInt(key));
		}
		if(config.contains(path + "." + getName() + ".potions")){
			Set<String> pots = config.getConfigurationSection(path + "." + getName() + ".potions").getKeys(false);
			for(String eff : pots){
				if(PotionEffectType.getByName(eff) != null){
					PotionEffect effect = new PotionEffect(PotionEffectType.getByName(eff),
							config.getInt(path + "." + getName() + ".potions." + eff + ".dur"),
							config.getInt(path + "." + getName() + ".potions." + eff + ".amp"));
					getFlag().addPotionEffect(effect);
				}
			}
		}
		
		if(config.contains(path + "." + getName() + ".usepermissions")){
			getFlag().setUsePermissions(config.getBoolean(path + "." + getName() + ".usepermissions"));
		}
		
		if(config.contains(path + "." + getName() + ".falldamage"))
			getFlag().setHasFallDamage(config.getBoolean(path + "." + getName() + ".falldamage"));
		
		if(config.contains(path + "." + getName() + ".hunger"))
			getFlag().setHasHunger(config.getBoolean(path + "." + getName() + ".hunger"));
	}

	@Override
	public MenuItem getMenuItem(String name, Material displayItem) {
		return null; //TODO: Menu item easy access for loadouts.
	}

	@Override
	public MenuItem getMenuItem(String name, Material displayItem,
			List<String> description) {
		return null;
	}

}
