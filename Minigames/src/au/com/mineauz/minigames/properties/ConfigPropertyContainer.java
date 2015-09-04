package au.com.mineauz.minigames.properties;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.google.common.collect.Maps;

public class ConfigPropertyContainer {
	private final Map<String, ConfigProperty<?>> properties;
	
	public ConfigPropertyContainer() {
		properties = Maps.newHashMap();
	}
	
	public void addProperty(ConfigProperty<?> property) {
		properties.put(property.getName().toLowerCase(), property);
	}
	
	public <T> ConfigProperty<T> getProperty(String name) {
		return (ConfigProperty<T>)properties.get(name.toLowerCase());
	}
	
	public Collection<ConfigProperty<?>> getProperties() {
		return Collections.unmodifiableCollection(properties.values());
	}
	
	public void saveAll(ConfigurationSection section) {
		for (ConfigProperty<?> prop : properties.values()) {
			if (prop.isModified()) {
				prop.save(section);
			}
		}
	}
	
	public void loadAll(ConfigurationSection section) {
		for (ConfigProperty<?> prop : properties.values()) {
			if (section.contains(prop.getName())) {
				prop.load(section);
			}
		}
	}
}
