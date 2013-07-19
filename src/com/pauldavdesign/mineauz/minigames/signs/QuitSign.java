package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;

public class QuitSign implements MinigameSign {
	
	private static Minigames plugin = Minigames.plugin;

	@Override
	public String getName() {
		return "Quit";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.quit";
	}

	@Override
	public String getCreatePermissionMessage() {
		return "You do not have permission to create a Minigames quit sign.";
	}

	@Override
	public String getUsePermission() {
		return null;
	}

	@Override
	public String getUsePermissionMessage() {
		return null;
	}

	@Override
	public boolean signCreate(SignChangeEvent event) {
		event.setLine(1, ChatColor.GREEN + "Quit");
		return true;
	}

	@Override
	public boolean signUse(Sign sign, MinigamePlayer player) {
		if(player.isInMinigame() && player.getPlayer().getItemInHand().getType() == Material.AIR){
			plugin.pdata.quitMinigame(player, false);
			return true;
		}
		else if(player.getPlayer().getItemInHand().getType() != Material.AIR)
			player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Your hand must be empty to use this sign!");
		return false;
	}

}
