package com.pauldavdesign.mineauz.minigames.presets;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.PlayerLoadout;

public class InfectionPreset implements BasePreset {

	@Override
	public String getName() {
		return "Infection";
	}

	@Override
	public String getInfo() {
		return "Creates balanced settings for an Infection game. This is based off the \"Introducing Infection\" video on the Minigames 1.5 release. " +
				"It gives survivors a stone knockback 2 sword and power 10 bow with 24 arrows (insta kill) and give the Infected a sharpness 3 sword (2 hit kill), jump boost 2 " +
				"and speed 1 for unlimited time (also a zombie head). The games timer is 5 minutes.";
	}

	@Override
	public void execute(Minigame minigame) {
		//Loadouts
		minigame.addLoadout("red");
		minigame.addLoadout("blue");
		PlayerLoadout red = minigame.getLoadout("red");
		PlayerLoadout blue = minigame.getLoadout("blue");
		
		ItemStack zsword = new ItemStack(Material.DIAMOND_SWORD);
		ItemStack zhead = new ItemStack(Material.SKULL_ITEM);
		ItemStack ssword = new ItemStack(Material.STONE_SWORD);
		ItemStack sbow = new ItemStack(Material.BOW);
		ItemStack sarrows = new ItemStack(Material.ARROW, 24);
		
		zsword.addEnchantment(Enchantment.DAMAGE_ALL, 3);
		zhead.setDurability((short)2);
		ssword.addEnchantment(Enchantment.KNOCKBACK, 2);
		sbow.addEnchantment(Enchantment.ARROW_DAMAGE, 10);
		
		red.addItemToLoadout(zsword);
		red.addItemToLoadout(zhead);
		blue.addItemToLoadout(ssword);
		blue.addItemToLoadout(sbow);
		blue.addItemToLoadout(sarrows);
		
		//Settings
		minigame.setScoreType("infection");
		minigame.setDefaultWinner("blue");
		minigame.setType("teamdm");
		minigame.setMinPlayers(4);
		minigame.setMaxPlayers(16);
		minigame.setTimer(300);
		minigame.saveMinigame();
	}

}
