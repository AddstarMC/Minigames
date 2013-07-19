package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;

public class LoadoutSign implements MinigameSign {
	
	private static Minigames plugin = Minigames.plugin;

	@Override
	public String getName() {
		return "Loadout";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.loadout";
	}

	@Override
	public String getCreatePermissionMessage() {
		return "You do not have permission to create a Minigames loadout sign!";
	}

	@Override
	public String getUsePermission() {
		return "minigame.sign.use.loadout";
	}

	@Override
	public String getUsePermissionMessage() {
		return "You do not have permission to use a Minigames loadout sign!";
	}

	@Override
	public boolean signCreate(SignChangeEvent event) {
		event.setLine(1, ChatColor.GREEN + "Loadout");
		return true;
	}

	@Override
	public boolean signUse(Sign sign, MinigamePlayer player) {
		if(player.getPlayer().getItemInHand().getType() == Material.AIR && player.isInMinigame()){
			Minigame mgm = player.getMinigame();
			if(mgm == null || mgm.isSpectator(player)){
				return false;
			}
			
			if(mgm.hasLoadout(sign.getLine(2))){
				if(!mgm.getLoadout(sign.getLine(2)).getUsePermissions() || player.getPlayer().hasPermission("minigame.loadout." + sign.getLine(2).toLowerCase())){
					mgm.setPlayersLoadout(player, mgm.getLoadout(sign.getLine(2)));
					player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You have been equipped with the " + sign.getLine(2) + " loadout.");
					
					if(mgm.getType().equals("sp") || (mgm.getMpTimer() != null && mgm.getMpTimer().getStartWaitTimeLeft() == 0)){
						if(sign.getLine(3).equalsIgnoreCase("respawn")){
							player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "This loadout will be equipped the next time you respawn.");
						}
						else{
							mgm.getLoadout(sign.getLine(2)).equiptLoadout(player);
						}
					}
					return true;
				}
				else{
					player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You don't have permission to use the " + sign.getLine(2) + " loadout.");
				}
			}
			else if(plugin.mdata.hasLoadout(sign.getLine(2))){
				if(!plugin.mdata.getLoadout(sign.getLine(2)).getUsePermissions() || player.getPlayer().hasPermission("minigame.loadout." + sign.getLine(2).toLowerCase())){
					mgm.setPlayersLoadout(player, plugin.mdata.getLoadout(sign.getLine(2)));
					player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You have been equipped with the " + sign.getLine(2) + " loadout.");

					if(mgm.getType().equals("sp") || (mgm.getMpTimer() != null && mgm.getMpTimer().getStartWaitTimeLeft() == 0)){
						if(sign.getLine(3).equalsIgnoreCase("respawn")){
							player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "This loadout will be equipped the next time you respawn.");
						}
						else{
							plugin.mdata.getLoadout(sign.getLine(2)).equiptLoadout(player);
						}
					}
					return true;
				}
				else{
					player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You don't have permission to use the " + sign.getLine(2) + " loadout.");
				}
			}
			else{
				player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "This loadout does not exist!");
			}
		}
		else if(player.getPlayer().getItemInHand().getType() != Material.AIR)
			player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Your hand must be empty to use this sign!");
		return false;
	}

}
