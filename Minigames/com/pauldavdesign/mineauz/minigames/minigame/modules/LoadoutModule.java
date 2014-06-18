package com.pauldavdesign.mineauz.minigames.minigame.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.PlayerLoadout;
import com.pauldavdesign.mineauz.minigames.config.Flag;
import com.pauldavdesign.mineauz.minigames.menu.InteractionInterface;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemCustom;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.MinigameModule;

public class LoadoutModule implements MinigameModule {
	
	private PlayerLoadout defaultLoadout = new PlayerLoadout("default");
	private Map<String, PlayerLoadout> extraLoadouts = new HashMap<String, PlayerLoadout>();

	@Override
	public String getName() {
		return "Loadouts";
	}
	
	@Override
	public Map<String, Flag<?>> getFlags(){
		return null;
	}
	
	@Override
	public boolean useSeparateConfig(){
		return false;
	}

	@Override
	public void save(Minigame minigame, FileConfiguration config) {
		if(hasDefaultLoadout()){
			for(Integer slot : getDefaultPlayerLoadout().getItems()){
				config.set(minigame + ".loadout." + slot, getDefaultPlayerLoadout().getItem(slot));
			}
			
			if(!getDefaultPlayerLoadout().getAllPotionEffects().isEmpty()){
				for(PotionEffect eff : getDefaultPlayerLoadout().getAllPotionEffects()){
					config.set(minigame + ".loadout.potions." + eff.getType().getName() + ".amp", eff.getAmplifier());
					config.set(minigame + ".loadout.potions." + eff.getType().getName() + ".dur", eff.getDuration());
				}
			}
			else{
				config.set(minigame + ".loadout.potions", null);
			}
			if(getDefaultPlayerLoadout().getUsePermissions()){
				config.set(minigame + ".loadout.usepermissions", true);
			}
			else{
				config.set(minigame + ".loadout.usepermissions", null);
			}
			
			if(!getDefaultPlayerLoadout().hasFallDamage())
				config.set(minigame + ".loadout.falldamage", getDefaultPlayerLoadout().hasFallDamage());
			else
				config.set(minigame + ".loadout.falldamage", null);
			
			if(getDefaultPlayerLoadout().hasHunger())
				config.set(minigame + ".loadout.hunger", getDefaultPlayerLoadout().hasHunger());
			else
				config.set(minigame + ".loadout.hunger", null);
		}
		
		if(hasLoadouts()){
			for(String loadout : getLoadouts()){
				for(Integer slot : getLoadout(loadout).getItems()){
					config.set(minigame + ".extraloadouts." + loadout + "." + slot, getLoadout(loadout).getItem(slot));
				}
				if(!getLoadout(loadout).getAllPotionEffects().isEmpty()){
					for(PotionEffect eff : getLoadout(loadout).getAllPotionEffects()){
						config.set(minigame + ".extraloadouts." + loadout + ".potions." + eff.getType().getName() + ".amp", eff.getAmplifier());
						config.set(minigame + ".extraloadouts." + loadout + ".potions." + eff.getType().getName() + ".dur", eff.getDuration());
					}
				}
				else{
					config.set(minigame + ".extraloadouts." + loadout + ".potions", null);
				}
				
				if(getLoadout(loadout).getUsePermissions()){
					config.set(minigame + ".extraloadouts." + loadout + ".usepermissions", true);
				}
				else{
					config.set(minigame + ".extraloadouts." + loadout + ".usepermissions", null);
				}
				
				if(!getLoadout(loadout).hasFallDamage())
					config.set(minigame + ".extraloadouts." + loadout + ".falldamage", getLoadout(loadout).hasFallDamage());
				else
					config.set(minigame + ".extraloadouts." + loadout + ".falldamage", null);
				
				if(getLoadout(loadout).hasHunger())
					config.set(minigame + ".extraloadouts." + loadout + ".hunger", getLoadout(loadout).hasHunger());
				else
					config.set(minigame + ".extraloadouts." + loadout + ".hunger", null);
			}
		}
	}

	@Override
	public void load(Minigame minigame, FileConfiguration config) {
		if(config.contains(minigame + ".loadout")){
			Set<String> keys = config.getConfigurationSection(minigame + ".loadout").getKeys(false);
			for(String key : keys){
				if(key.matches("[0-9]+"))
					getDefaultPlayerLoadout().addItem(config.getItemStack(minigame + ".loadout." + key), Integer.parseInt(key));
			}
			
			if(config.contains(minigame + ".loadout.potions")){
				keys = config.getConfigurationSection(minigame + ".loadout.potions").getKeys(false);
				for(String eff : keys){
					if(PotionEffectType.getByName(eff) != null){
						PotionEffect effect = new PotionEffect(PotionEffectType.getByName(eff),
								config.getInt(minigame + ".loadout.potions." + eff + ".dur"),
								config.getInt(minigame + ".loadout.potions." + eff + ".amp"), true);
						getDefaultPlayerLoadout().addPotionEffect(effect);
					}
				}
			}
			
			if(config.contains(minigame + ".loadout.usepermissions")){
				getDefaultPlayerLoadout().setUsePermissions(config.getBoolean(minigame + ".loadout.usepermissions"));
			}
			
			if(config.contains(minigame + ".loadout.falldamage")){
				getDefaultPlayerLoadout().setHasFallDamage(config.getBoolean(minigame + ".loadout.falldamage"));
			}
			if(config.contains(minigame + ".loadout.hunger")){
				getDefaultPlayerLoadout().setHasHunger(config.getBoolean(minigame + ".loadout.hunger"));
			}
		}
		if(config.contains(minigame + ".extraloadouts")){
			Set<String> keys = config.getConfigurationSection(minigame + ".extraloadouts").getKeys(false);
			for(String loadout : keys){
				addLoadout(loadout);
				Set<String> items = config.getConfigurationSection(minigame + ".extraloadouts." + loadout).getKeys(false);
				for(String key : items){
					if(key.matches("[0-9]+"))
						getLoadout(loadout).addItem(config.getItemStack(minigame + ".extraloadouts." + loadout + "." + key), Integer.parseInt(key));
				}
				if(config.contains(minigame + ".extraloadouts." + loadout + ".potions")){
					Set<String> pots = config.getConfigurationSection(minigame + ".extraloadouts." + loadout + ".potions").getKeys(false);
					for(String eff : pots){
						if(PotionEffectType.getByName(eff) != null){
							PotionEffect effect = new PotionEffect(PotionEffectType.getByName(eff),
									config.getInt(minigame + ".extraloadouts." + loadout + ".potions." + eff + ".dur"),
									config.getInt(minigame + ".extraloadouts." + loadout + ".potions." + eff + ".amp"));
							getLoadout(loadout).addPotionEffect(effect);
						}
					}
				}
				
				if(config.contains(minigame + ".extraloadouts." + loadout + ".usepermissions")){
					getLoadout(loadout).setUsePermissions(config.getBoolean(minigame + ".extraloadouts." + loadout + ".usepermissions"));
				}
				
				if(config.contains(minigame + ".extraloadouts." + loadout + ".falldamage"))
					getLoadout(loadout).setHasFallDamage(config.getBoolean(minigame + ".extraloadouts." + loadout + ".falldamage"));
				
				if(config.contains(minigame + ".extraloadouts." + loadout + ".hunger"))
					getLoadout(loadout).setHasHunger(config.getBoolean(minigame + ".extraloadouts." + loadout + ".hunger"));
			}
		}
	}
	
	public static LoadoutModule getMinigameModule(Minigame minigame){
		return (LoadoutModule) minigame.getModule("Loadouts");
	}
	
	public PlayerLoadout getDefaultPlayerLoadout(){
		return defaultLoadout;
	}
	
	public boolean hasDefaultLoadout(){
		if(defaultLoadout.getItems().isEmpty() && defaultLoadout.getAllPotionEffects().isEmpty() && 
				defaultLoadout.hasFallDamage() && !defaultLoadout.hasHunger()){
			return false;
		}
		return true;
	}
	
	public void addLoadout(String name){
		extraLoadouts.put(name, new PlayerLoadout(name));
	}
	
	public void deleteLoadout(String name){
		if(extraLoadouts.containsKey(name)){
			extraLoadouts.remove(name);
		}
	}
	
	public Set<String> getLoadouts(){
		return extraLoadouts.keySet();
	}
	
	public Map<String, PlayerLoadout> getLoadoutMap(){
		return extraLoadouts;
	}
	
	public PlayerLoadout getLoadout(String name){
		PlayerLoadout pl = null;
		if(name.equalsIgnoreCase("default")){
			pl = getDefaultPlayerLoadout();
		}
		else{
			if(extraLoadouts.containsKey(name)){
				pl = extraLoadouts.get(name);
			}
		}
		return pl;
	}
	
	public boolean hasLoadouts(){
		if(extraLoadouts.isEmpty()){
			return false;
		}
		return true;
	}
	
	public boolean hasLoadout(String name){
		if(!name.equalsIgnoreCase("default")){
			return extraLoadouts.containsKey(name);
		}
		else{
			return true;
		}
	}
	
	public void displaySelectionMenu(MinigamePlayer player, final boolean equip){
		Menu m = new Menu(6, "Select Loadout", player);
		MenuItemCustom d = new MenuItemCustom(getDefaultPlayerLoadout().getName(), Material.GLASS);
		if(!getDefaultPlayerLoadout().getItems().isEmpty())
			d.setItem(getDefaultPlayerLoadout().getItem(new ArrayList<Integer>(getDefaultPlayerLoadout().getItems()).get(0)));
		final MinigamePlayer fply = player;
		final PlayerLoadout floadout = getDefaultPlayerLoadout();
		d.setClick(new InteractionInterface() {
			
			@Override
			public Object interact(Object object) {
				fply.setLoadout(floadout);
				fply.getPlayer().closeInventory();
				if(!equip)
					fply.sendMessage(MinigameUtils.getLang("player.loadout.nextSpawn"), null);
				else{
					fply.sendMessage(MinigameUtils.formStr("player.loadout.equipped", floadout.getName()), null);
					floadout.equiptLoadout(fply);
				}
				return null;
			}
		});
		m.addItem(d);
		
		for(PlayerLoadout loadout : extraLoadouts.values()){
			if(!loadout.getUsePermissions() || player.getPlayer().hasPermission("minigame.loadout." + loadout.getName().toLowerCase())){
				MenuItemCustom c = new MenuItemCustom(loadout.getName(), Material.GLASS);
				if(!loadout.getItems().isEmpty())
					c.setItem(loadout.getItem(new ArrayList<Integer>(loadout.getItems()).get(0)));
				final PlayerLoadout floadout2 = loadout;
				c.setClick(new InteractionInterface() {
					
					@Override
					public Object interact(Object object) {
						fply.setLoadout(floadout2);
						fply.getPlayer().closeInventory();
						if(!equip)
							fply.sendMessage(MinigameUtils.getLang("player.loadout.nextSpawn"), null);
						else{
							fply.sendMessage(MinigameUtils.formStr("player.loadout.equipped", floadout2.getName()), null);
							floadout2.equiptLoadout(fply);
						}
						return null;
					}
				});
				m.addItem(c);
			}
		}
		m.displayMenu(player);
	}

	@Override
	public void addMenuOptions(Menu menu, Minigame minigame) {
		// TODO Auto-generated method stub
		
	}

}
