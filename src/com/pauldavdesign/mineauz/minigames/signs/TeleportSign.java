package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.Minigames;

public class TeleportSign implements MinigameSign {
	
	private static Minigames plugin = Minigames.plugin;

	@Override
	public String getName() {
		return "Teleport";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.teleport";
	}

	@Override
	public String getCreatePermissionMessage() {
		return "You do not have permission to create a Minigames teleport sign!";
	}

	@Override
	public String getUsePermission() {
		return "minigame.sign.use.teleport";
	}

	@Override
	public String getUsePermissionMessage() {
		return "You do not have permission to use a Minigames teleport sign!";
	}

	@Override
	public boolean signCreate(SignChangeEvent event) {
		event.setLine(1, ChatColor.GREEN + "Teleport");
		if(event.getLine(2).isEmpty()){
			return false;
		}
		else{
			if(!event.getLine(2).matches("-?[0-9]+,[0-9]+,-?[0-9]+")){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean signUse(Sign sign, Player player) {
		if(!sign.getLine(2).isEmpty() && sign.getLine(2).matches("-?[0-9]+,[0-9]+,-?[0-9]+")){
			int x;
			int y;
			int z;
			String[] split = sign.getLine(2).split(",");
			x = Integer.parseInt(split[0]);
			y = Integer.parseInt(split[1]);
			z = Integer.parseInt(split[2]);
			
			if(plugin.pdata.playerInMinigame(player)){
				plugin.pdata.setAllowTP(player, true);
			}
			
			if(!sign.getLine(3).isEmpty() && sign.getLine(3).matches("-?[0-9]+,-?[0-9]+")){
				float yaw;
				float pitch;
				String[] split2 = sign.getLine(3).split(",");
				yaw = Float.parseFloat(split2[0]);
				pitch = Float.parseFloat(split2[1]);
				player.teleport(new Location(player.getWorld(), x + 0.5, y, z + 0.5, yaw, pitch));

				if(plugin.pdata.playerInMinigame(player)){
					plugin.pdata.setAllowTP(player, false);
				}
				return true;
			}
			player.teleport(new Location(player.getWorld(), x + 0.5, y, z + 0.5));
			if(plugin.pdata.playerInMinigame(player)){
				plugin.pdata.setAllowTP(player, false);
			}
			return true;
		}
		player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Invalid teleport sign!");
		return false;
	}

}
