package com.pauldavdesign.mineauz.minigames.minigame;

import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.menu.Menu;

public interface MinigameModule {
	public String getName();
	public boolean useSeparateConfig();
	public void save(Minigame minigame, FileConfiguration config);
	public void load(Minigame minigame, FileConfiguration config);
	public void addMenuOptions(Menu menu, Minigame minigame);
}
