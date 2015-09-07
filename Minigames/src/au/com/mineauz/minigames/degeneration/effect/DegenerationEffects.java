package au.com.mineauz.minigames.degeneration.effect;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItem.IMenuItemClick;
import au.com.mineauz.minigames.properties.Property;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

public final class DegenerationEffects {
	private DegenerationEffects() {}
	
	private static final ClearDegenEffect clearEffect = new ClearDegenEffect();
	
	private static SetMultimap<Plugin, String> pluginEffects = HashMultimap.create();
	private static Map<String, DefinedEffect> effects = Maps.newHashMap();
	
	static {
		register(Minigames.plugin, clearEffect);
		register(Minigames.plugin, new BreakDegenEffect());
		register(Minigames.plugin, new FallDegenEffect());
	}
	
	public static DegenerationEffect get(String type) {
		DefinedEffect defined = effects.get(type.toLowerCase());
		
		if (defined == null) {
			return clearEffect;
		} else {
			return defined.getEffect();
		}
	}
	
	/**
	 * Registers a new effect.
	 * @param plugin The plugin registering it
	 * @param effect The effect to register. The value of {@link DegenerationEffect#getName()} must be unique
	 * @throws IllegalArgumentException Thrown if the effect cannot be registered
	 */
	public static void register(Plugin plugin, DegenerationEffect effect) throws IllegalArgumentException {
		Preconditions.checkNotNull(plugin);
		Preconditions.checkNotNull(effect);
		Preconditions.checkNotNull(effect.getName());
		Preconditions.checkNotNull(effect.getDescription());
		
		String name = effect.getName().toLowerCase();
		
		if (effects.containsKey(name)) {
			throw new IllegalArgumentException("A degenerator with the name '" + name + "' is already registered");
		}
		
		// Now register it
		DefinedEffect defined = new DefinedEffect(effect, effect.getDescription());
		effects.put(name, defined);
		pluginEffects.put(plugin, name);
	}
	
	/**
	 * Unregisters a specific effect
	 * @param plugin The owning plugin
	 * @param name The name of the effect, case insensitive
	 */
	public static void unregister(Plugin plugin, String name) {
		if (pluginEffects.remove(plugin, name.toLowerCase())) {
			effects.remove(name.toLowerCase());
		}
	}
	
	/**
	 * Unregisters all effects registered by {@code plugin}
	 * @param plugin The owning plugin
	 */
	public static void unregisterAll(Plugin plugin) {
		Set<String> names = pluginEffects.removeAll(plugin);
		for (String name : names) {
			effects.remove(name);
		}
	}
	
	/**
	 * Gets all registered effect types
	 * @return An unmodifiable set of the names of all registered effect
	 */
	public static Set<String> getTypes() {
		return Collections.unmodifiableSet(effects.keySet());
	}
	
	public static Menu createSelectionMenu(final Property<String> property) {
		Menu menu = new Menu(5, "Select Effect");
		
		for (final String type : effects.keySet()) {
			DefinedEffect defined = effects.get(type);
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
	
	private static class DefinedEffect {
		private final DegenerationEffect effect;
		
		private final String description;
		
		public DefinedEffect(DegenerationEffect effect, String description) {
			this.effect = effect;
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}

		public DegenerationEffect getEffect() {
			return effect;
		}
	}
}
