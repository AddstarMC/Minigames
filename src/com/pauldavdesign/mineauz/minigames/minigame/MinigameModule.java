package com.pauldavdesign.mineauz.minigames.minigame;

import org.bukkit.configuration.file.FileConfiguration;

public interface MinigameModule {
	public String getName();
	public void save(String minigame, FileConfiguration config);
	public void load(String minigame, FileConfiguration config);
}
