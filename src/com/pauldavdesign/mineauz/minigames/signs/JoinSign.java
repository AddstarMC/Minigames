package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
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
		return MinigameUtils.getLang("sign.join.createPermission");
	}

	@Override
	public String getUsePermission() {
		return "minigame.sign.use.join";
	}

	@Override
	public String getUsePermissionMessage() {
		return MinigameUtils.getLang("sign.join.usePermission");
	}

	@Override
	public boolean signCreate(SignChangeEvent event) {
		if(plugin.mdata.hasMinigame(event.getLine(2))){
			event.setLine(1, ChatColor.GREEN + "Join");
			event.setLine(2, plugin.mdata.getMinigame(event.getLine(2)).getName());
			if(Minigames.plugin.hasEconomy()){
				if(!event.getLine(3).isEmpty() && !event.getLine(3).matches("\\$?[0-9]+(.[0-9]{2})?")){
					event.getPlayer().sendMessage(ChatColor.RED + MinigameUtils.getLang("sign.join.invalidMoney"));
					return false;
				}
				else if(event.getLine(3).matches("[0-9]+(.[0-9]{2})?")){
					event.setLine(3, "$" + event.getLine(3));
				}
			}
			else{
				event.setLine(3, "");
				event.getPlayer().sendMessage(ChatColor.RED + MinigameUtils.getLang("minigame.error.noVault"));
			}
			return true;
		}
		event.getPlayer().sendMessage(ChatColor.RED + MinigameUtils.formStr("minigame.error.noMinigameName", event.getLine(2)));
		return false;
	}

	@Override
	public boolean signUse(Sign sign, MinigamePlayer player) {
		if(player.getPlayer().getItemInHand().getType() == Material.AIR && !player.isInMinigame()){
			Minigame mgm = plugin.mdata.getMinigame(sign.getLine(2));
			if(mgm != null && (!mgm.getUsePermissions() || player.getPlayer().hasPermission("minigame.join." + mgm.getName().toLowerCase()))){
				if(mgm.isEnabled()){
					if(!sign.getLine(3).isEmpty() && Minigames.plugin.hasEconomy()){
						double amount = Double.parseDouble(sign.getLine(3).replace("$", ""));
						if(Minigames.plugin.getEconomy().getBalance(player.getName()) >= amount){
							Minigames.plugin.getEconomy().withdrawPlayer(player.getName(), amount);
						}
						else{
							player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.join.notEnoughMoney"));
							return false;
						}
					}
					plugin.pdata.joinMinigame(player, mgm);
					return true;
				}
				else if(!mgm.isEnabled()){
					player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.notEnabled"));
				}
			}
			else if(mgm == null){
				player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noMinigame"));
			}
			else if(mgm.getUsePermissions()){
				player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("minigame.error.noPermission", "minigame.join." + mgm.getName().toLowerCase()));
			}
		}
		else if(!player.isInMinigame())
			player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.emptyHand"));
		return false;
	}

}
