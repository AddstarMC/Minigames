package com.pauldavdesign.mineauz.minigames.presets;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.Minigame;

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
		
		minigame.setType("dm");
		minigame.setLives(1);
		
		minigame.getDefaultPlayerLoadout().clearLoadout();
		minigame.getDefaultPlayerLoadout().addItemToLoadout(new ItemStack(Material.DIAMOND_SPADE));
		
		minigame.setEnabled(true);
	}
}
