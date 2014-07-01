package com.pauldavdesign.mineauz.minigames.minigame;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.config.Flag;
import com.pauldavdesign.mineauz.minigames.menu.Menu;

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
	public abstract void addMenuOptions(Menu menu);
	public abstract boolean getMenuOptions(Menu previous);
}
