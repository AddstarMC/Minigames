package com.pauldavdesign.mineauz.minigames;

import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class MinigameUtils {
	/**
	 * Returns the item stack from a number or name.
	 * @param item - The items name or ID.
	 * @param quantity - The number of said item
	 * @return - The ItemStack referred to in the parameter.
	 */
	public static ItemStack stringToItemStack(String item, int quantity){
		String itemName = "";
		int itemInt = 0;
		short itemData = 0;
		String[] split = null;
		
		if(item.matches("[0-9]+(:[0-9]+)?")){
			if(item.contains(":")){
				split = item.split(":");
				itemInt = Integer.parseInt(split[0]);
				itemData = Short.parseShort(split[1]);
			}
			else{
				itemInt = Integer.parseInt(item);
			}
		}
		else{
			if(item.contains(":")){
				split = item.split(":");
				itemName = split[0].toUpperCase();
				if(split[1].matches("[0-9]+")){
					itemData = Short.parseShort(split[1]);
				}
			}
			else{
				itemName = item.toUpperCase();
			}
		}
		
		ItemStack it = null;
		
		if(Material.getMaterial(itemName) != null){
			it = new ItemStack(Material.getMaterial(itemName), quantity, itemData);
		}
		else if(Material.getMaterial(itemInt) != null){
			it = new ItemStack(Material.getMaterial(itemInt), quantity, itemData);
		}
		return it;
	}
	
	public static String getItemStackName(ItemStack item){
		return item.getType().toString().toLowerCase().replace("_", " ");
	}
}
