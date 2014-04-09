package com.pauldavdesign.mineauz.minigames.minigame.modules;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.minigame.MinigameModule;
import com.pauldavdesign.mineauz.minigames.minigame.regions.Region;

public class RegionModule implements MinigameModule {
	
	private Map<String, Region> regions = new HashMap<String, Region>();
	
	@Override
	public String getName(){
		return "Regions";
	}

	@Override
	public void save(String minigame, FileConfiguration config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(String minigame, FileConfiguration config) {
		// TODO Auto-generated method stub
		
	}
	
	public void addRegion(String name, Region region){
		regions.put(name, region);
	}
	
	public Region getRegion(String name){
		return regions.get(name);
	}

}
