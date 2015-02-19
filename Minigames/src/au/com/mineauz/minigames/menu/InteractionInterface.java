package au.com.mineauz.minigames.menu;

import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;

public interface InteractionInterface {
	ItemStack interact(MinigamePlayer player, ItemStack object);
}
