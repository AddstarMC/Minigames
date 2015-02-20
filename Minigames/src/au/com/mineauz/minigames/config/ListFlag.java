package au.com.mineauz.minigames.config;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

public class ListFlag extends Flag<List<String>>{
	
	public ListFlag(List<String> value, String name){
		setFlag(value);
		setDefaultFlag(value);
		setName(name);
	}

	@Override
	public void saveValue(String path, FileConfiguration config) {
		config.set(path + "." + getName(), getFlag());
	}

	@Override
	public void loadValue(String path, FileConfiguration config) {
		setFlag(config.getStringList(path + "." + getName()));
	}
}
