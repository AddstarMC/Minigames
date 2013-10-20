package com.pauldavdesign.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuItem {
	private ItemStack displayItem = null;
	private Menu container = null;
	
	public MenuItem(String name, Material displayItem){
		this.displayItem = new ItemStack(displayItem);
		
		ItemMeta meta = this.displayItem.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		this.displayItem.setItemMeta(meta);
	}
	
	public MenuItem(String name, List<String> description, Material displayItem){
		this.displayItem = new ItemStack(displayItem);
		
		ItemMeta meta = this.displayItem.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		meta.setLore(description);
		this.displayItem.setItemMeta(meta);
	}
	
	public void setDescription(List<String> description){
		ItemMeta meta = displayItem.getItemMeta();
		
		meta.setLore(description);
		displayItem.setItemMeta(meta);
	}
	
	public List<String> getDescription(){
		return displayItem.getItemMeta().getLore();
	}
	
	public String getName(){
		return displayItem.getItemMeta().getDisplayName();
	}
	
	public ItemStack getItem(){
		return displayItem;
	}
	
	public ItemStack onClick(){
		//Do stuff
		return getItem();
	}
	
	public ItemStack onRightClick(){
		//Do stuff
		return getItem();
	}
	
	public ItemStack onShiftClick(){
		//Do stuff
		return getItem();
	}
	
	public ItemStack onShiftRightClick(){
		//Do stuff
		return getItem();
	}
	
	public ItemStack onDoubleClick(){
		//Do Stuff
		return getItem();
	}
	
	public void checkValidEntry(String entry){
		//Do Stuff
	}
	
	public void setContainer(Menu container){
		this.container = container;
	}
	
	public Menu getContainer(){
		return container;
	}
}
