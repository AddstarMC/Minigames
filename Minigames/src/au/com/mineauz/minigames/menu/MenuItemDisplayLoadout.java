package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;

public class MenuItemDisplayLoadout extends MenuItem{
	
	private PlayerLoadout loadout = null;
	private Minigame mgm = null;
	private boolean allowDelete = true;
	
	public MenuItemDisplayLoadout(String name, Material displayItem, PlayerLoadout loadout, Minigame minigame) {
		super(name, displayItem);
		this.loadout = loadout;
		mgm = minigame;
		if(!loadout.isDeleteable())
			allowDelete = false;
	}
	
	public MenuItemDisplayLoadout(String name, Material displayItem, PlayerLoadout loadout) {
		super(name, displayItem);
		this.loadout = loadout;
		if(!loadout.isDeleteable())
			allowDelete = false;
	}

	public MenuItemDisplayLoadout(String name, String description, Material displayItem, PlayerLoadout loadout, Minigame minigame) {
		super(name, description, displayItem);
		this.loadout = loadout;
		mgm = minigame;
		if(!loadout.isDeleteable())
			allowDelete = false;
	}

	public MenuItemDisplayLoadout(String name, String description, Material displayItem, PlayerLoadout loadout) {
		super(name, description, displayItem);
		this.loadout = loadout;
		if(!loadout.isDeleteable())
			allowDelete = false;
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		Menu loadoutMenu = loadout.createLoadoutMenu();
		loadoutMenu.displayMenu(player);
	}
	
	@Override
	public void onShiftRightClick(MinigamePlayer player){
		if(allowDelete){
			beginManualEntry(player, "Delete the " + loadout.getName(false) + " loadout from " + getName() + "? Type \"Yes\" to confirm.", 10);;
			player.sendMessage("The menu will automatically reopen in 10s if nothing is entered.");
		}
	}
	
	@Override
	public void checkValidEntry(MinigamePlayer player, String entry){
		if(entry.equalsIgnoreCase("yes")){
			String loadoutName = loadout.getName(false);
			if(mgm != null)
				mgm.getModule(LoadoutModule.class).deleteLoadout(loadoutName);
			else
				Minigames.plugin.mdata.deleteLoadout(loadoutName);
			remove();
			
			player.sendMessage(loadoutName + " has been deleted.", MessageType.Normal);
			return;
		}
		player.sendMessage(loadout.getName(false) + " was not deleted.", MessageType.Error);
	}
	
	public void setAllowDelete(boolean bool){
		allowDelete = bool;
	}
}
