package au.com.mineauz.minigames.properties.types;

import org.bukkit.configuration.ConfigurationSection;
import com.google.common.base.Preconditions;

import au.com.mineauz.minigames.properties.ConfigProperty;

public class EnumProperty<T extends Enum<T>> extends ConfigProperty<T> {
	private Class<T> enumClass;
	
	/**
	 * Constructs this flag with a non null value
	 * @param value The value of this flag. Cannot be null
	 * @param name The name of this flag
	 */
	@SuppressWarnings("unchecked")
	public EnumProperty(T value, String name) {
		super(name, value);
		Preconditions.checkNotNull(value);
		
		enumClass = (Class<T>) value.getClass();
	}

	@Override
	public void save(ConfigurationSection section) {
		section.set(getName(), getValue().name());
	}
	
	@Override
	public void load(ConfigurationSection section) {
		setValue(T.valueOf(enumClass, section.getString(getName())));
	}
}
