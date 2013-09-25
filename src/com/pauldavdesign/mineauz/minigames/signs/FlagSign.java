package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;

public class FlagSign implements MinigameSign {

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
		return MinigameUtils.getLang("sign.flag.createPermission");
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
				event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.flag.invalidSyntax") + " red, blue and neutral.");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean signUse(Sign sign, MinigamePlayer player) {
		if(player.getPlayer().getItemInHand().getType() == Material.AIR && player.isInMinigame()){
			Minigame mgm = player.getMinigame();

			if(mgm.isSpectator(player)){
				return false;
			}
			if(!sign.getLine(2).isEmpty() && ((LivingEntity)player.getPlayer()).isOnGround() && 
					!mgm.getScoreType().equals("ctf") &&
					!player.hasFlag(sign.getLine(2).replaceAll(ChatColor.RED.toString(), "").replaceAll(ChatColor.BLUE.toString(), ""))){
				player.addFlag(sign.getLine(2).replaceAll(ChatColor.RED.toString(), "").replaceAll(ChatColor.BLUE.toString(), ""));
				player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + 
						MinigameUtils.formStr("sign.flag.taken", sign.getLine(2).replaceAll(ChatColor.RED.toString(), "").replaceAll(ChatColor.BLUE.toString(), "")) );
				return true;
			}
		}
		else if(player.getPlayer().getItemInHand().getType() != Material.AIR)
			player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.emptyHand"));
		return false;
	}

}
