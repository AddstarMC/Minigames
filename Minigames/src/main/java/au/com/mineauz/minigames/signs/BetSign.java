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

public class BetSign implements MinigameSign{
	
	private static Minigames plugin = Minigames.plugin;

	@Override
	public String getName() {
		return "Bet";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.bet";
	}

	@Override
	public String getCreatePermissionMessage() {
		return MinigameUtils.getLang("sign.bet.createPermission");
	}

	@Override
	public String getUsePermission() {
		return "minigame.sign.use.bet";
	}

	@Override
	public String getUsePermissionMessage() {
		return MinigameUtils.getLang("sign.bet.usePermission");
	}

	@Override
	public boolean signCreate(SignChangeEvent event) {
		if(plugin.mdata.hasMinigame(event.getLine(2))){
			event.setLine(1, ChatColor.GREEN + "Bet");
			event.setLine(2, plugin.mdata.getMinigame(event.getLine(2)).getName(false));
			if(event.getLine(3).matches("[0-9]+")){
				event.setLine(3, "$" + event.getLine(3));
			}
			return true;
		}
		event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("minigame.error.noMinigameName", event.getLine(2)));
		return false;
	}

	@Override
	public boolean signUse(Sign sign, MinigamePlayer player) {
		Minigame mgm = plugin.mdata.getMinigame(sign.getLine(2));
		if (mgm != null) {
			boolean invOk = true;
			boolean fullInv;
			boolean moneyBet = sign.getLine(3).startsWith("$");
			
			if (plugin.getConfig().getBoolean("requireEmptyInventory")) {
				fullInv = true;
				ItemStack[] contents = player.getPlayer().getInventory().getContents();
				for (int i = 0; i < contents.length; ++i) {
					// Non money bets can hold an item
					if (!moneyBet && i == player.getPlayer().getInventory().getHeldItemSlot()) {
						continue;
					}
					
					if (contents[i] != null) {
						invOk = false;
						break;
					}
				}
				
				for (ItemStack item : player.getPlayer().getInventory().getArmorContents()) {
					if (item != null && item.getType() != Material.AIR) {
						invOk = false;
						break;
					}
				}
			} else {
				fullInv = false;
				invOk = (moneyBet ? player.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR : player.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR);
			}
			
			if(invOk){
				if(mgm.isEnabled() && (!mgm.getUsePermissions() || player.getPlayer().hasPermission("minigame.join." + mgm.getName(false).toLowerCase()))){
					if(mgm.isSpectator(player)){
						return false;
					}
					
					if(!sign.getLine(3).startsWith("$")){
						plugin.pdata.joinMinigame(player, plugin.mdata.getMinigame(sign.getLine(2)), true, 0.0);
					}
					else{
						if(plugin.hasEconomy()){
							Double bet = Double.parseDouble(sign.getLine(3).replace("$", ""));
							plugin.pdata.joinMinigame(player, plugin.mdata.getMinigame(sign.getLine(2)), true, bet);
							return true;
						}
						else{
							player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noVault"));
						}
					}
				}
				else if(!mgm.isEnabled()){
					player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.notEnabled"));
				}
				else if(mgm.getUsePermissions()){
					player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("minigame.error.noPermission", "minigame.join." + mgm.getName(false).toLowerCase()));
				}
			}
			else if(!moneyBet){
				if(fullInv && player.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) {
					player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.emptyInv"));
				}
				else {
					player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.bet.noBet"));
				}
			}
			else {
				if(fullInv) {
					player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.emptyInv"));
				}
				else {
					player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.emptyHand"));
				}
			}
		}
		else{
			player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noMinigame"));
		}
		return false;
	}

	@Override
	public void signBreak(Sign sign, MinigamePlayer player) {
		
	}

}
