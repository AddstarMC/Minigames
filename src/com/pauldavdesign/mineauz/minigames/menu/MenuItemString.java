package com.pauldavdesign.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;

public class MenuItemString extends MenuItem{
	
	private Callback<String> str;

	public MenuItemString(String name, Material displayItem, Callback<String> str) {
		super(name, displayItem);
		this.str = str;
		updateDescription();
	}

	public MenuItemString(String name, List<String> description, Material displayItem, Callback<String> str) {
		super(name, description, displayItem);
		this.str = str;
		updateDescription();
	}
	
	public void updateDescription(){
		List<String> description = null;
		String setting = str.getValue();
		if(setting == null)
			setting = "Not Set";
		
		if(getDescription() != null){
			description = getDescription();
			String desc = getDescription().get(0);
			
			if(desc.startsWith(ChatColor.GREEN.toString()))
				description.set(0, ChatColor.GREEN.toString() + setting);
			else
				description.add(0, ChatColor.GREEN.toString() + setting);
		}
		else{
			description = new ArrayList<String>();
			description.add(ChatColor.GREEN.toString() + setting);
		}
		
		setDescription(description);
	}
	
	@Override
	public ItemStack onDoubleClick(){
		MinigamePlayer ply = getContainer().getViewer();
		ply.setNoClose(true);
		ply.getPlayer().closeInventory();
		ply.sendMessage("Enter string value into chat for " + getName() + ", the menu will automatically reopen in 20s if nothing is entered.", null);
		ply.setManualEntry(this);
		getContainer().startReopenTimer(20);
		
		return null;
	}
	
	@Override
	public void checkValidEntry(String entry){
		str.setValue(entry);
		updateDescription();
		getContainer().cancelReopenTimer();
		getContainer().displayMenu(getContainer().getViewer());
	}
	
	Callback<String> getString(){
		return str;
	}
}
