package au.com.mineauz.minigames.properties.types;

import org.bukkit.configuration.ConfigurationSection;
import au.com.mineauz.minigames.properties.ConfigProperty;

public class StringProperty extends ConfigProperty<String> {
	public StringProperty(String value, String name) {
		super(name, value);
	}
	
	public StringProperty(String name) {
		super(name, null);
	}
	
	@Override
	public void save(ConfigurationSection section) {
		section.set(getName(), getValue());
	}

	@Override
	public void load(ConfigurationSection section) {
		setValue(section.getString(getName()));
	}
}
