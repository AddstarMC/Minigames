package com.pauldavdesign.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.PlayerLoadout;

public class MenuItemDisplayLoadout extends MenuItem{
	
	private PlayerLoadout loadout = null;
	private Minigame mgm = null;
	private boolean allowDelete = true;
	
	public MenuItemDisplayLoadout(String name, Material displayItem, PlayerLoadout loadout, Minigame minigame) {
		super(name, displayItem);
		this.loadout = loadout;
		mgm = minigame;
	}

	public MenuItemDisplayLoadout(String name, List<String> description, Material displayItem, PlayerLoadout loadout, Minigame minigame) {
		super(name, description, displayItem);
		this.loadout = loadout;
		mgm = minigame;
	}
	
	@Override
	public ItemStack onClick(){
		Menu loadoutMenu = new Menu(5, loadout.getName(), getContainer().getViewer());
		
		loadoutMenu.setAllowModify(true);
		loadoutMenu.setPreviousPage(getContainer());

		loadoutMenu.addItem(new MenuItem("Edit Potion Effects", Material.POTION), 43); //TODO: Potion effect button
		loadoutMenu.addItem(new MenuItemSaveLoadout("Save Loadout", Material.REDSTONE_TORCH_ON, loadout), 44);
		
		for(int i = 36; i < 43; i++){
			loadoutMenu.addItem(new MenuItem("blank", null), i);
		}
		loadoutMenu.displayMenu(getContainer().getViewer());
		
		int count = 0;
		for(ItemStack item : loadout.getItems()){
			if(count == 36) break;
			loadoutMenu.addItemStack(item, count);
			count++;
		}
		
		return null;
	}
	
	@Override
	public ItemStack onRightClick(){
		if(allowDelete){
			MinigamePlayer ply = getContainer().getViewer();
			ply.setNoClose(true);
			ply.getPlayer().closeInventory();
			ply.sendMessage("Delete the " + loadout.getName() + " loadout from " + getName() + "? Type \"Yes\" to confirm.", null);
			ply.sendMessage("The menu will automatically reopen in 10s if nothing is entered.");
			ply.setManualEntry(this);
			getContainer().startReopenTimer(10);
		}
		
		return null;
	}
	
	@Override
	public void checkValidEntry(String entry){
		if(entry.equalsIgnoreCase("yes")){
			String loadoutName = loadout.getName();
			mgm.deleteLoadout(loadoutName);
			getContainer().removeItem(getSlot());
			getContainer().cancelReopenTimer();
			getContainer().displayMenu(getContainer().getViewer());
			getContainer().getViewer().sendMessage(loadoutName + " has been deleted from " + mgm.getName(), null);
			return;
		}
		getContainer().getViewer().sendMessage(loadout.getName() + " was not deleted.", "error");
		getContainer().cancelReopenTimer();
		getContainer().displayMenu(getContainer().getViewer());
	}
	
	public void setAllowDelete(boolean bool){
		allowDelete = bool;
	}
}
