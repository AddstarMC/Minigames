package com.pauldavdesign.mineauz.minigames.minigame;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.config.Flag;
import com.pauldavdesign.mineauz.minigames.menu.Menu;

public interface MinigameModule {
	public String getName();
	public Map<String, Flag<?>> getFlags();
	public boolean useSeparateConfig();
	public void save(Minigame minigame, FileConfiguration config);
	public void load(Minigame minigame, FileConfiguration config);
	public void addMenuOptions(Menu menu, Minigame minigame);
}
