package au.com.mineauz.minigames.properties.types;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import au.com.mineauz.minigames.properties.ConfigProperty;

public class ListProperty extends ConfigProperty<List<String>> {
	
	public ListProperty(List<String> value, String name) {
		super(name, value);
	}

	@Override
	public void save(ConfigurationSection section) {
		section.set(getName(), getValue());
	}
	
	@Override
	public void load(ConfigurationSection section) {
		setValue(section.getStringList(getName()));
	}
}
