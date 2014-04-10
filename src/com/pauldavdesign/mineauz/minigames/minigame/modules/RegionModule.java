package com.pauldavdesign.mineauz.minigames.minigame.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.MinigameModule;
import com.pauldavdesign.mineauz.minigames.minigame.regions.Region;
import com.pauldavdesign.mineauz.minigames.minigame.regions.RegionAction;
import com.pauldavdesign.mineauz.minigames.minigame.regions.RegionExecutor;
import com.pauldavdesign.mineauz.minigames.minigame.regions.RegionTrigger;

public class RegionModule implements MinigameModule {
	
	private Map<String, Region> regions = new HashMap<String, Region>();
	
	@Override
	public String getName(){
		return "Regions";
	}

	@Override
	public void save(String minigame, FileConfiguration config) {
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
			
			int c = 0;
			for(RegionExecutor ex : r.getExecutors()){
				config.set(minigame + ".regions." + name + ".executors." + c + ".trigger", ex.getTrigger().toString());
				config.set(minigame + ".regions." + name + ".executors." + c + ".action", ex.getAction().toString());
				c++;
			}
		}
	}

	@Override
	public void load(String minigame, FileConfiguration config) {
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
				
				regions.put(name, new Region(loc1, loc2));
				Region r = regions.get(name);
				if(config.contains(minigame + ".regions." + name + ".executors")){
					Set<String> ex = config.getConfigurationSection(minigame + ".regions." + name + ".executors").getKeys(false);
					for(String i : ex){
						r.addExecutor(RegionTrigger.valueOf(config.getString(minigame + ".regions." + name + ".executors." + i + ".trigger")), 
								RegionAction.valueOf(config.getString(minigame + ".regions." + name + ".executors." + i + ".action")));
					}
				}
			}
		}
	}
	
	public static RegionModule getMinigameModule(Minigame minigame){
		return (RegionModule) minigame.getModule("Regions");
	}
	
	public void addRegion(String name, Region region){
		regions.put(name, region);
	}
	
	public Region getRegion(String name){
		return regions.get(name);
	}
	
	public List<Region> getRegions(){
		return new ArrayList<Region>(regions.values());
	}

}
