package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.Minigames;

public class CheckpointSign implements MinigameSign {
	
	private static Minigames plugin = Minigames.plugin;

	@Override
	public String getName() {
		return "Checkpoint";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.checkpoint";
	}

	@Override
	public String getCreatePermissionMessage() {
		return "You do not have permission to create a Minigame checkpoint sign!";
	}

	@Override
	public String getUsePermission() {
		return "minigame.sign.use.checkpoint";
	}

	@Override
	public String getUsePermissionMessage() {
		return "You do not have permission to use a Minigame checkpoint sign!";
	}

	@Override
	public boolean signCreate(SignChangeEvent event) {
		event.setLine(1, ChatColor.GREEN + "Checkpoint");
		return true;
	}

	@Override
	public boolean signUse(Sign sign, Player player) {
		if(plugin.pdata.playerInMinigame(player) && player.getItemInHand().getType() == Material.AIR){
			if(plugin.mdata.getMinigame(plugin.pdata.getPlayersMinigame(player)).isSpectator(player)){
				return false;
			}
			Location loc = player.getLocation();
			loc.setY(loc.getY() - 0.5);
			if(loc.getBlock().getType() != Material.AIR){
				Location newloc = player.getLocation();
				plugin.pdata.setPlayerCheckpoints(player, newloc);
				
				player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Checkpoint set!");
				return true;
			}
			else{
				player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You can not set a checkpoint here!");
			}
		}
		else
			player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Your hand must be empty to use this sign!");
		return false;
	}

}
