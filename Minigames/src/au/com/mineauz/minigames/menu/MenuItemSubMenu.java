package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;

public class MenuItemSubMenu extends MenuItem{
	
	private Menu menu = null;
	
	public MenuItemSubMenu(String name, Material displayItem, Menu menu) {
		super(name, displayItem);
		this.menu = menu;
	}

	public MenuItemSubMenu(String name, String description, Material displayItem, Menu menu) {
		super(name, description, displayItem);
		this.menu = menu;
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		menu.displayMenu(player);
	}
}
