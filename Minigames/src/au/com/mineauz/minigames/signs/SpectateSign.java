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

public class SpectateSign implements MinigameSign {
	
	private Minigames plugin = Minigames.plugin;

	@Override
	public String getName() {
		return "Spectate";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.spectate";
	}

	@Override
	public String getCreatePermissionMessage() {
		return MinigameUtils.getLang("sign.spectate.createPermission");
	}

	@Override
	public String getUsePermission() {
		return "minigame.sign.use.spectate";
	}

	@Override
	public String getUsePermissionMessage() {
		return MinigameUtils.getLang("sign.spectate.usePermission");
	}

	@Override
	public boolean signCreate(SignChangeEvent event) {
		if(plugin.mdata.hasMinigame(event.getLine(2))){
			event.setLine(1, ChatColor.GREEN + "Spectate");
			event.setLine(2, plugin.mdata.getMinigame(event.getLine(2)).getName(false));
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
			
			// Spectate the game
			player.spectateMinigame(minigame);

		} catch (IllegalArgumentException e) {
			player.sendMessage(e.getMessage(), "error");
		} catch (IllegalStateException e) {
			player.sendMessage(e.getMessage(), "error");
		}
			
		return false;
	}

	@Override
	public void signBreak(Sign sign, MinigamePlayer player) {
		
	}

}
