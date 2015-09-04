package au.com.mineauz.minigames.properties.types;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.properties.ConfigProperty;

public class LoadoutSetProperty extends ConfigProperty<Map<String, PlayerLoadout>> {
	public LoadoutSetProperty(Map<String, PlayerLoadout> value, String name) {
		super(name, value);
	}
	
	@Override
	public void save(ConfigurationSection root) {
		ConfigurationSection section = root.createSection(getName());
		Map<String, PlayerLoadout> loadouts = getValue();
		
		for(PlayerLoadout loadout : loadouts.values()) {
			loadout.save(section.createSection(loadout.getName(false)));
		}
	}

	@Override
	public void load(ConfigurationSection root) {
		ConfigurationSection section = root.getConfigurationSection(getName());
		
		for (String name : section.getKeys(false)) {
			PlayerLoadout loadout = new PlayerLoadout(name);
			loadout.load(section.getConfigurationSection(name));
			
			getValue().put(name, loadout);
		}
	}
}
