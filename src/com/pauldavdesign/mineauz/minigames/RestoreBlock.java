package com.pauldavdesign.mineauz.minigames;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RestoreBlock {
	private String name;
	private Material block;
	private ItemStack[] items;
	private Location location = null;
	
	public RestoreBlock(String name, Material block, Location loc){
		this.name = name;
		this.block = block;
		setLocation(loc);
	}
	
	public RestoreBlock(String name, Material block, ItemStack[] items){
		this.name = name;
		this.block = block;
		this.items = items;
	}

	public Material getBlock(){
		return block;
	}

	public void setBlock(Material block){
		this.block = block;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	public ItemStack[] getItems(){
		return items;
	}

	public void setItems(ItemStack[] items){
		this.items = items;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
