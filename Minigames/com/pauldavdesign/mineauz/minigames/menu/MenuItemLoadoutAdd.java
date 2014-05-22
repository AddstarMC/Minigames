package com.pauldavdesign.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.PlayerLoadout;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class MenuItemLoadoutAdd extends MenuItem{
	
	private Map<String, PlayerLoadout> loadouts;
	private Minigame minigame = null;

	public MenuItemLoadoutAdd(String name, Material displayItem, Map<String, PlayerLoadout> loadouts, Minigame mgm) {
		super(name, displayItem);
		this.loadouts = loadouts;
		this.minigame = mgm;
	}

	public MenuItemLoadoutAdd(String name, List<String> description, Material displayItem, Map<String, PlayerLoadout> loadouts, Minigame mgm) {
		super(name, description, displayItem);
		this.loadouts = loadouts;
		this.minigame = mgm;
	}

	public MenuItemLoadoutAdd(String name, Material displayItem, Map<String, PlayerLoadout> loadouts) {
		super(name, displayItem);
		this.loadouts = loadouts;
	}

	public MenuItemLoadoutAdd(String name, List<String> description, Material displayItem, Map<String, PlayerLoadout> loadouts) {
		super(name, description, displayItem);
		this.loadouts = loadouts;
	}
	
	@Override
	public ItemStack onClick(){
		MinigamePlayer ply = getContainer().getViewer();
		ply.setNoClose(true);
		ply.getPlayer().closeInventory();
		ply.sendMessage("Enter a name for the new Loadout, the menu will automatically reopen in 10s if nothing is entered.", null);
		ply.setManualEntry(this);

		getContainer().startReopenTimer(30);
		return null;
	}
	
	@Override
	public void checkValidEntry(String entry){
		entry = entry.replace(" ", "_");
		if(!loadouts.keySet().contains(entry)){
			for(int i = 0; i < 45; i++){
				if(!getContainer().hasMenuItem(i)){
					PlayerLoadout loadout = new PlayerLoadout(entry);
					loadouts.put(entry, loadout);
					List<String> des = new ArrayList<String>();
					des.add("Shift + Right Click to Delete");
					if(minigame != null)
						getContainer().addItem(new MenuItemDisplayLoadout(entry, des, Material.DIAMOND_SWORD, loadout, minigame), i);
					else
						getContainer().addItem(new MenuItemDisplayLoadout(entry, des, Material.DIAMOND_SWORD, loadout), i);
					break;
				}
			}
			
			getContainer().cancelReopenTimer();
			getContainer().displayMenu(getContainer().getViewer());
			return;
		}
		getContainer().cancelReopenTimer();
		getContainer().displayMenu(getContainer().getViewer());
		
		getContainer().getViewer().sendMessage("A Loadout already exists by the name \"" + entry + "\".", "error");
	}
}
