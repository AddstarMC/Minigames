package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;

public class MenuItemWhitelistBlock extends MenuItem{
	
	private List<Material> whitelist;

	public MenuItemWhitelistBlock(Material displayItem, List<Material> whitelist) {
		super(MinigameUtils.capitalize(displayItem.toString().replace("_", " ")), displayItem);
		setDescription(MinigameUtils.stringToList("Right Click to remove"));
		this.whitelist = whitelist;
	}
	
	@Override
	public ItemStack onRightClick(MinigamePlayer player) {
		whitelist.remove(getItem().getType());
		remove();
		return null;
	}
}
