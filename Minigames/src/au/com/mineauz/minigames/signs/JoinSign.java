package au.com.mineauz.minigames.signs;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MessageType;
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
		
		try {
			Minigame minigame = plugin.mdata.getMinigame(sign.getLine(2));
			if (minigame == null) {
				throw new IllegalArgumentException(MinigameUtils.getLang("minigame.error.noMinigame"));
			}
			
			// Ignore the minigame tool
			if(MinigameUtils.isMinigameTool(player.getPlayer().getItemInHand())) {
				return true;
			}
			
			// Make sure inventory constraints are met
			if (plugin.getConfig().getBoolean("requireEmptyInventory")) {
				for (ItemStack item : player.getPlayer().getInventory().getContents()) {
					if (item != null) {
						throw new IllegalStateException(MinigameUtils.getLang("sign.emptyInv"));
					}
				}
				
				for (ItemStack item : player.getPlayer().getInventory().getArmorContents()) {
					if (item != null && item.getType() != Material.AIR) {
						throw new IllegalStateException(MinigameUtils.getLang("sign.emptyInv"));
					}
				}
			} else {
				if (player.getPlayer().getItemInHand().getType() != Material.AIR) {
					throw new IllegalStateException(MinigameUtils.getLang("sign.emptyHand"));
				}
			}
			
			// Handle payment
			double entryFee = 0;
			Economy economy = Minigames.plugin.getEconomy();
			
			if(!sign.getLine(3).isEmpty() && Minigames.plugin.hasEconomy()) {
				entryFee = Double.parseDouble(sign.getLine(3).replace("$", ""));
				
				if (!economy.has(player.getPlayer(), entryFee)) {
					throw new IllegalStateException(MinigameUtils.getLang("sign.join.notEnoughMoney"));
				}
			}
			
			// Join the game
			if (player.joinMinigame(minigame)) {
				if (entryFee > 0) {
					economy.withdrawPlayer(player.getPlayer(), entryFee);
				}
			}
		} catch (IllegalArgumentException e) {
			player.sendMessage(e.getMessage(), MessageType.Error);
		} catch (IllegalStateException e) {
			player.sendMessage(e.getMessage(), MessageType.Error);
		}
			
		return false;
	}

	@Override
	public void signBreak(Sign sign, MinigamePlayer player) {
		
	}

}
