package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;

public class QuitSign implements MinigameSign {
	
	private static Minigames plugin = Minigames.plugin;
	private FileConfiguration lang = plugin.getLang();

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
		return lang.getString("sign.quit.createPermission");
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
			player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + lang.getString("sign.emptyHand"));
		return false;
	}

}
