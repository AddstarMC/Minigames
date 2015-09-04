package au.com.mineauz.minigames.properties;

import org.bukkit.configuration.ConfigurationSection;
import com.google.common.base.Objects;

/**
 * Represents a property that is part of configuration.
 * It can be saved
 *
 * @param <T> The value's type
 */
public abstract class ConfigProperty<T> extends AbstractProperty<T> {
	private final String name;
	private final T defaultValue;
	
	public ConfigProperty(String name, T value) {
		this(name, value, value);
	}
	
	public ConfigProperty(String name, T value, T defaultValue) {
		super(value);
		
		this.name = name;
		this.defaultValue = defaultValue;
	}
	
	/**
	 * Gets the name of this property
	 * @return The name
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * Gets the default value for this property
	 * @return The default value
	 */
	public final T getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * Checks whether this value is different from the default value
	 * @return True if it is different
	 */
	public final boolean isModified() {
		return !Objects.equal(getValue(), defaultValue);
	}
	
	/**
	 * Saves the value to the {@link ConfigurationSection}
	 * The check for {@link #isModified()} is called before this method so you do not need to handle that.
	 * @param section The section to write into. You must write the value (or values) under the key {@link #getName()}.
	 */
	public abstract void save(ConfigurationSection section);
	
	/**
	 * Loads the value from the {@link ConfigurationSection}
	 * A preliminary check will be made that {@code section.contains(getName())} is true.
	 * @param section The section to read from. The key to read should be {@link #getName()}.
	 */
	public abstract void load(ConfigurationSection section);
}
