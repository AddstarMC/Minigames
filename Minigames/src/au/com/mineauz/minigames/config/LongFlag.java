package au.com.mineauz.minigames.config;

import org.bukkit.configuration.file.FileConfiguration;

public class LongFlag extends Flag<Long>{
	
	public LongFlag(Long value, String name){
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
		setFlag(((Integer)config.getInt(path + "." + getName())).longValue());
	}
}
