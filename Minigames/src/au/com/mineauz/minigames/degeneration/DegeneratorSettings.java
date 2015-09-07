package au.com.mineauz.minigames.degeneration;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;

public abstract class DegeneratorSettings {
	protected final ConfigPropertyContainer properties;
	
	public DegeneratorSettings() {
		properties = new ConfigPropertyContainer();
	}
	
	public final ConfigPropertyContainer getProperties() {
		return properties;
	}
	
	public void addMenuItems(Menu menu) {}
}
