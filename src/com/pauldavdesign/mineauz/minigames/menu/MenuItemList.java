package com.pauldavdesign.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;

public class MenuItemList extends MenuItem{
	
	private Callback<String> value = null;
	private List<String> options = null;
	
	public MenuItemList(String name, Material displayItem, Callback<String> value, List<String> options) {
		super(name, displayItem);
		this.value = value;
		this.options = options;
		updateDescription();
	}
	
	public MenuItemList(String name, List<String> description, Material displayItem, Callback<String> value, List<String> options) {
		super(name, description, displayItem);
		this.value = value;
		this.options = options;
		updateDescription();
	}
	
	public void updateDescription(){
		List<String> description = null;
		if(getDescription() != null){
			description = getDescription();
			String desc = ChatColor.stripColor(getDescription().get(0));
			
			if(options.contains(desc)){
				description.set(0, ChatColor.GREEN.toString() + value.getValue());
			}
			else
				description.add(0, ChatColor.GREEN.toString() + value.getValue());
		}
		else{
			description = new ArrayList<String>();
			description.add(ChatColor.GREEN.toString() + value.getValue());
		}
		
		setDescription(description);
	}
	
	@Override
	public ItemStack onClick(){
		int ind = options.lastIndexOf(value.getValue());
		ind++;
		if(ind == options.size())
			ind = 0;
		
		value.setValue(options.get(ind));
		updateDescription();
		
		return getItem();
	}
	
	@Override
	public ItemStack onRightClick(){
		int ind = options.lastIndexOf(value.getValue());
		ind--;
		if(ind == -1)
			ind = options.size() - 1;
		
		value.setValue(options.get(ind));
		updateDescription();
		
		return getItem();
	}
	
	@Override
	public ItemStack onDoubleClick(){
		MinigamePlayer ply = getContainer().getViewer();
		ply.setNoClose(true);
		ply.getPlayer().closeInventory();
		ply.sendMessage("Enter the name of the option into chat for " + getName() + ", the menu will automatically reopen in 10s if nothing is entered.", null);
		ply.setManualEntry(this);
		ply.sendMessage("Possible Options: " + MinigameUtils.listToString(options));

		getContainer().startReopenTimer(10);
		return null;
	}
	
	@Override
	public void checkValidEntry(String entry){
		entry = entry.toLowerCase();
		
		if(options.contains(entry)){
			value.setValue(entry);
			updateDescription();
			
			getContainer().cancelReopenTimer();
			getContainer().displayMenu(getContainer().getViewer());
			return;
		}
		getContainer().cancelReopenTimer();
		getContainer().displayMenu(getContainer().getViewer());
		
		getContainer().getViewer().sendMessage("Invalid value entry!", "error");
	}
}
