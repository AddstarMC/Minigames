package au.com.mineauz.minigames.signs;

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
			
			boolean moneyBet = sign.getLine(3).startsWith("$");
			
			// Make sure inventory constraints are met
			if (plugin.getConfig().getBoolean("requireEmptyInventory")) {
				ItemStack[] contents = player.getPlayer().getInventory().getContents();
				for (int i = 0; i < contents.length; ++i) {
					// Non money bets can hold an item
					if (!moneyBet && i == player.getPlayer().getInventory().getHeldItemSlot()) {
						continue;
					}
					
					if (contents[i] != null) {
						throw new IllegalStateException(MinigameUtils.getLang("sign.emptyInv"));
					}
				}
				
				for (ItemStack item : player.getPlayer().getInventory().getArmorContents()) {
					if (item != null && item.getType() != Material.AIR) {
						throw new IllegalStateException(MinigameUtils.getLang("sign.emptyInv"));
					}
				}
			} else {
				if (moneyBet) {
					if (player.getPlayer().getItemInHand().getType() != Material.AIR) {
						throw new IllegalStateException(MinigameUtils.getLang("sign.emptyHand"));
					}
				} else {
					if (player.getPlayer().getItemInHand().getType() == Material.AIR) {
						throw new IllegalStateException(MinigameUtils.getLang("sign.bet.noBet"));
					}
				}
			}
			
			// Join game
			if (moneyBet) {
				if (!Minigames.plugin.hasEconomy()) {
					throw new IllegalArgumentException(MinigameUtils.getLang("minigame.error.noVault"));
				}
				
				double bet = Double.parseDouble(sign.getLine(3).replace("$", ""));
				
				player.joinMinigameWithBet(minigame, bet);
			} else {
				ItemStack bet = new ItemStack(player.getPlayer().getItemInHand());
				bet.setAmount(1);
				if (player.joinMinigameWithBet(minigame, bet)) {
					player.getPlayer().getInventory().removeItem(bet);
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
