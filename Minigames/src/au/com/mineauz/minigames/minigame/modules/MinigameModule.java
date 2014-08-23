package au.com.mineauz.minigames.minigame.modules;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.minigame.Minigame;

public abstract class MinigameModule {
	private final Minigame mgm;
	
	public MinigameModule(Minigame mgm){
		this.mgm = mgm;
	}
	public Minigame getMinigame(){
		return mgm;
	}
	public abstract String getName();
	public abstract Map<String, Flag<?>> getFlags();
	public abstract boolean useSeparateConfig();
	public abstract void save(FileConfiguration config);
	public abstract void load(FileConfiguration config);
	public abstract void addEditMenuOptions(Menu menu);
	public abstract boolean displayMechanicSettings(Menu previous);
}
