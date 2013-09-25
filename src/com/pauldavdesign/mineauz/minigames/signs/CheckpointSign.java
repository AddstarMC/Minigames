package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
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
		return MinigameUtils.getLang("sign.checkpoint.createPermission");
	}

	@Override
	public String getUsePermission() {
		return "minigame.sign.use.checkpoint";
	}

	@Override
	public String getUsePermissionMessage() {
		return MinigameUtils.getLang("sign.checkpoint.usePermission");
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
	public boolean signUse(Sign sign, MinigamePlayer player) {
		if((player.isInMinigame() || (!player.isInMinigame() && sign.getLine(2).equals(ChatColor.BLUE + "Global"))) 
				&& player.getPlayer().getItemInHand().getType() == Material.AIR){
			if(player.isInMinigame() && player.getMinigame().isSpectator(player)){
				return false;
			}
			if(((LivingEntity)player.getPlayer()).isOnGround()){
				Location newloc = player.getPlayer().getLocation();
				if(!sign.getLine(2).equals(ChatColor.BLUE + "Global")){
					player.setCheckpoint(newloc);
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
				
				player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.checkpoint.set"));
				return true;
			}
			else{
				player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.checkpoint.fail"));
			}
		}
		else
			player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.emptyHand"));
		return false;
	}

}
