package au.com.mineauz.minigames.minigame.modules;

import java.lang.reflect.Constructor;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;

public abstract class MinigameModule {
	private final Minigame mgm;
	
	public MinigameModule(Minigame mgm){
		this.mgm = mgm;
	}
	public Minigame getMinigame(){
		return mgm;
	}
	public abstract String getName();
	public abstract ConfigPropertyContainer getProperties();
	public abstract boolean useSeparateConfig();
	public abstract void save(FileConfiguration config);
	public abstract void load(FileConfiguration config);
	public void addEditMenuOptions(Menu menu) {}
	public Menu createSettingsMenu() { return null; }
	
	/**
	 * Allows the module to apply any settings to the player
	 * @param player The player to manipulate
	 */
	public void applySettings(MinigamePlayer player) {}
	
	public static <T extends MinigameModule> T makeModule(Class<T> moduleClass, Minigame minigame) {
		try {
			Constructor<T> constructor = moduleClass.getConstructor(Minigame.class);
			return constructor.newInstance(minigame);
		} catch (Exception e) {
			Minigames.plugin.getLogger().severe("Unable to instanciate module " + moduleClass.getName() + ". It doesn't have the required constructor");
			return null;
		}
	}
}
