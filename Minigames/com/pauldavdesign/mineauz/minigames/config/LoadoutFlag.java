package com.pauldavdesign.mineauz.minigames.config;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.pauldavdesign.mineauz.minigames.PlayerLoadout;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;

public class LoadoutFlag extends Flag<PlayerLoadout>{
	
	public LoadoutFlag(PlayerLoadout value, String name){
		setFlag(value);
		setDefaultFlag(null);
		setName(name);
	}

	@Override
	public void saveValue(String path, FileConfiguration config) {
		for(Integer slot : getFlag().getItems()){
			config.set(path + "." + getFlag().getName() + ".items." + slot, getFlag().getItem(slot));
		}
		
		for(PotionEffect eff : getFlag().getAllPotionEffects()){
			config.set(path + "." + getFlag().getName() + ".potions." + eff.getType().getName() + ".amp", eff.getAmplifier());
			config.set(path + "." + getFlag().getName() + ".potions." + eff.getType().getName() + ".dur", eff.getDuration());
		}
		
		if(getFlag().getUsePermissions()){
			config.set(path + "." + getFlag().getName() + ".usepermissions", true);
		}
		
		if(!getFlag().hasFallDamage())
			config.set(path + "." + getFlag().getName() + ".falldamage", getFlag().hasFallDamage());
		
		if(getFlag().hasHunger())
			config.set(path + "." + getFlag().getName() + ".hunger", getFlag().hasHunger());
	}

	@Override
	public void loadValue(String path, FileConfiguration config) {
		Set<String> items = config.getConfigurationSection(path + getFlag().getName() + ".items").getKeys(false);
		for(String key : items){
			if(key.matches("[0-9]+"))
				getFlag().addItem(config.getItemStack(path + getFlag().getName() + ".items." + key), Integer.parseInt(key));
		}
		if(config.contains(path + getFlag().getName() + ".potions")){
			Set<String> pots = config.getConfigurationSection(path + getFlag().getName() + ".potions").getKeys(false);
			for(String eff : pots){
				if(PotionEffectType.getByName(eff) != null){
					PotionEffect effect = new PotionEffect(PotionEffectType.getByName(eff),
							config.getInt(path + getFlag().getName() + ".potions." + eff + ".dur"),
							config.getInt(path + getFlag().getName() + ".potions." + eff + ".amp"));
					getFlag().addPotionEffect(effect);
				}
			}
		}
		
		if(config.contains(path + getFlag().getName() + ".usepermissions")){
			getFlag().setUsePermissions(config.getBoolean(path + getFlag().getName() + ".usepermissions"));
		}
		
		if(config.contains(path + getFlag().getName() + ".falldamage"))
			getFlag().setHasFallDamage(config.getBoolean(path + getFlag().getName() + ".falldamage"));
		
		if(config.contains(path + getFlag().getName() + ".hunger"))
			getFlag().setHasHunger(config.getBoolean(path + getFlag().getName() + ".hunger"));
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
