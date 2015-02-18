package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;

public class MenuItemSubMenu extends MenuItem{
	
	private Menu menu = null;
	
	public MenuItemSubMenu(String name, Material displayItem, Menu menu) {
		super(name, displayItem);
		this.menu = menu;
	}

	public MenuItemSubMenu(String name, List<String> description, Material displayItem, Menu menu) {
		super(name, description, displayItem);
		this.menu = menu;
	}
	
	@Override
	public ItemStack onClick(MinigamePlayer player){
		menu.displayMenu(player);
		return null;
	}
}
