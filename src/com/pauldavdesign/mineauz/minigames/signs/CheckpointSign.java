package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.StoredPlayerCheckpoints;

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
		if(event.getLine(2).equalsIgnoreCase("global")){
			event.setLine(2, ChatColor.BLUE + "Global");
		}
		return true;
	}

	@Override
	public boolean signUse(Sign sign, Player player) {
		if((plugin.pdata.playerInMinigame(player) || (!plugin.pdata.playerInMinigame(player) && sign.getLine(2).equals(ChatColor.BLUE + "Global"))) 
				&& player.getItemInHand().getType() == Material.AIR){
			if(plugin.pdata.playerInMinigame(player) && plugin.pdata.getPlayersMinigame(player).isSpectator(player)){
				return false;
			}
			if(((LivingEntity)player).isOnGround()){
				Location newloc = player.getLocation();
				if(!sign.getLine(2).equals(ChatColor.BLUE + "Global")){
					plugin.pdata.setPlayerCheckpoints(player, newloc);
				}
				else{
					if(!plugin.pdata.hasStoredPlayerCheckpoint(player)){
						StoredPlayerCheckpoints spc = new StoredPlayerCheckpoints(player.getName(), newloc);
						plugin.pdata.addStoredPlayerCheckpoints(player.getName(), spc);
					}
					else{
						StoredPlayerCheckpoints spc = plugin.pdata.getPlayersStoredCheckpoints(player);
						spc.setGlobalCheckpoint(newloc);
					}
				}
				
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
