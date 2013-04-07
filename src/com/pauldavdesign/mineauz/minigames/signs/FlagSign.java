package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.Minigames;

public class FlagSign implements MinigameSign {
	
	private static Minigames plugin = Minigames.plugin;

	@Override
	public String getName() {
		return "Flag";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.flag";
	}

	@Override
	public String getCreatePermissionMessage() {
		return "You do not have permission to create a Minigame flag sign!";
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
		event.setLine(1, ChatColor.GREEN + "Flag");
		if(event.getLine(2).equalsIgnoreCase("red")){
			event.setLine(2, ChatColor.RED + "Red");
		}
		else if(event.getLine(2).equalsIgnoreCase("blue")){
			event.setLine(2, ChatColor.BLUE + "Blue");
		}
		else if(event.getLine(2).equalsIgnoreCase("neutral")){
			event.setLine(2, ChatColor.GRAY + "Neutral");
		}
		else if(event.getLine(2).equalsIgnoreCase("capture") && !event.getLine(3).isEmpty()){
			event.setLine(2, ChatColor.GREEN + "Capture");
			if(event.getLine(3).equalsIgnoreCase("red")){
				event.setLine(3, ChatColor.RED + "Red");
			}
			else if(event.getLine(3).equalsIgnoreCase("blue")){
				event.setLine(3, ChatColor.BLUE + "Blue");
			}
			else if(event.getLine(3).equalsIgnoreCase("neutral")){
				event.setLine(3, ChatColor.GRAY + "Neutral");
			}
			else{
				event.getBlock().breakNaturally();
				event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Invalid sign syntax!" +
						" Acceptable arguments for capture signs are red, blue and neutral.");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean signUse(Sign sign, Player player) {
		if(player.getItemInHand().getType() == Material.AIR && plugin.pdata.playerInMinigame(player)){
			Minigame mgm = plugin.pdata.getPlayersMinigame(player);

			if(mgm.isSpectator(player)){
				return false;
			}
			if(!sign.getLine(2).isEmpty() && ((LivingEntity)player).isOnGround() && 
					!mgm.getScoreType().equals("ctf") &&
					!plugin.pdata.playerHasFlag(player, sign.getLine(2).replaceAll(ChatColor.RED.toString(), "").replaceAll(ChatColor.BLUE.toString(), ""))){
				plugin.pdata.addPlayerFlags(player, sign.getLine(2).replaceAll(ChatColor.RED.toString(), "").replaceAll(ChatColor.BLUE.toString(), ""));
				player.sendMessage(ChatColor.AQUA + "[Minigames] " + 
						ChatColor.WHITE + sign.getLine(2).replaceAll(ChatColor.RED.toString(), "").replaceAll(ChatColor.BLUE.toString(), "") + " flag taken!");
				return true;
			}
		}
		else if(player.getItemInHand().getType() != Material.AIR)
			player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Your hand must be empty to use this sign!");
		return false;
	}

}
