package au.com.mineauz.minigames.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class LocationListFlag extends Flag<List<Location>>{
	
	public LocationListFlag(List<Location> value, String name){
		setFlag(value);
		setDefaultFlag(value);
		setName(name);
	}

	@Override
	public void saveValue(String path, FileConfiguration config) {
		if(!getFlag().isEmpty()){
			LocationFlag locf;
			for(int i = 0; i < getFlag().size(); i++){
				locf = new LocationFlag(null, getName() + "." + i);
				locf.setFlag(getFlag().get(i));
				locf.saveValue(path, config);
			}
		}
	}

	@Override
	public void loadValue(String path, FileConfiguration config) {
		List<Location> locs = new ArrayList<Location>();
		Set<String> ids = config.getConfigurationSection(path + "." + getName()).getKeys(false);
		LocationFlag locf;
		
		for(int i = 0; i < ids.size(); i++){
			locf = new LocationFlag(null, getName() + "." + String.valueOf(i));
			locf.loadValue(path, config);
			locs.add(locf.getFlag());
		}
		setFlag(locs);
	}
}
