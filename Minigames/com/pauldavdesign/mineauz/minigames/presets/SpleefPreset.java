package com.pauldavdesign.mineauz.minigames.presets;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.modules.LoadoutModule;

public class SpleefPreset implements BasePreset {

	@Override
	public String getName() {
		return "spleef";
	}

	@Override
	public String getInfo() {
		return "Sets up settings for a basic Spleef Minigame. This will allow players to break a snow block floor and equip them" +
				" with diamond spades. Starting locations, lobby, quit and end positions must be set manually. Don't forget to add a " +
				"floor degenerator if you want to stop islanding!";
	}

	@Override
	public void execute(Minigame minigame) {
		minigame.setBlocksdrop(false);
		minigame.setCanBlockBreak(true);
		minigame.setCanBlockPlace(false);
		minigame.getBlockRecorder().addWBBlock(Material.SNOW_BLOCK);
		minigame.getBlockRecorder().setWhitelistMode(true);
		
		minigame.setType(MinigameType.MULTIPLAYER);
		minigame.setLives(1);
		
		LoadoutModule mod = LoadoutModule.getMinigameModule(minigame);
		mod.getDefaultPlayerLoadout().clearLoadout();
		mod.getDefaultPlayerLoadout().addItem(new ItemStack(Material.DIAMOND_SPADE), 0);
		
		minigame.setEnabled(true);
	}
}
