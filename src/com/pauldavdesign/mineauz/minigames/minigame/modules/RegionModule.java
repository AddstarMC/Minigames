package com.pauldavdesign.mineauz.minigames.minigame.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.MinigameModule;
import com.pauldavdesign.mineauz.minigames.minigame.regions.MenuItemRegion;
import com.pauldavdesign.mineauz.minigames.minigame.regions.Region;
import com.pauldavdesign.mineauz.minigames.minigame.regions.actions.RegionActions;
import com.pauldavdesign.mineauz.minigames.minigame.regions.conditions.RegionConditionInterface;
import com.pauldavdesign.mineauz.minigames.minigame.regions.conditions.RegionConditions;
import com.pauldavdesign.mineauz.minigames.minigame.regions.RegionExecutor;
import com.pauldavdesign.mineauz.minigames.minigame.regions.RegionTrigger;

public class RegionModule implements MinigameModule {
	
	private Map<String, Region> regions = new HashMap<String, Region>();
	
	@Override
	public String getName(){
		return "Regions";
	}

	@Override
	public void save(Minigame minigame, FileConfiguration config) {
		Set<String> rs = regions.keySet();
		for(String name : rs){
			Region r = regions.get(name);
			Map<String, Object> sloc = MinigameUtils.serializeLocation(r.getFirstPoint());
			for(String i : sloc.keySet()){
				if(!i.equals("yaw") && !i.equals("pitch"))
					config.set(minigame + ".regions." + name + ".point1." + i, sloc.get(i));
			}
			sloc = MinigameUtils.serializeLocation(r.getSecondPoint());
			for(String i : sloc.keySet()){
				if(!i.equals("yaw") && !i.equals("pitch"))
					config.set(minigame + ".regions." + name + ".point2." + i, sloc.get(i));
			}
			
			if(r.getTickDelay() != 20){
				config.set(minigame + ".regions." + name + ".tickDelay", r.getTickDelay());
			}
			
			int c = 0;
			for(RegionExecutor ex : r.getExecutors()){
				String path = minigame + ".regions." + name + ".executors." + c;
				config.set(path + ".trigger", ex.getTrigger().toString());
				config.set(path + ".action", ex.getAction().getName());
				if(!ex.getConditions().isEmpty()){
					List<String> conditions = new ArrayList<String>(ex.getConditions().size());
					for(RegionConditionInterface con : ex.getConditions()){
						conditions.add(con.getName());
					}
					config.set(path + ".conditions", conditions);
				}
				if(!ex.getArguments().isEmpty()){
					ex.getAction().saveArguments(ex.getArguments(), config, path + ".arguments");
					for(RegionConditionInterface con : ex.getConditions()){
						con.saveArguments(ex.getArguments(), config, path + ".arguments");
					}
				}
				c++;
			}
		}
	}

	@Override
	public void load(Minigame minigame, FileConfiguration config) {
		if(config.contains(minigame + ".regions")){
			Set<String> rs = config.getConfigurationSection(minigame + ".regions").getKeys(false);
			for(String name : rs){
				String cloc1 = minigame + ".regions." + name + ".point1.";
				String cloc2 = minigame + ".regions." + name + ".point2.";
				World w1 = Minigames.plugin.getServer().getWorld(config.getString(cloc1 + "world"));
				World w2 = Minigames.plugin.getServer().getWorld(config.getString(cloc2 + "world"));
				double x1 = config.getDouble(cloc1 + "x");
				double x2 = config.getDouble(cloc2 + "x");
				double y1 = config.getDouble(cloc1 + "y");
				double y2 = config.getDouble(cloc2 + "y");
				double z1 = config.getDouble(cloc1 + "z");
				double z2 = config.getDouble(cloc2 + "z");
				Location loc1 = new Location(w1, x1, y1, z1);
				Location loc2 = new Location(w2, x2, y2, z2);
				
				regions.put(name, new Region(name, loc1, loc2));
				Region r = regions.get(name);
				if(config.contains(minigame + ".regions." + name + ".tickDelay")){
					r.changeTickDelay(config.getLong(minigame + ".regions." + name + ".tickDelay"));
				}
				if(config.contains(minigame + ".regions." + name + ".executors")){
					Set<String> ex = config.getConfigurationSection(minigame + ".regions." + name + ".executors").getKeys(false);
					for(String i : ex){
						String path = minigame + ".regions." + name + ".executors." + i;
						RegionExecutor rex = new RegionExecutor(RegionTrigger.valueOf(config.getString(path + ".trigger")), 
								RegionActions.getActionByName(config.getString(path + ".action")));
						if(config.contains(path + ".conditions")){
							for(String con : config.getStringList(path + ".conditions")){
								rex.addCondition(RegionConditions.getConditionByName(con));
							}
						}
						if(config.contains(path + ".arguments")){
							rex.addArguments(rex.getAction().loadArguments(config, path + ".arguments"));
							for(RegionConditionInterface con : rex.getConditions()){
								rex.addArguments(con.loadArguments(config, path + ".arguments"));
							}
						}
						r.addExecutor(rex);
					}
				}
			}
		}
	}
	
	public static RegionModule getMinigameModule(Minigame minigame){
		return (RegionModule) minigame.getModule("Regions");
	}
	
	public boolean hasRegion(String name){
		if(!regions.containsKey(name)){
			for(String n : regions.keySet()){
				if(n.equalsIgnoreCase(name))
					return true;
			}
			return false;
		}
		return true;
	}
	
	public void addRegion(String name, Region region){
		if(!hasRegion(name))
			regions.put(name, region);
	}
	
	public Region getRegion(String name){
		if(!hasRegion(name)){
			for(String n : regions.keySet()){
				if(n.equalsIgnoreCase(name))
					return regions.get(n);
			}
			return null;
		}
		return regions.get(name);
	}
	
	public List<Region> getRegions(){
		return new ArrayList<Region>(regions.values());
	}
	
	public void removeRegion(String name){
		if(hasRegion(name)){
			regions.get(name).removeTickTask();
			regions.remove(name);
		}
		else{
			for(String n : regions.keySet()){
				if(n.equalsIgnoreCase(name)){
					regions.get(n).removeTickTask();
					regions.remove(n);
					break;
				}
			}
		}
	}
	
	public void displayMenu(MinigamePlayer viewer){
		Menu rm = new Menu(6, "Regions", viewer);
		List<MenuItem> items = new ArrayList<MenuItem>(regions.size());
		for(String name : regions.keySet()){
			MenuItemRegion mir = new MenuItemRegion(name, Material.CHEST, regions.get(name), this);
			items.add(mir);
		}
		rm.addItems(items);
		rm.displayMenu(viewer);
	}
	
	
	
	@Override
	public void addMenuOptions(Menu menu) {
		// TODO Auto-generated method stub
		
	}
}
