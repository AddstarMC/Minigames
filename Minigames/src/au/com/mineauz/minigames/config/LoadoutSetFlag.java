package au.com.mineauz.minigames.config;

import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.PlayerLoadout;

public class LoadoutSetFlag extends Flag<Map<String, PlayerLoadout>>{
	
	public LoadoutSetFlag(Map<String, PlayerLoadout> value, String name){
		setFlag(value);
		setDefaultFlag(null);
		setName(name);
	}

	@Override
	public void saveValue(String path, FileConfiguration config) {
		LoadoutFlag lf;
		for(String loadout : getFlag().keySet()){
			lf = new LoadoutFlag(getFlag().get(loadout), loadout);
			lf.saveValue(path + "." + getName(), config);
		}
	}

	@Override
	public void loadValue(String path, FileConfiguration config) {
		Set<String> keys = config.getConfigurationSection(path + "." + getName()).getKeys(false);
		LoadoutFlag lf;
		for(String loadout : keys){
			lf = new LoadoutFlag(new PlayerLoadout(loadout), loadout);
			if(loadout.equals("default"))
				lf.getFlag().setDeleteable(false);
			lf.loadValue(path + "." + getName(), config);
			getFlag().put(lf.getName(), lf.getFlag());
		}
	}
}
