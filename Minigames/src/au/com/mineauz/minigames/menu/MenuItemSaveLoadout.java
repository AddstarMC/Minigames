package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.PlayerLoadout;

public class MenuItemSaveLoadout extends MenuItem{
	
	private PlayerLoadout loadout = null;
	private Menu altMenu = null;
	
	public MenuItemSaveLoadout(String name, Material displayItem, PlayerLoadout loadout) {
		super(name, displayItem);
		this.loadout = loadout;
	}
	
	public MenuItemSaveLoadout(String name, String description, Material displayItem, PlayerLoadout loadout) {
		super(name, description, displayItem);
		this.loadout = loadout;
	}
	
	public MenuItemSaveLoadout(String name, Material displayItem, PlayerLoadout loadout, Menu altMenu) {
		super(name, displayItem);
		this.loadout = loadout;
		this.altMenu = altMenu;
	}
	
	public MenuItemSaveLoadout(String name, String description, Material displayItem, PlayerLoadout loadout, Menu altMenu) {
		super(name, description, displayItem);
		this.loadout = loadout;
		this.altMenu = altMenu;
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		ItemStack[] items = ((MenuPageInventory)getContainer().getFirstPage()).getInventory();
		loadout.clearLoadout();
		
		for(int i = 0; i < 36; i++){
			if(items[i] != null)
				loadout.addItem(items[i], i);
		}
		for(int i = 36; i < 40; i++){
			if(items[i] != null){
				if(i == 36)
					loadout.addItem(items[i], 103);
				else if(i == 37)
					loadout.addItem(items[i], 102);
				else if(i == 38)
					loadout.addItem(items[i], 101);
				else if(i == 39)
					loadout.addItem(items[i], 100);
			}
		}
		player.sendMessage("Saved the '" + loadout.getName(false) + "' loadout.", MessageType.Normal);
		if(altMenu == null)
			player.showPreviousMenu();
		else
			altMenu.displayMenu(player);
	}
}
