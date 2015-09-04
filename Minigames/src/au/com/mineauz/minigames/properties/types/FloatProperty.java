package au.com.mineauz.minigames.properties.types;

import org.bukkit.configuration.ConfigurationSection;
import au.com.mineauz.minigames.properties.ConfigProperty;

public class FloatProperty extends ConfigProperty<Float> {
	public FloatProperty(float value, String name) {
		super(name, value);
	}

	@Override
	public void save(ConfigurationSection section) {
		section.set(getName(), getValue().doubleValue());
	}
	
	@Override
	public void load(ConfigurationSection section) {
		setValue((float)section.getDouble(getName()));
	}
}
