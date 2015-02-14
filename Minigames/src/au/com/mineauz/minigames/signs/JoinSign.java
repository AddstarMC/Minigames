package au.com.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;

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
			event.setLine(2, plugin.mdata.getMinigame(event.getLine(2)).getName(false));
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
		if (player.isInMinigame()) {
			return false;
		}
		
		boolean invOk = true;
		boolean fullInv;
		if (plugin.getConfig().getBoolean("requireEmptyInventory")) {
			fullInv = true;
			for (ItemStack item : player.getPlayer().getInventory().getContents()) {
				if (item != null) {
					System.out.println("Found: " + item);
					invOk = false;
					break;
				}
			}
			
			for (ItemStack item : player.getPlayer().getInventory().getArmorContents()) {
				if (item != null && item.getType() != Material.AIR) {
					System.out.println("Found armor: " + item);
					invOk = false;
					break;
				}
			}
		} else {
			fullInv = false;
			invOk = player.getPlayer().getItemInHand().getType() == Material.AIR;
		}
		if(invOk){
			Minigame mgm = plugin.mdata.getMinigame(sign.getLine(2));
			if(mgm != null && (!mgm.getUsePermissions() || player.getPlayer().hasPermission("minigame.join." + mgm.getName(false).toLowerCase()))){
				if(mgm.isEnabled()){
					if(!sign.getLine(3).isEmpty() && Minigames.plugin.hasEconomy()){
						double amount = Double.parseDouble(sign.getLine(3).replace("$", ""));
						if(Minigames.plugin.getEconomy().getBalance(player.getPlayer().getPlayer()) >= amount){
							Minigames.plugin.getEconomy().withdrawPlayer(player.getPlayer().getPlayer(), amount);
						}
						else{
							player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.join.notEnoughMoney"));
							return false;
						}
					}
					plugin.pdata.joinMinigame(player, mgm, false, 0.0);
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
				player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("minigame.error.noPermission", "minigame.join." + mgm.getName(false).toLowerCase()));
			}
		}
		else if(!MinigameUtils.isMinigameTool(player.getPlayer().getItemInHand())) {
			if (fullInv) {
				player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.emptyInv"));
			} else {
				player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.emptyHand"));
			}
		}
			
		return false;
	}

	@Override
	public void signBreak(Sign sign, MinigamePlayer player) {
		
	}

}
