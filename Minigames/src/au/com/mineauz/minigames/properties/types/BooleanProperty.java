package au.com.mineauz.minigames.properties.types;

import org.bukkit.configuration.ConfigurationSection;
import au.com.mineauz.minigames.properties.ConfigProperty;

public class BooleanProperty extends ConfigProperty<Boolean> {
	public BooleanProperty(boolean value, String name) {
		super(name, value);
	}
	
	@Override
	public void save(ConfigurationSection section) {
		section.set(getName(), getValue());
	}
	
	@Override
	public void load(ConfigurationSection section) {
		setValue(section.getBoolean(getName()));
	}
}
