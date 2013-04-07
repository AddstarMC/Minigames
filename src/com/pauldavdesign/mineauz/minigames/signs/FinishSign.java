package com.pauldavdesign.mineauz.minigames.signs;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.Minigames;

public class FinishSign implements MinigameSign {
	
	private static Minigames plugin = Minigames.plugin;

	@Override
	public String getName() {
		return "Finish";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.finish";
	}

	@Override
	public String getCreatePermissionMessage() {
		return "You do not have permission to create a Minigame Finish sign!";
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
		event.setLine(1, ChatColor.GREEN + "Finish");
		return true;
	}

	@Override
	public boolean signUse(Sign sign, Player player) {
		if(plugin.pdata.playerInMinigame(player) && player.getItemInHand().getType() == Material.AIR){
			Minigame minigame = plugin.pdata.getPlayersMinigame(player);

			if(minigame.isSpectator(player)){
				return false;
			}
			
			if(!minigame.getFlags().isEmpty()){
				if(((LivingEntity)player).isOnGround()){
					
					if(plugin.pdata.checkRequiredFlags(player, minigame.getName()).isEmpty()){
						plugin.pdata.endMinigame(player);
						plugin.pdata.partyMode(player);
					}
					else{
						List<String> requiredFlags = plugin.pdata.checkRequiredFlags(player, minigame.getName());
						String flags = "";
						int num = requiredFlags.size();
						
						for(int i = 0; i < num; i++){
							flags += requiredFlags.get(i);
							if(i != num - 1){
								flags += ", ";
							}
						}
						player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You still require the following flags:");
						player.sendMessage(ChatColor.GRAY + flags);
					}
				}
				return true;
			}
			else{
				if(((LivingEntity)player).isOnGround()){
					plugin.pdata.endMinigame(player);
					plugin.pdata.partyMode(player);
					return true;
				}
			}
		}
		else if(player.getItemInHand().getType() != Material.AIR){
			player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Your hand must be empty to use this sign!");
		}
		return false;
	}

}
