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
import com.pauldavdesign.mineauz.minigames.config.LoadoutSetFlag;
import com.pauldavdesign.mineauz.minigames.menu.InteractionInterface;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemCustom;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.MinigameModule;

public class LoadoutModule implements MinigameModule {
	
	private Map<String, PlayerLoadout> extraLoadouts = new HashMap<String, PlayerLoadout>();
	private LoadoutSetFlag loadoutsFlag = new LoadoutSetFlag(extraLoadouts, "loadouts");
	
	public LoadoutModule(){
		PlayerLoadout def = new PlayerLoadout("default");
		def.setDeleteable(false);
		extraLoadouts.put("default", def);
	}

	@Override
	public String getName() {
		return "Loadouts";
	}
	
	@Override
	public Map<String, Flag<?>> getFlags(){
		Map<String, Flag<?>> flags = new HashMap<String, Flag<?>>();
		flags.put(loadoutsFlag.getName(), loadoutsFlag);
		return flags;
	}
	
	@Override
	public boolean useSeparateConfig(){
		return false;
	}

	@Override
	public void save(Minigame minigame, FileConfiguration config) {
		//Do Nothing
	}

	@Override
	public void load(Minigame minigame, FileConfiguration config) {
		
		//TODO: Remove entire load after 1.7
		if(config.contains(minigame + ".loadout")){
			Set<String> keys = config.getConfigurationSection(minigame + ".loadout").getKeys(false);
			for(String key : keys){
				if(key.matches("[0-9]+"))
					getLoadout("default").addItem(config.getItemStack(minigame + ".loadout." + key), Integer.parseInt(key));
			}
			
			if(config.contains(minigame + ".loadout.potions")){
				keys = config.getConfigurationSection(minigame + ".loadout.potions").getKeys(false);
				for(String eff : keys){
					if(PotionEffectType.getByName(eff) != null){
						PotionEffect effect = new PotionEffect(PotionEffectType.getByName(eff),
								config.getInt(minigame + ".loadout.potions." + eff + ".dur"),
								config.getInt(minigame + ".loadout.potions." + eff + ".amp"), true);
						getLoadout("default").addPotionEffect(effect);
					}
				}
			}
			
			if(config.contains(minigame + ".loadout.usepermissions")){
				getLoadout("default").setUsePermissions(config.getBoolean(minigame + ".loadout.usepermissions"));
			}
			
			if(config.contains(minigame + ".loadout.falldamage")){
				getLoadout("default").setHasFallDamage(config.getBoolean(minigame + ".loadout.falldamage"));
			}
			if(config.contains(minigame + ".loadout.hunger")){
				getLoadout("default").setHasHunger(config.getBoolean(minigame + ".loadout.hunger"));
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
		if(extraLoadouts.containsKey(name)){
			pl = extraLoadouts.get(name);
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
		final MinigamePlayer fply = player;
		
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
