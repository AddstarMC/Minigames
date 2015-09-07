package au.com.mineauz.minigames.degeneration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.degeneration.ExpandingDegenerator.InwardExpandingGenerator;
import au.com.mineauz.minigames.degeneration.ExpandingDegenerator.OutwardExpandingGenerator;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItem.IMenuItemClick;
import au.com.mineauz.minigames.properties.Property;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

public final class Degenerators {
	private Degenerators() {}
	
	private static SetMultimap<Plugin, String> pluginDegenerators = HashMultimap.create();
	private static Map<String, DefinedDegenerator> degenerators = Maps.newHashMap();
	
	static {
		register(Minigames.plugin, InwardExpandingGenerator.class);
		register(Minigames.plugin, OutwardExpandingGenerator.class);
		register(Minigames.plugin, RandomDegenerator.class, RandomDegenerator.Settings.class);
		register(Minigames.plugin, ClearDegenerator.class);
	}
	
	/**
	 * Creates a degenerator of the specified type.
	 * @param type The type of degenerator, case insensitive
	 * @param min The minimum corner of the degeneration bounds
	 * @param max The maximum corner of the degeneration bounds
	 * @return The created degenerator. When not found a NullDegenerator is returned (does nothing)
	 */
	public static Degenerator create(String type, Location min, Location max) {
		DefinedDegenerator defined = degenerators.get(type.toLowerCase());
		
		if (defined == null) {
			return new NullDegenerator();
		} else {
			Degenerator degenerator = defined.createDegenerator(min, max);
			if (degenerator == null) {
				return new NullDegenerator();
			} else {
				return degenerator;
			}
		}
	}
	
	/**
	 * Creates a settings object for the specified type
	 * @param type The type of degenerator to get settings for
	 * @return The created settings object or null if none is available
	 */
	public static DegeneratorSettings createSettings(String type) {
		DefinedDegenerator defined = degenerators.get(type.toLowerCase());
		
		if (defined == null) {
			return null;
		}
		
		return defined.createSettings();
	}
	
	private static <T extends Degenerator> T createInstance(Constructor<T> constructor, Location min, Location max) {
		try {
			return constructor.newInstance(min, max);
		} catch (IllegalArgumentException e) {
			// Should not happen
			throw new AssertionError();
		} catch (IllegalAccessException e) {
			// The class's accessibility is probably bad
			throw new IllegalArgumentException("Could not access constructor for " + constructor.getDeclaringClass().getName());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * Registers a new degenerator.
	 * @param plugin The plugin registering it
	 * @param type The class of the degenerator. The class must not be abstract and must have a public constructor that takes 2 Locations.
	 * The name returned by the class must be unique, if not an IllegalArgumentException will be thrown.
	 * @throws IllegalArgumentException Thrown if the degenerator cannot be registered
	 */
	public static void register(Plugin plugin, Class<? extends Degenerator> type) throws IllegalArgumentException {
		register(plugin, type, null);
	}
	
	/**
	 * Registers a new degenerator with a custom settings provider
	 * @param plugin The plugin registering it
	 * @param type The class of the degenerator. The class must not be abstract and must have a public constructor that takes 2 Locations.
	 * The name returned by the class must be unique, if not an IllegalArgumentException will be thrown.
	 * @param settingsType The class of the DegeneratorSettings that provides properties to save and put in the menu
	 * @throws IllegalArgumentException Thrown if the degenerator cannot be registered
	 */
	public static void register(Plugin plugin, Class<? extends Degenerator> type, Class<? extends DegeneratorSettings> settingsType) throws IllegalArgumentException {
		Preconditions.checkNotNull(plugin);
		Preconditions.checkNotNull(type);
		
		// Check type validity
		if (Modifier.isAbstract(type.getModifiers()) || Modifier.isInterface(type.getModifiers())) {
			throw new IllegalArgumentException("Type cannot be abstract");
		}
		
		Constructor<? extends Degenerator> constructor;
		try {
			constructor = type.getConstructor(Location.class, Location.class);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Missing constructor");
		}
		
		Location testLoc = Bukkit.getWorlds().get(0).getSpawnLocation();
		
		// Now get the name
		Degenerator degen = createInstance(constructor, testLoc, testLoc);
		if (degen == null) {
			throw new IllegalArgumentException("Failed to create test instance");
		}
		
		String name = degen.getName();
		if (name == null) {
			throw new IllegalArgumentException("Name returned by " + type.getName() + " must not be null");
		}
		
		name = name.toLowerCase();
		
		if (degenerators.containsKey(name)) {
			throw new IllegalArgumentException("A degenerator with the name '" + name + "' is already registered");
		}
		
		// Now for the settings
		Constructor<? extends DegeneratorSettings> settingsConstructor = null;
		if (settingsType != null) {
			try {
				settingsConstructor = settingsType.getConstructor();
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException(settingsType.getName() + " requires a zero args constructor");
			}
		}
		
		// Now register it
		DefinedDegenerator defined = new DefinedDegenerator(constructor, settingsConstructor, degen.getDescription());
		degenerators.put(name, defined);
		pluginDegenerators.put(plugin, name);
	}
	
	/**
	 * Unregisters a specific degenerator
	 * @param plugin The owning plugin
	 * @param name The name of the degenerator, case insensitive
	 */
	public static void unregister(Plugin plugin, String name) {
		if (pluginDegenerators.remove(plugin, name.toLowerCase())) {
			degenerators.remove(name.toLowerCase());
		}
	}
	
	/**
	 * Unregisters all degenerators registered by {@code plugin}
	 * @param plugin The owning plugin
	 */
	public static void unregisterAll(Plugin plugin) {
		Set<String> names = pluginDegenerators.removeAll(plugin);
		for (String name : names) {
			degenerators.remove(name);
		}
	}
	
	/**
	 * Gets all registered degenerator types
	 * @return An unmodifiable set of the names of all registered degenerators
	 */
	public static Set<String> getTypes() {
		return Collections.unmodifiableSet(degenerators.keySet());
	}
	
	public static Menu createSelectionMenu(final Property<String> property) {
		Menu menu = new Menu(5, "Select Degenerator Type");
		
		for (final String type : degenerators.keySet()) {
			DefinedDegenerator defined = degenerators.get(type);
			MenuItem item = new MenuItem(WordUtils.capitalizeFully(type), Material.PAPER);
			
			String description = defined.getDescription();
			description = WordUtils.wrap(description, 30, ";" + ChatColor.GRAY, false);
			item.setDescription(ChatColor.GRAY + description);
			
			item.setClickHandler(new IMenuItemClick() {
				@Override
				public void onClick(MenuItem menuItem, MinigamePlayer player) {
					property.setValue(type);
					player.showPreviousMenu();
				}
			});
			
			menu.addItem(item);
		}
		
		return menu;
	}
	
	private static class DefinedDegenerator {
		private final Constructor<? extends Degenerator> degenConstructor;
		private final Constructor<? extends DegeneratorSettings> settingsConstructor;
		
		private final String description;
		
		public DefinedDegenerator(Constructor<? extends Degenerator> degenConstructor, Constructor<? extends DegeneratorSettings> settingsConstructor, String description) {
			this.degenConstructor = degenConstructor;
			this.settingsConstructor = settingsConstructor;
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}
		
		public Degenerator createDegenerator(Location min, Location max) {
			return createInstance(degenConstructor, min, max);
		}
		
		public DegeneratorSettings createSettings() {
			if (settingsConstructor == null) {
				return null;
			}
			try {
				return settingsConstructor.newInstance();
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("Could not access settings constructor on " + settingsConstructor.getDeclaringClass().getName());
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
