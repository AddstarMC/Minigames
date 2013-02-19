package com.pauldavdesign.mineauz.minigames.presets;

import com.pauldavdesign.mineauz.minigames.Minigame;

public interface BasePreset {
	public String getName();
	
	public String getInfo();
	
	public void execute(Minigame minigame);
}
