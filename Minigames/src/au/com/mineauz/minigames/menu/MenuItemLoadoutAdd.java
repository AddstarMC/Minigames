package au.com.mineauz.minigames.menu;

import java.util.Map;

import org.bukkit.Material;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.minigame.Minigame;

public class MenuItemLoadoutAdd extends MenuItem{
	
	private Map<String, PlayerLoadout> loadouts;
	private Minigame minigame = null;

	public MenuItemLoadoutAdd(String name, Material displayItem, Map<String, PlayerLoadout> loadouts, Minigame mgm) {
		super(name, displayItem);
		this.loadouts = loadouts;
		this.minigame = mgm;
	}

	public MenuItemLoadoutAdd(String name, String description, Material displayItem, Map<String, PlayerLoadout> loadouts, Minigame mgm) {
		super(name, description, displayItem);
		this.loadouts = loadouts;
		this.minigame = mgm;
	}

	public MenuItemLoadoutAdd(String name, Material displayItem, Map<String, PlayerLoadout> loadouts) {
		super(name, displayItem);
		this.loadouts = loadouts;
	}

	public MenuItemLoadoutAdd(String name, String description, Material displayItem, Map<String, PlayerLoadout> loadouts) {
		super(name, description, displayItem);
		this.loadouts = loadouts;
	}
	
	@Override
	public void onClick(MinigamePlayer player) {
		beginManualEntry(player, "Enter a name for the new Loadout, the menu will automatically reopen in 10s if nothing is entered.", 10);
	}
	
	@Override
	public void checkValidEntry(MinigamePlayer player, String entry){
		entry = entry.replace(" ", "_");
		if(!loadouts.keySet().contains(entry)) {
			PlayerLoadout loadout = new PlayerLoadout(entry);
			loadouts.put(entry, loadout);
			if(minigame != null)
				getContainer().addItem(new MenuItemDisplayLoadout(entry, "Shift + Right Click to Delete", Material.DIAMOND_SWORD, loadout, minigame));
			else
				getContainer().addItem(new MenuItemDisplayLoadout(entry, "Shift + Right Click to Delete", Material.DIAMOND_SWORD, loadout));
			return;
		}
		
		player.sendMessage("A Loadout already exists by the name \"" + entry + "\".", MessageType.Error);
	}
}
