package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.Minigames;

public class JoinSign implements MinigameSign {
	
	private static Minigames plugin = Minigames.plugin;

	@Override
	public String getName() {
		return "Join";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.join";
	}

	@Override
	public String getCreatePermissionMessage() {
		return "You do not have permission to create a Minigame join sign!";
	}

	@Override
	public String getUsePermission() {
		return "minigame.use.join";
	}

	@Override
	public String getUsePermissionMessage() {
		return "You do not have permission to use a Minigame join sign!";
	}

	@Override
	public boolean signCreate(SignChangeEvent event) {
		if(plugin.mdata.hasMinigame(event.getLine(2))){
			event.setLine(1, ChatColor.GREEN + "Join");
			event.setLine(2, plugin.mdata.getMinigame(event.getLine(2)).getName());
			return true;
		}
		event.getPlayer().sendMessage(ChatColor.RED + "There is no minigame by the name \"" + event.getLine(2) + "\"");
		return false;
	}

	@Override
	public boolean signUse(Sign sign, Player player) {
		if(player.getItemInHand().getType() == Material.AIR && !plugin.pdata.playerInMinigame(player)){
			Minigame mgm = plugin.mdata.getMinigame(sign.getLine(2));
			if(mgm != null && (!mgm.getUsePermissions() || player.hasPermission("minigame.join." + mgm.getName().toLowerCase()))){
				if(mgm.isEnabled()){
					plugin.pdata.joinMinigame(player, plugin.mdata.getMinigame(sign.getLine(2)));
					return true;
				}
				else if(!mgm.isEnabled()){
					player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "This minigame is currently not enabled.");
				}
			}
			else if(mgm == null){
				player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "This minigame doesn't exist!");
			}
			else if(mgm.getUsePermissions()){
				player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You do not have permission minigame.join." + mgm.getName().toLowerCase());
			}
		}
		else if(!plugin.pdata.playerInMinigame(player))
			player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Your hand must be empty to use this sign!");
		return false;
	}

}
