package au.com.mineauz.minigames.config;

import org.bukkit.configuration.file.FileConfiguration;
import au.com.mineauz.minigames.PlayerLoadout;

public class LoadoutFlag extends Flag<PlayerLoadout>{
	
	public LoadoutFlag(PlayerLoadout value, String name){
		setFlag(value);
		setDefaultFlag(null);
		setName(name);
	}

	@Override
	public void saveValue(String path, FileConfiguration config) {
		getFlag().save(config.createSection(path + "." + getName()));
	}

	@Override
	public void loadValue(String path, FileConfiguration config) {
		getFlag().load(config.getConfigurationSection(path + "." + getName()));
	}
}
