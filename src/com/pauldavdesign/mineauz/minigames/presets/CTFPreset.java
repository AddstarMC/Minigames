package com.pauldavdesign.mineauz.minigames.presets;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.Minigames;

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
		if(Minigames.plugin.mdata.getMinigameTypes().contains("teamdm")){
			minigame.setType("teamdm");
		}
		else{
			minigame.setType("dm");
		}
		minigame.setCanBlockBreak(false);
		minigame.setCanBlockPlace(false);
		minigame.setMinScore(5);
		minigame.setMaxScore(5);
		minigame.setDefaultGamemode(2);
		minigame.getDefaultPlayerLoadout().addItemToLoadout(new ItemStack(Material.STONE_SWORD));
		minigame.getDefaultPlayerLoadout().addItemToLoadout(new ItemStack(Material.LEATHER_HELMET));
		minigame.getDefaultPlayerLoadout().addItemToLoadout(new ItemStack(Material.LEATHER_CHESTPLATE));
		minigame.getDefaultPlayerLoadout().addItemToLoadout(new ItemStack(Material.LEATHER_LEGGINGS));
		minigame.getDefaultPlayerLoadout().addItemToLoadout(new ItemStack(Material.LEATHER_BOOTS));
		minigame.getDefaultPlayerLoadout().addItemToLoadout(new ItemStack(Material.ARROW, 16));
		minigame.getDefaultPlayerLoadout().addItemToLoadout(new ItemStack(Material.BOW));
	}

}
