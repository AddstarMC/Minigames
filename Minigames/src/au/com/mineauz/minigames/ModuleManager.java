package au.com.mineauz.minigames;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.CTFModule;
import au.com.mineauz.minigames.minigame.modules.GameOverModule;
import au.com.mineauz.minigames.minigame.modules.InfectionModule;
import au.com.mineauz.minigames.minigame.modules.JuggernautModule;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.minigame.modules.TreasureHuntModule;
import au.com.mineauz.minigames.minigame.modules.WeatherTimeModule;

public class ModuleManager {
	private Minigames plugin;
	private SetMultimap<Plugin, Class<? extends MinigameModule>> modules;
	private SetMultimap<MinigameType, Class<? extends MinigameModule>> additionalTypeModules;
	
	public ModuleManager(Minigames plugin) {
		modules = HashMultimap.create();
		additionalTypeModules = HashMultimap.create();
		this.plugin = plugin;
		
		// Register minigame modules
		registerModule(plugin, LoadoutModule.class);
		registerModule(plugin, LobbySettingsModule.class);
		registerModule(plugin, TeamsModule.class);
		registerModule(plugin, WeatherTimeModule.class);
		registerModule(plugin, TreasureHuntModule.class);
		registerModule(plugin, InfectionModule.class);
		registerModule(plugin, GameOverModule.class);
		registerModule(plugin, JuggernautModule.class);
		registerModule(plugin, CTFModule.class);
		
		// Add defaults
		addDefaultModule(MinigameType.SINGLEPLAYER, WeatherTimeModule.class);
		addDefaultModule(MinigameType.SINGLEPLAYER, LoadoutModule.class);
		addDefaultModule(MinigameType.SINGLEPLAYER, GameOverModule.class); // FIXME: For now this will be added to sp, but it only makes sense for multiplayer
		
		addDefaultModule(MinigameType.MULTIPLAYER, WeatherTimeModule.class);
		addDefaultModule(MinigameType.MULTIPLAYER, LoadoutModule.class);
		addDefaultModule(MinigameType.MULTIPLAYER, LobbySettingsModule.class);
		addDefaultModule(MinigameType.MULTIPLAYER, GameOverModule.class);
	}
	
	/**
	 * Registers a MinigameModule. 
	 * @param plugin The plugin that owns the module
	 * @param moduleClass The class of the module
	 */
	public void registerModule(Plugin plugin, Class<? extends MinigameModule> moduleClass) {
		modules.put(plugin, moduleClass);
	}
	
	/**
	 * Adds a default module for the specified minigame type
	 * @param type The minigame type to add the module to
	 * @param moduleClass The module type
	 * @throws IllegalArgumentException Thrown if the module is not registered
	 */
	public void addDefaultModule(MinigameType type, Class<? extends MinigameModule> moduleClass) throws IllegalArgumentException {
		Validate.isTrue(isRegistered(moduleClass), "Module must be registered first");
		additionalTypeModules.put(type, moduleClass);
	}
	
	/**
	 * Adds a default module for all minigame types
	 * @param moduleClass The module type
	 * @throws IllegalArgumentException Thrown if the module is not registered
	 */
	public void addDefaultModule(Class<? extends MinigameModule> moduleClass) throws IllegalArgumentException {
		Validate.isTrue(isRegistered(moduleClass), "Module must be registered first");
		for (MinigameType type : MinigameType.values()) {
			additionalTypeModules.put(type, moduleClass);
		}
	}
	
	/**
	 * Unregisters all modules from the specified plugin
	 * @param plugin The plugin that owns the modules
	 */
	public void unregisterAll(Plugin plugin) {
		for (Class<? extends MinigameModule> module : modules.removeAll(plugin)) {
			removeModuleType(module);
		}
	}
	
	private void removeModuleType(Class<? extends MinigameModule> moduleClass) {
		for(Minigame mg : this.plugin.mdata.getAllMinigames().values()) {
			mg.removeModule(moduleClass);
		}
		
		for (MinigameType type : MinigameType.values()) {
			additionalTypeModules.remove(type, moduleClass);
		}
	}
	
	/**
	 * @return Returns all registered modules
	 */
	public Collection<Class<? extends MinigameModule>> getAvailableModules() {
		return Collections.unmodifiableCollection(modules.values());
	}
	
	/**
	 * Checks if a module is registered
	 * @param moduleClass The module class to check
	 * @return True if it is registered
	 */
	public boolean isRegistered(Class<? extends MinigameModule> moduleClass) {
		return modules.containsValue(moduleClass);
	}
	
	/**
	 * Gets all the modules registered as default for the minigame type
	 * @param type The MinigameType to check
	 * @return A collection of modules
	 */
	public Collection<Class<? extends MinigameModule>> getDefaultModules(MinigameType type) {
		return Collections.unmodifiableCollection(additionalTypeModules.get(type));
	}
}
