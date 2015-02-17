package au.com.mineauz.minigames.minigame.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.LoadoutSetFlag;
import au.com.mineauz.minigames.menu.InteractionInterface;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.minigame.Minigame;

public class LoadoutModule extends MinigameModule {

	private Map<String, PlayerLoadout> extraLoadouts = new HashMap<String, PlayerLoadout>();
	private LoadoutSetFlag loadoutsFlag = new LoadoutSetFlag(extraLoadouts, "loadouts");
	
	public LoadoutModule(Minigame mgm) {
		super(mgm);
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
	public void save(FileConfiguration config) {
		//Do Nothing
	}

	@Override
	public void load(FileConfiguration config) {
		
		//TODO: Remove entire load after 1.7
		if(config.contains(getMinigame() + ".loadout")){
			Set<String> keys = config.getConfigurationSection(getMinigame() + ".loadout").getKeys(false);
			for(String key : keys){
				if(key.matches("[0-9]+"))
					getLoadout("default").addItem(config.getItemStack(getMinigame() + ".loadout." + key), Integer.parseInt(key));
			}
			
			if(config.contains(getMinigame() + ".loadout.potions")){
				keys = config.getConfigurationSection(getMinigame() + ".loadout.potions").getKeys(false);
				for(String eff : keys){
					if(PotionEffectType.getByName(eff) != null){
						PotionEffect effect = new PotionEffect(PotionEffectType.getByName(eff),
								config.getInt(getMinigame() + ".loadout.potions." + eff + ".dur"),
								config.getInt(getMinigame() + ".loadout.potions." + eff + ".amp"), true);
						getLoadout("default").addPotionEffect(effect);
					}
				}
			}
			
			if(config.contains(getMinigame() + ".loadout.usepermissions")){
				getLoadout("default").setUsePermissions(config.getBoolean(getMinigame() + ".loadout.usepermissions"));
			}
			
			if(config.contains(getMinigame() + ".loadout.falldamage")){
				getLoadout("default").setHasFallDamage(config.getBoolean(getMinigame() + ".loadout.falldamage"));
			}
			if(config.contains(getMinigame() + ".loadout.hunger")){
				getLoadout("default").setHasHunger(config.getBoolean(getMinigame() + ".loadout.hunger"));
			}
		}
		if(config.contains(getMinigame() + ".extraloadouts")){
			Set<String> keys = config.getConfigurationSection(getMinigame() + ".extraloadouts").getKeys(false);
			for(String loadout : keys){
				addLoadout(loadout);
				Set<String> items = config.getConfigurationSection(getMinigame() + ".extraloadouts." + loadout).getKeys(false);
				for(String key : items){
					if(key.matches("[0-9]+"))
						getLoadout(loadout).addItem(config.getItemStack(getMinigame() + ".extraloadouts." + loadout + "." + key), Integer.parseInt(key));
				}
				if(config.contains(getMinigame() + ".extraloadouts." + loadout + ".potions")){
					Set<String> pots = config.getConfigurationSection(getMinigame() + ".extraloadouts." + loadout + ".potions").getKeys(false);
					for(String eff : pots){
						if(PotionEffectType.getByName(eff) != null){
							PotionEffect effect = new PotionEffect(PotionEffectType.getByName(eff),
									config.getInt(getMinigame() + ".extraloadouts." + loadout + ".potions." + eff + ".dur"),
									config.getInt(getMinigame() + ".extraloadouts." + loadout + ".potions." + eff + ".amp"));
							getLoadout(loadout).addPotionEffect(effect);
						}
					}
				}
				
				if(config.contains(getMinigame() + ".extraloadouts." + loadout + ".usepermissions")){
					getLoadout(loadout).setUsePermissions(config.getBoolean(getMinigame() + ".extraloadouts." + loadout + ".usepermissions"));
				}
				
				if(config.contains(getMinigame() + ".extraloadouts." + loadout + ".falldamage"))
					getLoadout(loadout).setHasFallDamage(config.getBoolean(getMinigame() + ".extraloadouts." + loadout + ".falldamage"));
				
				if(config.contains(getMinigame() + ".extraloadouts." + loadout + ".hunger"))
					getLoadout(loadout).setHasHunger(config.getBoolean(getMinigame() + ".extraloadouts." + loadout + ".hunger"));
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
		else{
			for(String loadout : extraLoadouts.keySet()){
				if(loadout.equalsIgnoreCase(name)){
					pl = extraLoadouts.get(loadout);
					break;
				}
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
			if(extraLoadouts.containsKey(name))
				return extraLoadouts.containsKey(name);
			else{
				for(String loadout : extraLoadouts.keySet()){
					if(loadout.equalsIgnoreCase(name))
						return true;
				}
				return false;
			}
		}
		else{
			return true;
		}
	}
	
	public void displaySelectionMenu(MinigamePlayer player, final boolean equip){
		Menu m = new Menu(6, "Select Loadout");
		
		for(PlayerLoadout loadout : extraLoadouts.values()){
			if(loadout.isDisplayedInMenu()){
				if(!loadout.getUsePermissions() || player.getPlayer().hasPermission("minigame.loadout." + loadout.getName(false).toLowerCase())){
					if(!player.getMinigame().isTeamGame() || loadout.getTeamColor() == null || 
							player.getTeam().getColor() == loadout.getTeamColor()){
						MenuItemCustom c = new MenuItemCustom(loadout.getName(true), Material.GLASS);
						if(!loadout.getItems().isEmpty())
							c.setItem(loadout.getItem(new ArrayList<Integer>(loadout.getItems()).get(0)));
						final PlayerLoadout floadout2 = loadout;
						c.setClick(new InteractionInterface() {
							
							@Override
							public Object interact(MinigamePlayer player, Object object) {
								player.setLoadout(floadout2);
								player.getPlayer().closeInventory();
								if(!equip)
									player.sendMessage(MinigameUtils.getLang("player.loadout.nextSpawn"), null);
								else{
									player.sendMessage(MinigameUtils.formStr("player.loadout.equipped", floadout2.getName(true)), null);
									floadout2.equiptLoadout(player);
								}
								return null;
							}
						});
						m.addItem(c);
					}
				}
			}
		}
		m.displayMenu(player);
	}
}
