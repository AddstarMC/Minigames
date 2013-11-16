package com.pauldavdesign.mineauz.minigames.presets;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class CTFPreset implements BasePreset {

	@Override
	public String getName() {
		return "CTF";
	}

	@Override
	public String getInfo() {
		return "Sets up a team deathmatch game for CTF, or Capture the Flag. You must set up the flags yourself, information on this can be found on " +
				"dev.bukkit.org/server-mods/minigames/pages/minigame-signs/\n" +
				"Use /mgm info <Minigame> to see what the settings are. Please note, if you are using a version incompatible with team deathmatch, the " +
				"game type will be set to deathmatch.";
	}

	@Override
	public void execute(Minigame minigame) {
		minigame.setScoreType("ctf");
		minigame.setMinPlayers(4);
		minigame.setMaxPlayers(16);
		minigame.setType(MinigameType.TEAMS);
		
		minigame.setCanBlockBreak(false);
		minigame.setCanBlockPlace(false);
		minigame.setMinScore(5);
		minigame.setMaxScore(5);
		minigame.setDefaultGamemode(GameMode.ADVENTURE);

		minigame.getDefaultPlayerLoadout().addItem(new ItemStack(Material.STONE_SWORD), 0);
		minigame.getDefaultPlayerLoadout().addItem(new ItemStack(Material.LEATHER_HELMET), 103);
		minigame.getDefaultPlayerLoadout().addItem(new ItemStack(Material.LEATHER_CHESTPLATE), 102);
		minigame.getDefaultPlayerLoadout().addItem(new ItemStack(Material.LEATHER_LEGGINGS), 101);
		minigame.getDefaultPlayerLoadout().addItem(new ItemStack(Material.LEATHER_BOOTS), 100);
		minigame.getDefaultPlayerLoadout().addItem(new ItemStack(Material.ARROW, 16), 16);
		minigame.getDefaultPlayerLoadout().addItem(new ItemStack(Material.BOW), 1);
	}

}
