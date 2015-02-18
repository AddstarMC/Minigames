package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;

public class MenuItemBack extends MenuItem {
	private MenuSession previous;
	
	public MenuItemBack(MenuSession previous) {
		super("Back", Material.REDSTONE_TORCH_ON);
		this.previous = previous;
	}
	
	@Override
	public ItemStack onClick(MinigamePlayer player) {
		previous.current.displaySession(player, previous);
		return null;
	}

}
